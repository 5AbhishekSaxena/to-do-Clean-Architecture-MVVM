package tech.developingdeveloper.gettaskdone.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import tech.developingdeveloper.gettaskdone.data.db.TasksRepository
import tech.developingdeveloper.gettaskdone.data.db.local.TaskDao
import tech.developingdeveloper.gettaskdone.data.db.local.TaskDatabase
import tech.developingdeveloper.gettaskdone.data.db.local.TasksLocalDataSource
import javax.inject.Singleton


/**
 * @author Abhishek Saxena
 * @since 30-07-2020 03:26
 */

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun providesTasksLocalDataSource(taskDao: TaskDao): TasksLocalDataSource {
        return TasksLocalDataSource(taskDao)
    }

    @Singleton
    @Provides
    fun providesTaskRepository(
        tasksLocalDataSource: TasksLocalDataSource
    ): TasksRepository {
        return TasksRepository(tasksLocalDataSource)
    }

    @Singleton
    @Provides
    fun providesTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesTaskDao(taskDatabase: TaskDatabase): TaskDao {
        return taskDatabase.getTaskDao()
    }

}