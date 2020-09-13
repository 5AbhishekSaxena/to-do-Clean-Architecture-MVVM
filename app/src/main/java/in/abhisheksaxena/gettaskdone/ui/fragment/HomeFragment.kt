package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.adapter.TaskListAdapter
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentHomeBinding
import `in`.abhisheksaxena.gettaskdone.ui.base.AbstractFragment
import `in`.abhisheksaxena.gettaskdone.util.Constants
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 11:09
 */

@AndroidEntryPoint
class HomeFragment : AbstractFragment<FragmentHomeBinding, HomeViewModel>() {

    private val TAG = javaClass.name

    override var layoutRes: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var arguments: HomeFragmentArgs

    private lateinit var taskListAdapter: TaskListAdapter

    override fun onViewCreated(savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view?.doOnPreDraw { startPostponedEnterTransition() }

        binding.viewmodel = viewModel
        binding.executePendingBindings()
        arguments = HomeFragmentArgs.fromBundle(requireArguments())

        setHasOptionsMenu(true)
        setupObservers()
        setupRecyclerView()
        setupOnClickListeners()
    }

    private fun setupObservers() {
        setupDataObservers()
        setupEventObservers()
    }

    private fun setupEventObservers() {
        viewModel.newTaskEvent.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailsFragment(
                -1,
                getString(R.string.add_task_label)
            )
            findNavController().navigate(action)
        })
    }

    override fun setupSnackbar() {
        //snackbarText = viewModel.snackbarText
        super.setupSnackbar()
        var actionText = ""
        val action: () -> Unit =
            if (arguments.userMessage == Constants.MESSAGE.DELETE_TASK_OK && arguments.task != null) {
                actionText = "Undo"
                { viewModel.insertTask(arguments.task!!) }
            } else {
                {}
            }

        viewModel.showSnackbarMessage(
            arguments.userMessage,
            action = action,
            actionText = actionText
        )

    }

    private fun setupDataObservers() {
        viewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            Log.e(TAG, "tasks: $tasks")
            taskListAdapter.submitList(tasks)
        })
    }

    private fun setupRecyclerView() {
        taskListAdapter = TaskListAdapter(TaskListAdapter.TaskItemClickListener { id, binding ->
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(
                    R.integer.reply_motion_duration_large
                ).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(
                    R.integer.reply_motion_duration_large
                ).toLong()
            }
            val taskCardDetailTransitionName = getString(
                R.string.task_card_detail_transition_name
            )
            val extras = FragmentNavigatorExtras(
                binding.root to taskCardDetailTransitionName
            )
            val action = HomeFragmentDirections.actionHomeFragmentToTaskDetailsPreviewFragment(id)
            findNavController().navigate(action, extras)
        })

        binding.tasksRecyclerView.adapter = taskListAdapter
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

    private fun setupOnClickListeners() {
        setupFab()

        binding.sortTextView.setOnClickListener {
            //Log.d(TAG, "setupOnClickListeners, clicked")
            viewModel.updateSortOrder()
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            viewModel.newTaskEvent()
        }

        toggleFab(true)
    }

    private fun toggleFab(isVisible: Boolean) {
        if (isVisible)
            binding.fab.visibility = View.VISIBLE
        else
            binding.fab.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.updateSearchText(query)
                return false
            }
        })

        searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(arg0: View?) {
                // search was detached/closed
                viewModel.updateSearchText(null)
                toggleFab(true)
            }

            override fun onViewAttachedToWindow(arg0: View?) {
                // search was opened
                toggleFab(false)
            }
        })
    }
}