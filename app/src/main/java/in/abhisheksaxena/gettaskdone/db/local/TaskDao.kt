package `in`.abhisheksaxena.gettaskdone.db.local

import `in`.abhisheksaxena.gettaskdone.db.model.Task
import androidx.lifecycle.LiveData
import androidx.room.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:43
 */

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("SELECT * FROM tasks_table WHERE id == :id")
    fun getTask(id: Long): Task?

    @Query("SELECT * FROM tasks_table")
    fun getAllTasks(): LiveData<MutableList<Task>>

}