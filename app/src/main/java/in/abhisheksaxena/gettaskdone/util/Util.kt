package `in`.abhisheksaxena.gettaskdone.util

import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.EventObserver
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 17:38
 */

fun hideKeyboard(activity: Activity?) {
    (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow((activity.currentFocus ?: View(activity)).windowToken, 0)
}

fun View.showSnackBar(snackBarText: String, timeLength: Int){
    Snackbar.make(this, snackBarText, timeLength).show()
}

fun View.setupSnackBar(
    lifecycleOwner: LifecycleOwner,
    snackBarEvent: LiveData<Event<Int>>,
    timeLength: Int
){
    snackBarEvent.observe(lifecycleOwner, Observer{event ->
        event.getContentIfNotHandled()?.let {
            showSnackBar(context.getString(it), timeLength)
        }
    })
}