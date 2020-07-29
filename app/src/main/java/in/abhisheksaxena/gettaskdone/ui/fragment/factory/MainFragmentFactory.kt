package `in`.abhisheksaxena.gettaskdone.ui.fragment.factory

import `in`.abhisheksaxena.gettaskdone.ui.fragment.HomeFragment
import `in`.abhisheksaxena.gettaskdone.ui.fragment.TaskDetailsFragment
import `in`.abhisheksaxena.gettaskdone.ui.fragment.TaskDetailsPreviewFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject


/**
 * @author Abhishek Saxena
 * @since 30-07-2020 03:20
 */

class MainFragmentFactory
@Inject
constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            HomeFragment::class.java.name -> HomeFragment()
            TaskDetailsFragment::class.java.name -> TaskDetailsFragment()
            TaskDetailsPreviewFragment::class.java.name -> TaskDetailsPreviewFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}