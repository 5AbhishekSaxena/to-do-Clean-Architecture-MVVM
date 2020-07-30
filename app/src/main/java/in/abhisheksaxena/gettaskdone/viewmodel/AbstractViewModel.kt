package `in`.abhisheksaxena.gettaskdone.viewmodel

import `in`.abhisheksaxena.gettaskdone.Event
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 14:42
 */

abstract class AbstractViewModel(application: Application): AndroidViewModel(application){
    protected val _snackbarText: MutableLiveData<Event<Int>> = MutableLiveData()
    val snackbarText: LiveData<Event<Int>> = _snackbarText
}