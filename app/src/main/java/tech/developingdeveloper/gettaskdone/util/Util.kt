package tech.developingdeveloper.gettaskdone.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import tech.developingdeveloper.gettaskdone.data.model.Task
import java.util.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 17:38
 */

fun hideKeyboard(activity: Activity?) {
    (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow((activity.currentFocus ?: View(activity)).windowToken, 0)
}

fun getCurrentTimeInMilli(): Long = Calendar.getInstance(Locale.getDefault()).timeInMillis

fun convertTaskToString(task: Task): String {
    return StringBuilder().apply {
        append(task.title)
        append("\n\n")
        append("Details: \n")
        append(task.details)
    }.toString()
}