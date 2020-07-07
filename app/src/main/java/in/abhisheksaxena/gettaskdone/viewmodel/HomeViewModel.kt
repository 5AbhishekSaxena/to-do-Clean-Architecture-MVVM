package `in`.abhisheksaxena.gettaskdone.viewmodel


import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDao
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.Constants
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:06
 */

class HomeViewModel(
    private val dataSource: TaskDao,
    private var taskId: Long
) : BaseViewModel() {

    private val TAG = javaClass.name

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val ioDispatcher = Dispatchers.IO

    var tasks = dataSource.getAllTasks()

    var tempTask: Task = Task()
    var currentTask = MediatorLiveData<Task>()

    var hasMessageShown = false
    private var isNewTask = true

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
        if (taskId != -1L) {
            isNewTask = false
            currentTask.addSource(dataSource.getTaskById(taskId), currentTask::setValue)
        } else {
            isNewTask = true
        }
        Log.e(TAG, "init current task: ${currentTask.value}")
        Log.e(TAG, "init temp task: $tempTask")
        Log.e(TAG, "init taskid: $taskId")
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
            coroutineScope.launch {
                withContext(ioDispatcher) {
                    //if (_viewState.value == AddTaskState.NEW_TASK_STATE)
                    if (isNewTask)
                        dataSource.insertTask(tempTask)
                    else
                        dataSource.updateTask(tempTask)
                }
                if (isNewTask)
                    newTaskEvent()
                else
                    taskUpdatedEvent()
            }
            //Log.e(TAG, "Task updated")
        } else {
            taskUpdatedEvent()
        }
    }

    fun deleteTask() {
        if (currentTask.value != null) {
            coroutineScope.launch {
                withContext(ioDispatcher) {
                    dataSource.deleteTaskById(currentTask.value!!)
                }
                taskDeletedEvent()
            }
        }
    }

    fun swipeToDeleteTask(index: Int) {
        coroutineScope.launch {
            withContext(ioDispatcher) {
                val task = tasks.value?.get(index)
                task?.let {
                    dataSource.deleteTaskById(it)
                }
            }
            hasMessageShown = false
            taskDeletedEvent()
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