package `in`.abhisheksaxena.gettaskdone.data.db.local

import `in`.abhisheksaxena.gettaskdone.data.model.Task
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
    version = 1,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao



    companion object {
        const val DATABASE_NAME = "taskdb"

        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        TaskDatabase::class.java,
                        DATABASE_NAME
                    )
                        /*.addMigrations(MIGRATION_1_2)*/
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        /*private val MIGRATION_1_2 = object: Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN reminder_date TEXT NOT NULL DEFAULT ''")
                //database.execSQL("ALTER TABLE Song ADD COLUMN tag TEXT NOT NULL DEFAULT ''")
            }
        }*/



    }

}