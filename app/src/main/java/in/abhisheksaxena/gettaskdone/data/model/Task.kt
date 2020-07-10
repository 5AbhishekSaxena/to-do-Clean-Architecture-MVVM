package `in`.abhisheksaxena.gettaskdone.data.model

import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.util.getCurrentTimeInMilli
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:49
 */

@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var title: String = "",
    var details: String = "",
    var priority: String = "",
    @ColumnInfo(name = "created_on") val createdOn: Long = getCurrentTimeInMilli(),
    @ColumnInfo(name = "last_update") var lastUpdate: Long = getCurrentTimeInMilli()
) {
    constructor(task: Task) : this(
        task.id,
        task.title,
        task.details,
        task.priority,
        task.createdOn,
        task.lastUpdate
    )

    fun hasDetails(): Boolean = details.isNotEmpty()

    fun getPriorityValue(): Int{
        return when(priority){
            TaskPriority.LOW -> 1
            TaskPriority.MEDIUM -> 2
            else -> 3
        }
    }

    override fun toString(): String {
        return "{Task:" +
                " id: $id, " +
                " title: $title," +
                " details: $details," +
                " priority: $priority" +
                " created_on: $createdOn" +
                " last_update: $lastUpdate" +
                "}"
    }

    interface TaskPriority {
        companion object {
            const val LOW = "Low"
            const val MEDIUM = "Medium"
            const val HIGH = "High"
        }
    }
}