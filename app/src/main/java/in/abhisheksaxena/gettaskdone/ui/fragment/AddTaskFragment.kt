package `in`.abhisheksaxena.gettaskdone.ui.fragment


import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentAddTaskBinding
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.util.hideKeyboard
import `in`.abhisheksaxena.gettaskdone.util.showSnackBar
import `in`.abhisheksaxena.gettaskdone.viewmodel.AddTaskState
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory
import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
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

        val database = TaskDatabase.getInstance(requireNotNull(this.activity).application).taskDao
        val arguments = AddTaskFragmentArgs.fromBundle(requireArguments())
        Log.e(TAG, "arguments, state: ${arguments.navData}")
        val factory = HomeViewModelFactory(database, arguments.navData)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.tempTask.title = text.toString().trim()
            Log.e(
                TAG,
                "title updated, currentTask: ${viewModel.currentTask.value} tempTask: ${viewModel.tempTask}"
            )

        }
        binding.detailsEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.tempTask.details = text.toString().trim()
            Log.e(
                TAG,
                "details updated, currentTask: ${viewModel.currentTask.value} tempTask: ${viewModel.tempTask}"
            )
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            binding.fab.setFabButton(state)
            if (state == AddTaskState.VIEW_STATE) {
                toggleEditable(false)
            } else {
                toggleEditable(true)
            }
            toggleHint(state)
        })

        viewModel.currentTask.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.titleEditText.setText(it.title)
                binding.detailsEditText.setText(it.details)
                viewModel.tempTask = Task(it)
                Log.e(
                    TAG,
                    "onChange called, current task: ${it}, tempTask: ${viewModel.tempTask}, tempTask==currentTask: ${viewModel.tempTask == it}"
                )
            }
        })

        viewModel.navigateToHomeFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    findNavController().navigate(AddTaskFragmentDirections.actionAddTaskFragmentToHomeFragment())
                    viewModel.doneNavigationToHome()
                }
            }
        })
    }

    private fun toggleHint(state: AddTaskState) {
        if (state == AddTaskState.VIEW_STATE) {
            if (viewModel.tempTask.details.isEmpty()) {
                binding.detailsEditText.hint = ""
            }
        } else
            binding.detailsEditText.hint = getString(R.string.details)

    }

    private fun FloatingActionButton.setFabButton(state: AddTaskState) {
        if (state == AddTaskState.VIEW_STATE) {
            setImageResource(R.drawable.ic_baseline_edit_24)
            setOnClickListener { handleClickEvent(state) }
        } else {
            setImageResource(R.drawable.ic_baseline_save_24)
            setOnClickListener { handleClickEvent(state) }
        }
    }

    private fun handleClickEvent(state: AddTaskState) {
        if (state == AddTaskState.EDIT_STATE || state == AddTaskState.NEW_TASK_STATE) {
            when {
                viewModel.tempTask.title.isEmpty() -> binding.root.showSnackBar(getString(R.string.title_empty))
                else -> {
                    viewModel.addTask()
                    hideKeyboard(requireActivity())
                }
            }
        } else {
            viewModel.updateViewState(AddTaskState.EDIT_STATE)
        }
    }

    private fun toggleEditable(state: Boolean) {
        binding.titleEditText.isEnabled = state
        binding.detailsEditText.isEnabled = state
    }
}