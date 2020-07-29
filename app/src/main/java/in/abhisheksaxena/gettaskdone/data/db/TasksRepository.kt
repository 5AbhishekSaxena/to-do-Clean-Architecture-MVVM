package `in`.abhisheksaxena.gettaskdone.data.db

import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.data.db.local.TasksLocalDataSource
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.data.Result
import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 06-07-2020 16:49
 */


class TasksRepository(/*context: Context, */private val tasksLocalDataSource: TasksLocalDataSource) {

    //private val tasksLocalDataSource: TaskDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    /*companion object {
        @Volatile
        private var INSTANCE: TasksRepository? = null

        fun getRepository(context: Context): TasksRepository {
            return INSTANCE ?: synchronized(this) {
                TasksRepository(context).also {
                    INSTANCE = it
                }
            }
        }
    }*/

    /*init {
        tasksLocalDataSource = TasksLocalDataSource(TaskDatabase.getInstance(context).taskDao)
    }*/

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>> {
        return tasksLocalDataSource.getTasks()
    }

    fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    fun observeTask(taskId: Long): LiveData<Result<Task>> {
        return tasksLocalDataSource.observeTask(taskId)
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    suspend fun getTask(taskId: Long): Result<Task> {
        return tasksLocalDataSource.getTask(taskId)
    }

    suspend fun saveTask(task: Task) {
        coroutineScope { tasksLocalDataSource.saveTask(task) }
    }

    suspend fun updateTask(task: Task) {
        coroutineScope {
            tasksLocalDataSource.updateTask(task)
        }
    }

    suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            launch { tasksLocalDataSource.deleteAllTasks() }
        }
    }

    suspend fun deleteTask(taskId: Long) {
        withContext(ioDispatcher) {
            tasksLocalDataSource.deleteTask(taskId)
        }
    }

}