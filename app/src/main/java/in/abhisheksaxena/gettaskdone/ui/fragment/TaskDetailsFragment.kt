package `in`.abhisheksaxena.gettaskdone.ui.fragment


import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentTaskDetailsBinding
import `in`.abhisheksaxena.gettaskdone.ui.MainActivity
import `in`.abhisheksaxena.gettaskdone.util.hideKeyboard
import `in`.abhisheksaxena.gettaskdone.util.showSnackBar
import `in`.abhisheksaxena.gettaskdone.viewmodel.AddTaskState
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
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

class TaskDetailsFragment : Fragment() {


    private lateinit var binding: FragmentTaskDetailsBinding
    private lateinit var viewModel: HomeViewModel

    private val TAG = javaClass.name

    private val priorities =
        listOf(Task.TaskPriority.LOW, Task.TaskPriority.NORMAL, Task.TaskPriority.HIGH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentTaskDetailsBinding>(
            inflater,
            R.layout.fragment_task_details,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnBackPressed()
        setupSpinner()

        val database = TaskDatabase.getInstance(requireNotNull(this.activity).application).taskDao
        val arguments = TaskDetailsFragmentArgs.fromBundle(requireArguments())
        Log.e(TAG, "arguments, state: ${arguments.navData}")
        val factory = HomeViewModelFactory(database, arguments.navData)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this.viewLifecycleOwner

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

        binding.prioritySpinner.doOnTextChanged { text, _, _, _ ->
            viewModel.tempTask.priority = text.toString()
            Log.e(
                TAG,
                "priority updated, text: $text, currentTask: ${viewModel.currentTask.value} tempTask: ${viewModel.tempTask}"
            )
        }

        //todo: set via binding
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            Log.e(TAG, "viewState observer called, state: $state")
            state?.let {
                binding.fab.setFabButton(state)
                when (state) {
                    AddTaskState.NEW_TASK_STATE -> {
                        setTitle("Add a Task")
                        toggleEditable(true)
                        toggleDetailsHint(true)
                    }

                    AddTaskState.EDIT_STATE -> {
                        setTitle("Edit Task")
                        toggleEditable(true)
                        toggleDetailsHint(true)
                    }

                    AddTaskState.VIEW_STATE -> {
                        setTitle("View Task")
                        toggleEditable(false)
                        if (viewModel.tempTask.details.isEmpty())
                            toggleDetailsHint(false)
                        else
                            toggleDetailsHint(true)
                    }
                }
            }
        })

        //todo set via two-way binding
        viewModel.currentTask.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.titleEditText.setText(it.title)
                binding.prioritySpinner.setText(it.priority, false)
                binding.detailsEditText.setText(it.details)
                viewModel.tempTask = Task(it)
            }
        })

        setupNavigation()
    }

    private fun setupSpinner() {
        ArrayAdapter(requireContext(), R.layout.drop_down_menu_item, priorities).apply {
            binding.prioritySpinner.setAdapter(this)
            //binding.prioritySpinner.setText(priorities[1], false) //fixme
        }
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TaskDetailsFragmentDirections.actionAddTaskFragmentToHomeFragment()
            findNavController().navigate(action)
        })

        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(
                binding.coordinatorLayout,
                getString(R.string.task_created_success)
            ) //fixme
            //val action = TaskDetailsFragmentDirections.actionAddTaskFragmentToHomeFragment()
            findNavController().navigateUp()
        })

        viewModel.taskUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(binding.coordinatorLayout, getString(R.string.task_update_success)) //fixme
            findNavController().navigateUp()

        })

        viewModel.taskDeletedEvent.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(
                binding.coordinatorLayout,
                getString(R.string.task_deleted_success)
            ) //fixme
            val action = TaskDetailsFragmentDirections.actionAddTaskFragmentToHomeFragment()
            findNavController().navigate(action)
        })
    }

    private fun setTitle(title: String) {
        requireNotNull(activity as MainActivity).supportActionBar?.title = title
    }

    private fun toggleDetailsHint(isEnabled: Boolean) {
        if (isEnabled)
            binding.detailsEditText.hint = getString(R.string.details)
        else
            binding.detailsEditText.hint = ""

    }

    private fun toggleEditable(isEnabled: Boolean) {
        binding.titleEditText.isEnabled = isEnabled
        binding.priorityLayout.isEnabled = isEnabled
        binding.detailsEditText.isEnabled = isEnabled
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
                viewModel.tempTask.title.isEmpty() -> showSnackBar(
                    binding.coordinatorLayout,
                    getString(R.string.title_empty)
                )
                viewModel.tempTask.priority.isEmpty() -> showSnackBar(
                    binding.coordinatorLayout,
                    getString(R.string.priority_empty)
                )
                else -> {
                    viewModel.saveTask()
                    hideKeyboard(requireActivity())
                }
            }
        } else {
            viewModel.updateViewState(AddTaskState.EDIT_STATE)
        }
    }

    private fun handleOnBackPressed() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Handle the back button event'
                    if (viewModel.viewState.value == AddTaskState.EDIT_STATE) {
                        viewModel.updateViewState(AddTaskState.VIEW_STATE)
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}