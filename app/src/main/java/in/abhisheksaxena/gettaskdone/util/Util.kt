package `in`.abhisheksaxena.gettaskdone.util

import `in`.abhisheksaxena.gettaskdone.Event
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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

fun View.showSnackbar(snackBarText: String, timeLength: Int){
    Snackbar.make(this, snackBarText, timeLength).show()
}

fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
){
    snackbarEvent.observe(lifecycleOwner, Observer{ event ->
        event.getContentIfNotHandled()?.let {
            Log.d(javaClass.name, "setupSnackbar, event.hasExtras(): ${event.hasIntExtras()}, event.intArray: ${event.intExtras}")
            if (event.hasIntExtras() && event.intExtras != null)
                //Log.d(javaClass.name, "setupSnackbar, error: string: ${context.getString(it, *event.intExtras!!)}")
                showSnackbar(context.getString(it, *event.intExtras!!), timeLength)
            else
                showSnackbar(context.getString(it), timeLength)
        }
    })
}