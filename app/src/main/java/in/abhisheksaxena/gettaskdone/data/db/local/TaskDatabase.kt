package `in`.abhisheksaxena.gettaskdone.data.db.local

import `in`.abhisheksaxena.gettaskdone.data.model.Task
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:36
 */

@Database(
    entities = [Task::class],
    version = 3,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDao: TaskDao

    companion object {

        const val DATABASE_NAME = "taskdb"

        /* @Volatile
         private var instance: TaskDatabase? = null

         fun getInstance(context: Context): TaskDatabase {
             synchronized(this) {
                 var instance =
                     instance
                 if (instance == null) {
                     instance = Room.databaseBuilder(
                         context.applicationContext,
                         TaskDatabase::class.java,
                         DATABASE_NAME
                     )
                         .fallbackToDestructiveMigration()
                         .build()
                     Companion.instance = instance
                 }
                 return instance
             }
         }*/
    }
}