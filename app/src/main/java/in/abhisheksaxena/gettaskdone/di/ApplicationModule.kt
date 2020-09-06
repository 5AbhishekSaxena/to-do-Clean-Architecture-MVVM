package `in`.abhisheksaxena.gettaskdone.di

import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDao
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.data.db.local.TasksLocalDataSource
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun providesTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            TaskDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesTaskDao(taskDatabase: TaskDatabase): TaskDao {
        return taskDatabase.getTaskDao()
    }

}