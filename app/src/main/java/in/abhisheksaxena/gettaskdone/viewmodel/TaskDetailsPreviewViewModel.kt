package `in`.abhisheksaxena.gettaskdone.viewmodel

import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.Result
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import kotlinx.coroutines.launch


/**
 * @author Abhishek Saxena
 * @since 07-07-2020 16:27
 */

class TaskDetailsPreviewViewModel
@ViewModelInject constructor(
    application: Application,
    tasksRepository: TasksRepository
) : AbstractViewModel(application, tasksRepository) {

    private var hasMessageShown = false

    private val _taskId = MutableLiveData<Long>()

    private val _task = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }
    val task: LiveData<Task?> = _task

    private val _taskOpenEvent = MutableLiveData<Event<Unit>>()
    val taskOpenEvent: LiveData<Event<Unit>> = _taskOpenEvent

    private val _taskDeleteEvent = MutableLiveData<Event<Task>>()
    val taskDeleteEvent: LiveData<Event<Task>> = _taskDeleteEvent

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
                taskDeleteEvent(task.value!!)
                tasksRepository.deleteTask(taskId)
            }
        }
    }

    override fun showSnackbarMessage(
        messageRes: Int,
        intExtras: List<Int>,
        action: () -> Unit,
        actionText: String
    ) {
        if (hasMessageShown) return
        super.showSnackbarMessage(messageRes, intExtras, action, actionText)
        hasMessageShown = true
    }

    fun taskOpenEvent() {
        _taskOpenEvent.value = Event(Unit)
    }

    private fun taskDeleteEvent(task: Task) {
        _taskDeleteEvent.value = Event(task)
    }
}