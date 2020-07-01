package `in`.abhisheksaxena.gettaskdone.adapter

import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter


/**
 * @author Abhishek Saxena
 * @since 01-07-2020 13:49
 */

@BindingAdapter("setPriorityColor")
fun View.setPriorityColor(task: Task?) {
    task?.let {
        Log.e("setPriorityColor", "task: $it")
        when (it.priority) {
            Task.TaskPriority.LOW -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.dirty_yellow))
            }
            Task.TaskPriority.NORMAL -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.dirty_green))
            }
            else -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
            }
        }
    }
}