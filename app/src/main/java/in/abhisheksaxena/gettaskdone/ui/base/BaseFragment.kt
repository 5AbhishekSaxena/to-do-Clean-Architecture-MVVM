package `in`.abhisheksaxena.gettaskdone.ui.base

import `in`.abhisheksaxena.gettaskdone.viewmodel.BaseViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.BaseViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 06:56
 */

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel> :
    Fragment() {

    @get:LayoutRes
    protected abstract var layoutRes: Int

    protected lateinit var binding: B

    protected lateinit var viewModel: VM
    protected abstract var modelClass: Class<VM>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<B>(inflater, layoutRes, container, false)?.apply {
        binding = this
    }?.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModelStoreOwner()?.let { owner ->
            viewModel =
                ViewModelProvider(owner).get(modelClass)
        }
        onViewCreated(savedInstanceState)
    }

    protected abstract fun onViewCreated(savedInstanceState: Bundle?)

    protected abstract fun getViewModelStoreOwner(): ViewModelStoreOwner?
}