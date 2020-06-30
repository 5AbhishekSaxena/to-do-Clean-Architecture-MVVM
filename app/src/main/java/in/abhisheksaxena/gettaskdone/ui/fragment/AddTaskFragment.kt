package `in`.abhisheksaxena.gettaskdone.ui.fragment


import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentAddTaskBinding
import `in`.abhisheksaxena.gettaskdone.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.util.hideKeyboard
import `in`.abhisheksaxena.gettaskdone.util.showSnackBar
import `in`.abhisheksaxena.gettaskdone.viewmodel.AddTaskState
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:43
 */

class AddTaskFragment : Fragment() {


    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var viewModel: HomeViewModel

    private val TAG = javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_task, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = TaskDatabase.getInstance(requireContext())
        val arguments = AddTaskFragmentArgs.fromBundle(requireArguments())
        Log.e(TAG, "arguments, state: ${arguments.viewState}")
        val factory = HomeViewModelFactory(database, arguments.viewState)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        viewModel.navigateToHomeFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    findNavController().navigate(AddTaskFragmentDirections.actionAddTaskFragmentToHomeFragment())
                    viewModel.doneNavigationToHome()
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            binding.fab.setFabButton(state)
            if (state == AddTaskState.EDIT_STATE)
                toggleEditable(true)
            else
                toggleEditable(false)
        })
    }

    private fun FloatingActionButton.setFabButton(state: AddTaskState?) {
        when (state) {
            AddTaskState.EDIT_STATE -> {
                setImageResource(R.drawable.ic_baseline_save_24)
                setOnClickListener {
                    handleClickEvent(state)
                }
            }
            AddTaskState.VIEW_STATE -> {
                setImageResource(R.drawable.ic_baseline_edit_24)
                setOnClickListener {
                    handleClickEvent(state)
                }
            }
        }
    }

    private fun handleClickEvent(state: AddTaskState) {
        if (state == AddTaskState.EDIT_STATE) {
            viewModel.title = binding.titleEditText.text.toString()
            viewModel.details = binding.detailsEditText.text.toString()

            when {
                viewModel.title.isEmpty() -> binding.root.showSnackBar(getString(R.string.title_empty))
                else -> {
                    viewModel.title = binding.titleEditText.text.toString().trim()
                    viewModel.details = binding.detailsEditText.text.toString().trim()
                    viewModel.addTask()
                    hideKeyboard(requireActivity())
                }
            }
        } else {
            viewModel.updateViewState(state)
        }
    }

    private fun toggleEditable(state: Boolean) {
        binding.titleEditText.isEnabled = state
        binding.detailsEditText.isEnabled = state
    }
}