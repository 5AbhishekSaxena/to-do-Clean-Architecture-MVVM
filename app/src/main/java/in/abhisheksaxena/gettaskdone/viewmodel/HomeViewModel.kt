package `in`.abhisheksaxena.gettaskdone.viewmodel


import `in`.abhisheksaxena.gettaskdone.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.db.model.Task
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:06
 */

class HomeViewModel(
    private val database: TaskDatabase,
    state: AddTaskState
) : BaseViewModel() {

    private val TAG = javaClass.name

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var tasks = database.taskDao.getAllTasks()

    var title = ""
    var details = ""

    private var _viewState = MutableLiveData<AddTaskState>()

    val viewState: LiveData<AddTaskState>
        get() = _viewState

    private val _navigateToAddTaskFragment = MutableLiveData<Boolean>()

    val navigateToAddTaskFragment: LiveData<Boolean>
        get() = _navigateToAddTaskFragment

    private val _navigateToHomeFragment = MutableLiveData<Boolean>()

    val navigateToHomeFragment: LiveData<Boolean>
        get() = _navigateToHomeFragment

    init {
        _viewState.value = state
        _navigateToHomeFragment.value = false
        _navigateToAddTaskFragment.value = false
    }

    fun addTask() {
        if (title.isNotEmpty()) {
            if (details.isEmpty())
                details = ""
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    database.taskDao.insertTask(Task(title = title, details = details))
                }
                navigateToHomeFragment()
            }
        }
    }

    fun navigateToAddTaskFragment(){
        _navigateToAddTaskFragment.value = true
    }

    fun navigateToHomeFragment(){
        _navigateToHomeFragment.value = true
    }

    fun doneNavigationToAddTask() {
        _navigateToAddTaskFragment.value = false
    }

    fun doneNavigationToHome() {
        _navigateToHomeFragment.value = false
    }

    fun updateViewState(state: AddTaskState){
        _viewState.value = state
    }

}

enum class AddTaskState{
    EDIT_STATE, VIEW_STATE
}