package `in`.abhisheksaxena.gettaskdone.data.db.local

import `in`.abhisheksaxena.gettaskdone.data.Result
import `in`.abhisheksaxena.gettaskdone.data.db.TaskDataSource
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * @author Abhishek Saxena
 * @since 06-07-2020 16:55
 */

class TasksLocalDataSource internal constructor(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskDao.observeTasks().map {
            Result.Success(it)
        }
    }

    override suspend fun getTasks(): Result<List<Task>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(taskDao.getTasks())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTask(taskId: Long): LiveData<Result<Task>> {
        return taskDao.observeTaskWithId(taskId).map {
            Result.Success(it)
        }
    }

    override suspend fun getTask(taskId: Long): Result<Task> =
        withContext(ioDispatcher) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task != null) {
                    return@withContext Result.Success(task)
                } else {
                    return@withContext Result.Error(Exception("Task not found"))
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        taskDao.deleteTasks()
    }


    override suspend fun deleteTask(taskId: Long) = withContext<Unit>(ioDispatcher) {
        taskDao.deleteTaskById(taskId)
    }
}