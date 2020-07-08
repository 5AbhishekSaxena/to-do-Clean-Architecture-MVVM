package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.adapter.TaskListAdapter
import `in`.abhisheksaxena.gettaskdone.data.db.TasksRepository
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentHomeBinding
import `in`.abhisheksaxena.gettaskdone.util.Constants
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:09
 */

class HomeFragment : Fragment() {

    private val TAG = javaClass.name

    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel

    private lateinit var arguments: HomeFragmentArgs

    private lateinit var adapter: TaskListAdapter

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

        val database = TasksRepository.getRepository(requireNotNull(this.activity).application)

        val factory = HomeViewModelFactory(database)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner

        arguments = HomeFragmentArgs.fromBundle(requireArguments())

        binding.fab.setOnClickListener {
            viewModel.newTaskEvent()
        }

        setupSnackbar()
        setupObservers()
        setupRecyclerView()
    }

    private fun setupObservers() {
        setupDataObservers()
        setupEventObservers()
    }

    private fun setupEventObservers() {
        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToTaskDetails()
        })

        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToTaskDetailsPreview(it)
        })

        viewModel.taskSwipeToDeletedEvent.observe(viewLifecycleOwner, EventObserver {
            Log.d(TAG, "setupEventObservers - swipeToDelete")
            //adapter.notifyDataSetChanged()
        })
    }

    private fun setupDataObservers() {
        viewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            if (tasks != null && tasks.isNotEmpty()) {
                binding.emptyListTextView.visibility = View.GONE
            } else {
                //binding.tasksRecyclerView.visibility = View.GONE
                binding.emptyListTextView.visibility = View.VISIBLE
            }
            adapter.submitList(tasks)
        })
    }

    private fun setupSnackbar() {
        Log.e(TAG, "setupSnackbar called")
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments.let {
            viewModel.showUserMessage(it.userMessage)
        }
    }

    private fun setupRecyclerView() {
        val lambda: (Long) -> Unit = { id: Long ->
            viewModel.openTaskEvent(id)
        }

        val listener = TaskListAdapter.TaskItemClickListener(lambda)
        adapter = TaskListAdapter(listener)

        binding.tasksRecyclerView.adapter = adapter
        binding.tasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        setupSwipeToDeleteItem()
    }

    private fun setupSwipeToDeleteItem() {
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.swipeToDeleteTask(viewHolder.adapterPosition)
            }
        }.apply {
            ItemTouchHelper(this).attachToRecyclerView(binding.tasksRecyclerView)
        }
    }

    private fun navigateToTaskDetails() {
        val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailsFragment(
            -1,
            getString(R.string.add_task_label)
        )
        findNavController().navigate(action)
    }

    private fun navigateToTaskDetailsPreview(taskId: Long) {
        val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailsPreviewFragment(taskId)
        findNavController().navigate(action)
    }
}