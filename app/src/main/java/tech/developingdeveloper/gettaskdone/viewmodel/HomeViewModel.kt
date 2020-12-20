package tech.developingdeveloper.gettaskdone.viewmodel


import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import kotlinx.coroutines.launch
import tech.developingdeveloper.gettaskdone.Event
import tech.developingdeveloper.gettaskdone.R
import tech.developingdeveloper.gettaskdone.data.Result
import tech.developingdeveloper.gettaskdone.data.db.TasksRepository
import tech.developingdeveloper.gettaskdone.data.model.Task
import tech.developingdeveloper.gettaskdone.util.Constants
import tech.developingdeveloper.gettaskdone.util.TasksFilterType


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:06
 */

class HomeViewModel
@ViewModelInject constructor(
    application: Application,
    tasksRepository: TasksRepository
) : AbstractViewModel(application, tasksRepository) {

    private val TAG = javaClass.name

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _tasks: LiveData<List<Task>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            //refreshData
        }
        //Log.d(TAG, "TAG, forceUpdate: $forceUpdate")
        tasksRepository.observeTasks().switchMap { filterTasks(it) }
    }
    val tasks: LiveData<List<Task>> = _tasks

    var hasMessageShown = false

    private var currentFiltering = TasksFilterType.ALL_TASKS

    var isAscendingOrder = MutableLiveData<Boolean>(true)

    private val _queryText = MutableLiveData<String>()
    val queryText: LiveData<String> = _queryText

    private val _openTaskEvent = MutableLiveData<Event<Long>>()
    val openTaskEvent: LiveData<Event<Long>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    fun swipeToDeleteTask(index: Int) {
        Log.d(TAG, "swipeToDeleteTask, index: $index")
        coroutineScope.launch {
            val task = tasks.value?.get(index)
            task?.let {
                tasksRepository.deleteTask(it.id)
                hasMessageShown = false
                //Log.d(TAG, "swipeToDeleteTask, hasMessageShown: $hasMessageShown")
                taskSwipeToDeleteEvent(it)
            }
        }
    }

    override fun showSnackbarMessage(
        messageRes: Int,
        intExtras: List<Int>,
        action: () -> Unit,
        actionText: String
    ) {
        //Log.d(TAG, "showUserMessage, hasMessageShown: $hasMessageShown, message: $message")
        if (hasMessageShown) return
        super.showSnackbarMessage(messageRes, intExtras, action, actionText)
        hasMessageShown = true
    }

    private fun filterTasks(tasksResult: Result<List<Task>>): LiveData<List<Task>> {
        val result = MutableLiveData<List<Task>>()

        if (tasksResult is Result.Success) {
            coroutineScope.launch {
                result.value = filterItems(
                    tasksResult.data,
                    currentFiltering,
                    _queryText.value,
                    isAscendingOrder.value ?: true
                )
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.loading_tasks_error)
        }
        return result
    }

    private fun filterItems(
        tasks: List<Task>,
        filterType: TasksFilterType,
        query: String?,
        isInAscendingOrder: Boolean = true
    ): List<Task> {

        var tasksToShow = if (query != null && query.isNotEmpty())
            tasks.filter { it.toString().contains(query, true) }
        else
            tasks.toList()

        //Log.d(TAG, "filterItems, isAscendingOrder: $isInAscendingOrder")
        tasksToShow = if (isInAscendingOrder)
            tasksToShow.sortedWith(compareBy { it.updatedOn })
        else
            tasksToShow.sortedWith(compareByDescending { it.updatedOn })

        return tasksToShow
    }

    fun updateSearchText(text: String?) {
        _queryText.value = text
        loadTasks(false)
    }

    fun updateSortOrder() {
        if (isAscendingOrder.value != null)
            isAscendingOrder.value = !isAscendingOrder.value!!
        else
            isAscendingOrder.value = true

        //Log.d(TAG, "updateSortOrder, listIsAscending: $listIsAscending")
        loadTasks(false)
    }

    private fun loadTasks(forceLoad: Boolean) {
        _forceUpdate.value = forceLoad
    }

    fun newTaskEvent() {
        _newTaskEvent.value = Event(Unit)
    }

    fun openTaskEvent(id: Long) {
        _openTaskEvent.value = Event(id)
    }

    private fun taskSwipeToDeleteEvent(task: Task) {
        showSnackbarMessage(Constants.MESSAGE.DELETE_TASK_OK, action = {
            insertTask(task)
        }, actionText = "Undo")
    }
}