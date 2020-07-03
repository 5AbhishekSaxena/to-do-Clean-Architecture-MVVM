package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.adapter.TaskListAdapter
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentHomeBinding
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.util.setupSnackbar
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
import com.google.android.material.snackbar.Snackbar


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:09
 */

class HomeFragment : Fragment() {

    private val TAG = javaClass.name

    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel

    //private lateinit var arguments: HomeFragmentArgs

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

        val factory = HomeViewModelFactory(database, -1)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner

        val lambda: (Long) -> Unit = { id: Long ->
            viewModel.openTaskEvent(id)
        }

        val listener = TaskListAdapter.TaskItemClickListener(lambda)
        val adapter = TaskListAdapter(listener)

        binding.tasksRecyclerView.adapter = adapter
        binding.tasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.fab.setOnClickListener {
            viewModel.newTaskEvent()
        }

        viewModel.tasks.observe(viewLifecycleOwner, Observer {tasks ->
            if (tasks != null && tasks.isNotEmpty()) {
                binding.emptyListTextView.visibility = View.GONE
                adapter.submitList(tasks)
            }else
                binding.emptyListTextView.visibility = View.VISIBLE
        })

        setupNavigation()
        setupSnackbar()
    }

    private fun setupNavigation(){

        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver{
            navigateToTaskDetails()
        })

        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToTaskDetailsPreview(it)
        })
    }

    private fun setupSnackbar(){
        Log.e(TAG, "setupSnackbar called")
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let{

        }
    }

    private fun navigateToTaskDetails() {
            val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailsFragment(-1, getString(R.string.add_task_label))
            findNavController().navigate(action)
    }

    private fun navigateToTaskDetailsPreview(taskId: Long) {
            val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailsPreviewFragment(taskId)
            findNavController().navigate(action)
    }
}