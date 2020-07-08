package `in`.abhisheksaxena.gettaskdone.viewmodel

import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.Result
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.Constants
import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 07-07-2020 16:27
 */

class TaskDetailsPreviewViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Main + viewModelJob)
    private val ioDispatcher = Dispatchers.IO

    private val dataSource: TasksRepository = TasksRepository.getRepository(application)

    private val _taskId = MutableLiveData<Long>()

    private val _task = _taskId.switchMap { taskId ->
        dataSource.observeTask(taskId).map { computeResult(it) }
    }
    val task: LiveData<Task?> = _task

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _taskOpenEvent = MutableLiveData<Event<Unit>>()
    val taskOpenEvent: LiveData<Event<Unit>> = _taskOpenEvent

    private val _taskUpdateEvent = MutableLiveData<Event<Unit>>()
    val taskUpdateEvent: LiveData<Event<Unit>> = _taskUpdateEvent

    private val _taskDeleteEvent = MutableLiveData<Event<Unit>>()
    val taskDeleteEvent: LiveData<Event<Unit>> = _taskDeleteEvent

    fun start(taskId: Long?) {
        // possible configuration change
        if (taskId == _taskId.value)
            return

        _taskId.value = taskId
    }

    private fun computeResult(resultTask: Result<Task>): Task? {
        return if (resultTask is Result.Success) {
            resultTask.data
        } else {
            showSnackBarMessage(R.string.loading_tasks_error)
            null
        }
    }

    fun deleteTask() {
        _taskId.value?.let { taskId ->
            coroutineScope.launch {
                dataSource.deleteTask(taskId)
                taskDeleteEvent()
            }
        }
    }

    private fun showSnackBarMessage(@StringRes messageRes: Int) {
        _snackbarText.value = Event(messageRes)
    }

    fun showUserMessage(message: Int) {
        when (message) {
            Constants.MESSAGE.ADD_TASK_OK -> showSnackBarMessage(R.string.task_created_success)
            Constants.MESSAGE.UPDATE_TASK_OK -> showSnackBarMessage(R.string.task_update_success)
            Constants.MESSAGE.UPDATE_TASK_NOT_OK -> showSnackBarMessage(R.string.task_no_change)
        }
    }

    fun taskOpenEvent() {
        _taskOpenEvent.value = Event(Unit)
    }

    //todo
    fun taskUpdateEvent() {
        _taskUpdateEvent.value = Event(Unit)
    }

    private fun taskDeleteEvent() {
        _taskDeleteEvent.value = Event(Unit)
    }
}