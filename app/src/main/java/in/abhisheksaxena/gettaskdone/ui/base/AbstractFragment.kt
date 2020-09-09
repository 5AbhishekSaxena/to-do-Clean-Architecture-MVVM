package `in`.abhisheksaxena.gettaskdone.ui.base

import `in`.abhisheksaxena.gettaskdone.viewmodel.AbstractViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 06:56
 */

abstract class AbstractFragment<B : ViewDataBinding, VM : AbstractViewModel> :
    Fragment() {

    @get:LayoutRes
    protected abstract var layoutRes: Int

    protected lateinit var binding: B

    protected abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DataBindingUtil.inflate<B>(inflater, layoutRes, container, false)?.apply {
        binding = this
    }?.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        onViewCreated(savedInstanceState)
        setupSnackbar()
    }

    protected abstract fun onViewCreated(savedInstanceState: Bundle?)

    protected open fun setupSnackbar() {
        //Log.e(TAG, "setupSnackbar called")
        viewModel.snackbarText.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (event.containsIntExtras())
                    Snackbar.make(
                        requireView(),
                        getString(it, *event.intExtras.toTypedArray()),
                        Snackbar.LENGTH_SHORT
                    ).show()
                else {
                    if (event.hasAction) {
                        Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_LONG)
                            .setAction(event.actionText) {
                                event.action.invoke()
                            }.show()
                    } else {
                        Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
                    }
                }
                //showSnackbar(context.getString(it), timeLength)
            }
        })
    }
}