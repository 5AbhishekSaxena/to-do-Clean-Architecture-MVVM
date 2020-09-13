package `in`.abhisheksaxena.gettaskdone.viewmodel

import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.SnackBarEvent
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.Constants
import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 14:42
 */

abstract class AbstractViewModel(
    application: Application,
    protected val tasksRepository: TasksRepository
) : AndroidViewModel(application) {


    private val viewModelJob = Job()
    protected val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Main + viewModelJob)
    protected val ioDispatcher = Dispatchers.IO

    private val _snackbarText: MutableLiveData<SnackBarEvent<Int>> = MutableLiveData()
    val snackbarText: LiveData<SnackBarEvent<Int>> = _snackbarText

    open fun showSnackbarMessage(
        @StringRes messageRes: Int,
        intExtras: List<Int> = emptyList(),
        action: () -> Unit = {},
        actionText: String = ""
    ) {
        when (messageRes) {
            Constants.MESSAGE.ADD_TASK_OK -> setupSnackBarEvent(
                R.string.task_created_success,
                intExtras
            )
            Constants.MESSAGE.UPDATE_TASK_OK -> setupSnackBarEvent(
                R.string.task_update_success,
                intExtras
            )
            Constants.MESSAGE.DELETE_TASK_OK -> setupSnackBarEvent(
                R.string.task_deleted_success,
                intExtras,
                action,
                true,
                actionText
            )
            Constants.MESSAGE.ERROR_LOADING_TASK -> setupSnackBarEvent(
                R.string.loading_tasks_error,
                intExtras
            )
        }
    }

    private fun setupSnackBarEvent(
        @StringRes messageRes: Int,
        intExtras: List<Int>,
        action: () -> Unit = {},
        hasAction: Boolean = false,
        actionText: String = ""
    ) {
        //Log.d(TAG, "showSnackbarMessage, hasMessageShown: $hasMessageShown")

        _snackbarText.value = SnackBarEvent(messageRes).apply {
            if (intExtras.isNotEmpty())
                this.intExtras = intExtras

            this.hasAction = hasAction
            this.actionText = actionText
            this.action = action
        }
    }

    fun insertTask(task: Task) {
        coroutineScope.launch {
            withContext(ioDispatcher) {
                tasksRepository.saveTask(task)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}