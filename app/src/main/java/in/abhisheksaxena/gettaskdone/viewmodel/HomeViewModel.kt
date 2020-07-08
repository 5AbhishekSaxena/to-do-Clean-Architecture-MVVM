package `in`.abhisheksaxena.gettaskdone.viewmodel


import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.Result
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.Constants
import `in`.abhisheksaxena.gettaskdone.util.TasksFilterType
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.RuntimeException


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:06
 */

class HomeViewModel(
    private val tasksRepository: TasksRepository
) : BaseViewModel() {

    private val TAG = javaClass.name

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val ioDispatcher = Dispatchers.IO

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _tasks: LiveData<List<Task>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            //refreshData
        }
        tasksRepository.observeTasks().switchMap { filterTasks(it) }
    }
    val tasks: LiveData<List<Task>> = _tasks

    var tempTask: Task = Task()

    var hasMessageShown = false
    private var isNewTask = true

    private var currentFiltering = TasksFilterType.ALL_TASKS

    private var taskId: Long? = null

    private val _taskId = MutableLiveData<Long>()

    private var _currentTask = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }
    val currentTask: LiveData<Task?> = _currentTask

    private var _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _openTaskEvent = MutableLiveData<Event<Long>>()
    val openTaskEvent: LiveData<Event<Long>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private val _taskDeletedEvent = MutableLiveData<Event<Unit>>()
    val taskDeletedEvent: LiveData<Event<Unit>> = _taskDeletedEvent

    init {
        Log.e(TAG, "init current task: ${currentTask.value}")
        Log.e(TAG, "init temp task: $tempTask")
        //Log.e(TAG, "init taskid: $taskId")
    }

    fun start(taskId: Long?) {
        this.taskId = taskId
        if (taskId == null) {
            isNewTask = true
            return
        }

        isNewTask = false
        coroutineScope.launch {
            tasksRepository.getTask(taskId).let { result ->
                if (result is Result.Success) {
                    onTaskLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    private fun onTaskLoaded(task: Task) {
        _taskId.value = task.id
    }

    private fun onDataNotAvailable() {}


    private fun computeResult(taskResult: Result<Task>): Task? {
        return if (taskResult is Result.Success) {
            taskResult.data
        } else {
            showSnackbarMessage(R.string.loading_tasks_error)
            null
        }
    }

    fun saveTask() {
        if (currentTask.value != tempTask) {
            if (tempTask.title.isEmpty()) {
                showSnackbarMessage(R.string.title_empty)
                return
            } else if (tempTask.priority.isEmpty()) {
                showSnackbarMessage(R.string.priority_empty)
                return
            }

            if (tempTask.details.isEmpty())
                tempTask.details = ""

            //if (_viewState.value == AddTaskState.NEW_TASK_STATE)
            if (isNewTask)
                createTask(tempTask)
            else
                updateTask(tempTask)
            //Log.e(TAG, "Task updated")
        } else {
            taskUpdatedEvent()
        }
    }

    private fun createTask(newTask: Task) = coroutineScope.launch {
        tasksRepository.saveTask(newTask)
        newTaskEvent()
    }

    private fun updateTask(task: Task) {
        if (isNewTask)
            throw RuntimeException("updateTask() was called but task is new")

        coroutineScope.launch {
            tasksRepository.saveTask(task)
            taskUpdatedEvent()
        }
    }

    fun deleteTask() {
        coroutineScope.launch {
            _taskId.value?.let {
                tasksRepository.deleteTask(it)
                taskDeletedEvent()
            }
        }
    }

    fun swipeToDeleteTask(index: Int) {
        coroutineScope.launch {
            val task = tasks.value?.get(index)
            task?.let {
                tasksRepository.deleteTask(it.id)
                hasMessageShown = false
                taskDeletedEvent()
            }
        }
    }

    fun showUserMessage(message: Int) {
        if (hasMessageShown) return
        when (message) {
            Constants.MESSAGE.ADD_TASK_OK -> showSnackbarMessage(R.string.task_created_success)
            Constants.MESSAGE.UPDATE_TASK_OK -> showSnackbarMessage(R.string.task_update_success)
            Constants.MESSAGE.DELETE_TASK_OK -> showSnackbarMessage(R.string.task_deleted_success)
        }

        hasMessageShown = true
    }

    private fun filterTasks(tasksResult: Result<List<Task>>): LiveData<List<Task>> {
        val result = MutableLiveData<List<Task>>()

        if (tasksResult is Result.Success) {
            coroutineScope.launch {
                result.value = filterItems(tasksResult.data, currentFiltering)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.loading_tasks_error)
        }
        return result
    }

    private fun filterItems(tasks: List<Task>, filterType: TasksFilterType): List<Task> {
        val tasksToShow = ArrayList<Task>()

        for (task in tasks) {
            //do filtering
        }

        tasksToShow.addAll(tasks)
        return tasksToShow
    }

    private fun showSnackbarMessage(@StringRes messageRes: Int) {
        _snackbarText.value = Event(messageRes)
    }

    fun newTaskEvent() {
        _newTaskEvent.value = Event(Unit)
    }

    fun openTaskEvent(id: Long) {
        _openTaskEvent.value = Event(id)
    }

    private fun taskUpdatedEvent() {
        _taskUpdatedEvent.value = Event(Unit)
    }

    private fun taskDeletedEvent() {
        _taskDeletedEvent.value = Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}