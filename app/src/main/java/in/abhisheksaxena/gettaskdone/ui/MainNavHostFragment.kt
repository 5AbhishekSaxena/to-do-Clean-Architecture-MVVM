package `in`.abhisheksaxena.gettaskdone.ui

import `in`.abhisheksaxena.gettaskdone.ui.fragment.factory.MainFragmentFactory
import android.content.Context
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * @author Abhishek Saxena
 * @since 30-07-2020 03:18
 */

@AndroidEntryPoint
class MainNavHostFragment: NavHostFragment() {

    @Inject
    lateinit var fragmentFactory: MainFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = fragmentFactory
    }
}