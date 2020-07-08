package `in`.abhisheksaxena.gettaskdone.viewmodel.factory

import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import androidx.lifecycle.ViewModel
import java.lang.IllegalArgumentException


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 12:42
 */

class HomeViewModelFactory(private val dataSource: TasksRepository): BaseViewModelFactory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java))
            return HomeViewModel(dataSource) as T
        throw IllegalArgumentException("Unknown ViewModel initialised")
    }
}