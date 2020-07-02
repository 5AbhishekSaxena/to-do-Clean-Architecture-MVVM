package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentTaskDetailsPreviewBinding
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels


/**
 * @author Abhishek Saxena
 * @since 02-07-2020 14:15
 */

class TaskDetailsPreviewFragment : Fragment() {

    private lateinit var binding: FragmentTaskDetailsPreviewBinding

    private lateinit var viewModel: HomeViewModel

    private lateinit var arguments: TaskDetailsFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_task_details_preview,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = TaskDatabase.getInstance(requireNotNull(this.activity).application).taskDao
        arguments = TaskDetailsFragmentArgs.fromBundle(requireArguments())
        val factory = HomeViewModelFactory(database, arguments.navData)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = viewModel
        binding.executePendingBindings()
        setupFab()
        setupNavigation()
    }


    private fun setupFab() {
        binding.fab.setOnClickListener {
            viewModel.openTaskEvent()
        }
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver{
            navigateToTaskDetailsFragment()
        })
    }

    private fun navigateToTaskDetailsFragment() {
        val action =
            TaskDetailsPreviewFragmentDirections.
            actionTaskDetailsPreviewFragmentToTaskDetailsFragment(
                arguments.navData.copy(state = AddTaskState.EDIT_STATE)
            )
        findNavController().navigate(action)
    }
}