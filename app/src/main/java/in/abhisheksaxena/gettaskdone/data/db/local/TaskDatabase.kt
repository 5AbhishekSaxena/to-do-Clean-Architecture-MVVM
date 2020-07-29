package `in`.abhisheksaxena.gettaskdone.data.db.local

import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.getCurrentTimeInMilli
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:36
 */

@Database(
    entities = [Task::class],
    version = 4,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "taskdb"
    }

}