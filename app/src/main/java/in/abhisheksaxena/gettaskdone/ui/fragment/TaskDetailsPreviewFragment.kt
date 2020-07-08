package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentTaskDetailsPreviewBinding
import `in`.abhisheksaxena.gettaskdone.util.Constants
import `in`.abhisheksaxena.gettaskdone.util.setupSnackbar
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.TaskDetailsPreviewViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


/**
 * @author Abhishek Saxena
 * @since 02-07-2020 14:15
 */

class TaskDetailsPreviewFragment : Fragment() {

    private lateinit var binding: FragmentTaskDetailsPreviewBinding

    private val viewModel by viewModels<TaskDetailsPreviewViewModel>()

    private lateinit var arguments: TaskDetailsPreviewFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentTaskDetailsPreviewBinding>(
            inflater,
            R.layout.fragment_task_details_preview,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val database = TasksRepository.getRepository(requireNotNull(this.activity).application)
        arguments = TaskDetailsPreviewFragmentArgs.fromBundle(requireArguments())

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        setupFab()
        setupSnackbar()
        setupNavigation()
        setHasOptionsMenu(true)
        viewModel.start(arguments.taskId)
    }


    private fun setupFab() {
        binding.fab.setOnClickListener {
            viewModel.taskOpenEvent()
        }
    }

    private fun setupNavigation() {
        viewModel.taskOpenEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToTaskDetailsFragment()
        })

        viewModel.taskDeleteEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToHomeFragment()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        viewModel.showUserMessage(arguments.userMessage)
    }

    private fun navigateToTaskDetailsFragment() {
        val action =
            TaskDetailsPreviewFragmentDirections.actionTaskDetailsPreviewFragmentToTaskDetailsFragment(
                arguments.taskId,
            getString(R.string.edit_task_label))
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment() {
        val action =
            TaskDetailsPreviewFragmentDirections.actionTaskDetailsPreviewFragmentToHomeFragment(
            Constants.MESSAGE.DELETE_TASK_OK)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_details_preview_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_delete) {
            viewModel.deleteTask()
            true
        } else
            false
    }
}