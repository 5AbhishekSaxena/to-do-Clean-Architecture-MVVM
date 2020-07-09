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

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _tasks: LiveData<List<Task>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            //refreshData
        }
        tasksRepository.observeTasks().switchMap { filterTasks(it) }
    }
    val tasks: LiveData<List<Task>> = _tasks

    var hasMessageShown = false

    private var currentFiltering = TasksFilterType.ALL_TASKS

    private val _searchText = MutableLiveData<String>()
    val searchText: LiveData<String> = _searchText

    private var _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _openTaskEvent = MutableLiveData<Event<Long>>()
    val openTaskEvent: LiveData<Event<Long>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    private val _taskSwipeToDeletedEvent = MutableLiveData<Event<Unit>>()
    val taskSwipeToDeletedEvent: LiveData<Event<Unit>> = _taskSwipeToDeletedEvent

    fun swipeToDeleteTask(index: Int) {
        Log.d(TAG, "swipeToDeleteTask, index: $index")
        coroutineScope.launch {
            val task = tasks.value?.get(index)
            task?.let {
                tasksRepository.deleteTask(it.id)
                hasMessageShown = false
                taskSwipeToDeleteEvent()
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
                result.value = filterItems(tasksResult.data, currentFiltering, _searchText.value ?: "")
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
        searchText: String
    ): List<Task> {
        //val tasksToShow = mutableListOf<Task>()

        if (searchText.isEmpty())
            return tasks

        return tasks.filter { it.toString().contains(searchText) }
        //return tasksToShow
    }

    fun updateSearchText(text: String){
        _searchText.value = text
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

    private fun taskSwipeToDeleteEvent(){
        Log.d(TAG, "taskSwipeToDeleteEvent")
        showSnackbarMessage(R.string.task_deleted_success)
        _taskSwipeToDeletedEvent.value = Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}