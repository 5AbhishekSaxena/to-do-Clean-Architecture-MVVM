package `in`.abhisheksaxena.gettaskdone.viewmodel

import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.Result
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.Constants
import android.app.Application
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 07-07-2020 16:27
 */

class TaskDetailsPreviewViewModel
@ViewModelInject constructor(
    application: Application,
    private val tasksRepository: TasksRepository
) : AbstractViewModel(application) {

    private var hasMessageShown = false

    private val _taskId = MutableLiveData<Long>()

    private val _task = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }
    val task: LiveData<Task?> = _task

    private val _taskOpenEvent = MutableLiveData<Event<Unit>>()
    val taskOpenEvent: LiveData<Event<Unit>> = _taskOpenEvent

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
            showSnackbarMessage(R.string.loading_tasks_error)
            null
        }
    }

    fun deleteTask() {
        _taskId.value?.let { taskId ->
            coroutineScope.launch {
                tasksRepository.deleteTask(taskId)
                taskDeleteEvent()
            }
        }
    }

    override fun showSnackbarMessage(messageRes: Int, intExtras: List<Int>) {
        if (hasMessageShown) return
        super.showSnackbarMessage(messageRes, intExtras)
        hasMessageShown = true
    }

    fun taskOpenEvent() {
        _taskOpenEvent.value = Event(Unit)
    }

    private fun taskDeleteEvent() {
        _taskDeleteEvent.value = Event(Unit)
    }
}