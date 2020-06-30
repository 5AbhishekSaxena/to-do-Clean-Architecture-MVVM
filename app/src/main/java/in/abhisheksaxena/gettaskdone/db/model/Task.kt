package `in`.abhisheksaxena.gettaskdone.db.model

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
    var title: String,
    var details: String = ""
){
    override fun toString(): String {
        return "{Task: id: $id, title: $title, details: $details}"
    }
}