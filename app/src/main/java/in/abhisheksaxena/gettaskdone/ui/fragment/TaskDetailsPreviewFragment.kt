package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentTaskDetailsPreviewBinding
import `in`.abhisheksaxena.gettaskdone.util.setupSnackbar
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar


/**
 * @author Abhishek Saxena
 * @since 02-07-2020 14:15
 */

class TaskDetailsPreviewFragment : Fragment() {

    private lateinit var binding: FragmentTaskDetailsPreviewBinding

    private lateinit var viewModel: HomeViewModel

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


        val database = TaskDatabase.getInstance(requireNotNull(this.activity).application).taskDao
        arguments = TaskDetailsPreviewFragmentArgs.fromBundle(requireArguments())
        val factory = HomeViewModelFactory(database, arguments.taskId)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        setupFab()
        setupSnackbar()
        setupNavigation()
        setHasOptionsMenu(true)
    }


    private fun setupFab() {
        binding.fab.setOnClickListener {
            viewModel.openTaskEvent(viewModel.tempTask.id)
        }
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToTaskDetailsFragment()
        })

        viewModel.taskDeletedEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToHomeFragment()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun navigateToTaskDetailsFragment() {
        val action =
            TaskDetailsPreviewFragmentDirections.actionTaskDetailsPreviewFragmentToTaskDetailsFragment(
                arguments.taskId,
            getString(R.string.edit_task_label))
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment() {
        /*val action =
            TaskDetailsPreviewFragmentDirections.(
                -1
            )*/
        findNavController().navigateUp(/*action*/)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_details_preview_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_delete) {
            viewModel.deleteItem()
            true
        } else
            false
    }
}