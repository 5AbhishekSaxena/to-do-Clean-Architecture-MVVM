package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.adapter.TaskListAdapter
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentHomeBinding
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.data.model.NavData
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
import androidx.recyclerview.widget.LinearLayoutManager


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:09
 */

class HomeFragment : Fragment() {

    private val TAG = javaClass.name

    private lateinit var binding: FragmentHomeBinding

    private val navData = NavData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = TaskDatabase.getInstance(requireNotNull(this.activity).application).taskDao

        val factory = HomeViewModelFactory(database)

        val viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        val lambda: (Long) -> Unit = { id: Long ->
            navData.state = AddTaskState.VIEW_STATE
            navData.id = id
            viewModel.navigateToAddTaskFragment()
        }
        val listener = TaskListAdapter.TaskItemClickListener(lambda)

        val adapter = TaskListAdapter(listener)

        binding.tasksRecyclerView.adapter = adapter
        binding.tasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.fab.setOnClickListener {
            resetNavData()
            viewModel.navigateToAddTaskFragment()
        }

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.isTaskCreated.observe(viewLifecycleOwner, Observer {
            Log.e(TAG, "HomeFragment, isTaskCreated: $it")
            if (it) {
                showSnackBar(binding.coordinatorLayout, getString(R.string.task_created_success))
                viewModel.doneOnTaskCreated()
            }
        })

        viewModel.isTaskDeleted.observe(viewLifecycleOwner, Observer {
            Log.e(TAG, "HomeFragment, isTaskDeleted: $it")
            if (it) {
                showSnackBar(binding.coordinatorLayout, getString(R.string.task_deleted_success))
                viewModel.doneOnTaskDeleted()
            }
        })

        viewModel.navigateToAddTaskFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToTaskDetailsFragment(navData)
                findNavController().navigate(action)
                viewModel.doneNavigationToAddTask()
            }
        })
    }

    private fun resetNavData() {
        navData.id = -1
        navData.state = AddTaskState.NEW_TASK_STATE
    }
}