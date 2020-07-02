package `in`.abhisheksaxena.gettaskdone.util

import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import org.w3c.dom.Text
import java.util.*


/**
 * @author Abhishek Saxena
 * @since 01-07-2020 13:49
 */

@BindingAdapter("app:setPriorityColor")
fun setPriorityColor(view: View, task: Task?) {
    task?.let {
        Log.e("setPriorityColor", "task: $it")
        when (it.priority) {
            Task.TaskPriority.LOW -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.dirty_yellow))
            }
            Task.TaskPriority.NORMAL -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.dirty_green))
            }
            else -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.orange))
            }
        }
    }
}