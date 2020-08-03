package `in`.abhisheksaxena.gettaskdone.viewmodel

import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.util.Constants
import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 14:42
 */

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application) {


    private val viewModelJob = Job()
    protected val coroutineScope: CoroutineScope =
        CoroutineScope(Dispatchers.Main + viewModelJob)
    protected val ioDispatcher = Dispatchers.IO

    private val _snackbarText: MutableLiveData<Event<Int>> = MutableLiveData()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    open fun showSnackbarMessage(
        @StringRes messageRes: Int,
        intExtras: List<Int> = emptyList()
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
                intExtras
            )
            Constants.MESSAGE.ERROR_LOADING_TASK -> setupSnackBarEvent(
                R.string.loading_tasks_error,
                intExtras
            )
        }
    }

    private fun setupSnackBarEvent(
        @StringRes messageRes: Int,
        intExtras: List<Int>
    ) {
        //Log.d(TAG, "showSnackbarMessage, hasMessageShown: $hasMessageShown")

        val snackbarEvent = Event(messageRes)

        if (intExtras.isNotEmpty())
            snackbarEvent.intExtras = intExtras

        _snackbarText.value = snackbarEvent
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}