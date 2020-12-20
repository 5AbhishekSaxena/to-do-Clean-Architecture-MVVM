package tech.developingdeveloper.gettaskdone.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import kotlinx.coroutines.launch
import tech.developingdeveloper.gettaskdone.Event
import tech.developingdeveloper.gettaskdone.R
import tech.developingdeveloper.gettaskdone.data.Result
import tech.developingdeveloper.gettaskdone.data.db.TasksRepository
import tech.developingdeveloper.gettaskdone.data.model.Task
import tech.developingdeveloper.gettaskdone.util.Constants
import tech.developingdeveloper.gettaskdone.util.getCurrentTimeInMilli


/**
 * @author Abhishek Saxena
 * @since 07-07-2020 15:12
 */

private const val TAG = "TaskDetailsViewModel"

class TaskDetailsViewModel
@ViewModelInject
constructor(application: Application, tasksRepository: TasksRepository) :
    AbstractViewModel(application, tasksRepository) {

    private var isNewTask: Boolean = true

    private val _taskId = MutableLiveData<Long>()

    private val _task = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }
    val currentTask: LiveData<Task?> = _task

    var tempTask = Task()

    private val _taskCreateEvent = MutableLiveData<Event<Unit>>()
    val taskCreateEvent: LiveData<Event<Unit>> = _taskCreateEvent

    private val _taskUpdateEvent = MutableLiveData<Event<Boolean>>()
    val taskUpdateEvent: LiveData<Event<Boolean>> = _taskUpdateEvent

    fun start(taskId: Long) {
        Log.d(TAG, "start, taskId: $taskId")
        if (taskId == -1L) {
            isNewTask = true
            return
        }

        _taskId.value = taskId
        isNewTask = false
        coroutineScope.launch {
            tasksRepository.getTask(taskId).let { result ->
                if (result is Result.Success) {
                    // attach observer
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

    fun saveTask() {
        Log.d(
            TAG,
            "saveTask: " +
                    "currentTask: ${currentTask.value} " +
                    "tempTask: $tempTask " +
                    "equals: ${currentTask.value == tempTask}" +
                    "isNewTask: $isNewTask"
        )
        when {
            tempTask.title.isEmpty() -> {
                showSnackbarMessage(R.string.title_empty)
                return
            }
            tempTask.title.length > Constants.TITLE_CHARACTER_LIMIT -> {
                showSnackbarMessage(
                    R.string.title_text_over_limit,
                    listOf(Constants.TITLE_CHARACTER_LIMIT)
                )
                return
            }
            tempTask.priority.isEmpty() -> {
                showSnackbarMessage(R.string.priority_empty)
                return
            }
        }

        if (currentTask.value != tempTask) {
            if (isNewTask || _taskId.value == null)
                createTask(tempTask)
            else {
                tempTask.updatedOn = getCurrentTimeInMilli()
                updateTask(tempTask)
            }
        } else {
            taskUpdateEvent(false)
        }
    }

    private fun createTask(newTask: Task) {
        Log.d(TAG, "createTask")
        coroutineScope.launch {
            tasksRepository.saveTask(newTask)
            taskCreateEvent()
        }
    }

    private fun updateTask(updatedTask: Task) {
        Log.d(TAG, "updateTask ")
        coroutineScope.launch {
            tasksRepository.updateTask(updatedTask)
            taskUpdateEvent(true)
        }
    }

    private fun computeResult(taskResult: Result<Task>): Task? {
        return if (taskResult is Result.Success) {
            taskResult.data
        } else {
            showSnackbarMessage(R.string.loading_tasks_error)
            null
        }
    }

    private fun taskCreateEvent() {
        _taskCreateEvent.value = Event(Unit)
    }

    private fun taskUpdateEvent(isUpdated: Boolean) {
        _taskUpdateEvent.value = Event(isUpdated)
    }
}
