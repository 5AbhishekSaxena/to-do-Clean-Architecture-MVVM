package `in`.abhisheksaxena.gettaskdone.adapter


import `in`.abhisheksaxena.gettaskdone.databinding.TaskListItemBinding
import `in`.abhisheksaxena.gettaskdone.db.model.Task
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:48
 */

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.ViewHolder>(TaskDiffCallback()) {

    private val TAG = javaClass.name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Log.e(TAG, "onCreateViewHolder called")
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Log.e(TAG, "onBindViewHolder called")
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //Log.e("ViewHolder", "from called")
                val inflater = LayoutInflater.from(parent.context)
                val binding = TaskListItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(task: Task) {
            //Log.e(javaClass.name, "from bind(), task: $task")
            binding.titleTextView.text = task.title
            //binding.summaryTextView.text = task.details

            /*if (task.details.isEmpty())
                binding.summaryTextView.visibility = View.GONE*/
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

}