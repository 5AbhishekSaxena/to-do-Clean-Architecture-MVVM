package `in`.abhisheksaxena.gettaskdone.viewmodel.factory

import `in`.abhisheksaxena.gettaskdone.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.viewmodel.AddTaskState
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import androidx.lifecycle.ViewModel
import java.lang.IllegalArgumentException


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 12:42
 */

class HomeViewModelFactory(private val database: TaskDatabase, private val state: AddTaskState = AddTaskState.EDIT_STATE): BaseViewModelFactory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(database, state) as T
        throw IllegalArgumentException("Unknown ViewModel initialised")
    }
}