package `in`.abhisheksaxena.gettaskdone.ui.fragment


import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentTaskDetailsBinding
import `in`.abhisheksaxena.gettaskdone.util.Constants
import `in`.abhisheksaxena.gettaskdone.util.HideSoftKeyboardOnFocusChange
import `in`.abhisheksaxena.gettaskdone.util.hideKeyboard
import `in`.abhisheksaxena.gettaskdone.util.setupSnackbar
import `in`.abhisheksaxena.gettaskdone.viewmodel.TaskDetailsViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:43
 */

class TaskDetailsFragment : Fragment() {


    private lateinit var binding: FragmentTaskDetailsBinding
    private val viewModel by viewModels<TaskDetailsViewModel>()

    private lateinit var arguments: TaskDetailsFragmentArgs

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

        val database = TasksRepository.getRepository(requireNotNull(this.activity).application)
        arguments = TaskDetailsFragmentArgs.fromBundle(requireArguments())
        //Log.e(TAG, "arguments, state: ${arguments.navData}")
        binding.lifecycleOwner = viewLifecycleOwner

        //todo set via two-way binding
        viewModel.currentTask.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.titleEditText.setText(it.title)
                binding.prioritySpinner.setText(it.priority, false)
                binding.detailsEditText.setText(it.details)
                viewModel.tempTask = Task(it)
            }
        })

        setupSpinner()
        setupSnackbar()
        setupFabButton()
        setupListeners()
        setupNavigation()
        viewModel.start(arguments.taskId)
        //handleOnBackPressed()
    }

    private fun setupSpinner() {
        ArrayAdapter(requireContext(), R.layout.drop_down_menu_item, priorities).apply {
            binding.prioritySpinner.setAdapter(this)
            //binding.prioritySpinner.setText(priorities[1], false) //fixme
        }
    }

    private fun setupNavigation() {
        viewModel.taskCreateEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                TaskDetailsFragmentDirections.actionAddTaskFragmentToHomeFragment(Constants.MESSAGE.ADD_TASK_OK)
            findNavController().navigate(action)
        })

        viewModel.taskUpdateEvent.observe(viewLifecycleOwner, EventObserver {
            var message =
            if (it)
                Constants.MESSAGE.UPDATE_TASK_OK
            else
                Constants.MESSAGE.UPDATE_TASK_NOT_OK

            val action =
                TaskDetailsFragmentDirections.actionTaskDetailsFragmentToTaskDetailsPreviewFragment(
                    arguments.taskId,
                    message
                )
            findNavController().navigate(action)

        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }


    private fun setupListeners() {
        setTextChangeListeners()
        setFocusChangeListeners()
    }

    private fun setFocusChangeListeners() {
        binding.prioritySpinner.onFocusChangeListener =
            HideSoftKeyboardOnFocusChange(requireActivity(), true)
    }

    private fun setTextChangeListeners() {
        binding.titleEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.tempTask.title = text.toString().trim()
        }

        binding.detailsEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.tempTask.details = text.toString().trim()
        }

        binding.prioritySpinner.doOnTextChanged { text, _, _, _ ->
            viewModel.tempTask.priority = text.toString()
        }
    }

    private fun setupFabButton() {
        binding.fab.setImageResource(R.drawable.ic_baseline_save_24)
        binding.fab.setOnClickListener { handleClickEvent() }
    }

    private fun handleClickEvent() {
        viewModel.saveTask()
        hideKeyboard(requireActivity())
    }

    /*//fixme - handle new navigation
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
    }*/

    override fun onPause() {
        super.onPause()
        hideKeyboard(requireActivity())
    }
}