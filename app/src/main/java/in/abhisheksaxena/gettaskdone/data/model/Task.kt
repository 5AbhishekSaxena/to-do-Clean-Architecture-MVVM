package `in`.abhisheksaxena.gettaskdone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:49
 */

@Entity(tableName = "tasks_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var title: String = "",
    var details: String = "",
    var priority: String = ""
) {
    constructor(task: Task) : this(task.id, task.title, task.details, task.priority)

    override fun toString(): String {
        return "{Task: id: $id, title: $title, details: $details, priority: $priority}"
    }

    interface TaskPriority {
        companion object {
            const val LOW = "Low"
            const val NORMAL = "Normal"
            const val HIGH = "High"
        }
    }
}