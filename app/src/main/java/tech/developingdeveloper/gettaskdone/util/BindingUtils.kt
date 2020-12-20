package tech.developingdeveloper.gettaskdone.util

import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import tech.developingdeveloper.gettaskdone.R
import tech.developingdeveloper.gettaskdone.data.model.Task
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Abhishek Saxena
 * @since 01-07-2020 13:49
 */

private const val TAG = "BindingUtils"

@BindingAdapter("app:setPriorityColor")
fun setPriorityColor(view: View, task: Task?) {
    task?.let {
        Log.e("setPriorityColor", "task: $it")
        when (it.priority) {
            Task.TaskPriority.LOW -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.dirty_yellow))
            }
            Task.TaskPriority.MEDIUM -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.dirty_green))
            }
            else -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.orange))
            }
        }
    }
}

@BindingAdapter("app:setDate")
fun setDate(textView: TextView, dateInMilli: Long?) {
    dateInMilli?.let {
        val date = when {
            DateUtils.isToday(it) -> "Today"
            DateUtils.isToday(it + DateUtils.DAY_IN_MILLIS) -> "Yesterday"
            else -> SimpleDateFormat("MMMM dd, YYYY", Locale.getDefault()).format(Date(it))
        }
        textView.text = date

        Log.d(
            TAG,
            "setDate, dateInMilli.today: $it, tomorrow: ${it - DateUtils.DAY_IN_MILLIS} yesterday: ${it + DateUtils.DAY_IN_MILLIS}"
        )
    }
}

@BindingAdapter("app:setDrawableEnd")
fun setDrawableEnd(textView: TextView, isAscendingOrder: Boolean?) {
    isAscendingOrder?.let {
        if (it)
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_baseline_arrow_upward_24,
                0
            )
        else
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_baseline_arrow_downward_24,
                0
            )
    }
}