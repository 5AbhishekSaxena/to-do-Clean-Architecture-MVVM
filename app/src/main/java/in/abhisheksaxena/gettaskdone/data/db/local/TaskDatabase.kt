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

    abstract val taskDao: TaskDao

    companion object {

        private const val DATABASE_NAME = "taskdb"

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_3_4)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }


        private val MIGRATION_3_4: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE Tasks "
                            + " ADD COLUMN " +
                            "created_on LONG DEFAULT (${getCurrentTimeInMilli()}), " +
                            "last_update LONG DEFAULT (${getCurrentTimeInMilli()})"
                )
            }
        }
    }

}