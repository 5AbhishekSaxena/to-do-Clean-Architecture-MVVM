package `in`.abhisheksaxena.gettaskdone.di

import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDao
import `in`.abhisheksaxena.gettaskdone.data.db.local.TasksLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


/**
 * @author Abhishek Saxena
 * @since 30-07-2020 03:26
 */

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesTasksLocalDataSource(taskDao: TaskDao): TasksLocalDataSource{
        return TasksLocalDataSource(taskDao)
    }

    @Singleton
    @Provides
    fun providesTaskRepository(
        tasksLocalDataSource: TasksLocalDataSource
    ): TasksRepository{
        return TasksRepository(tasksLocalDataSource)
    }

}