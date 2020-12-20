package tech.developingdeveloper.gettaskdone.data.db

import androidx.lifecycle.LiveData
import tech.developingdeveloper.gettaskdone.data.Result
import tech.developingdeveloper.gettaskdone.data.model.Task


/**
 * @author Abhishek Saxena
 * @since 06-07-2020 16:50
 */

interface TaskDataSource {

    fun observeTasks(): LiveData<Result<List<Task>>>

    fun observeTask(taskId: Long): LiveData<Result<Task>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: Long): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: Long)

}