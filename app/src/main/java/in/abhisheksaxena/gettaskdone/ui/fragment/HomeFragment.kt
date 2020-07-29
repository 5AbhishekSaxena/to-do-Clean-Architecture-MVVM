package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.adapter.TaskListAdapter
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentHomeBinding
import `in`.abhisheksaxena.gettaskdone.ui.base.AbstractFragment
import `in`.abhisheksaxena.gettaskdone.util.setupSnackbar
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import android.os.Bundle
import android.view.*
import android.view.View.OnAttachStateChangeListener
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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

    private lateinit var adapter: TaskListAdapter

    override fun onViewCreated(savedInstanceState: Bundle?) {
        binding.viewmodel = viewModel
        binding.executePendingBindings()
        arguments = HomeFragmentArgs.fromBundle(requireArguments())

        setHasOptionsMenu(true)
        setupSnackbar()
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
            navigateToTaskDetails()
        })

        viewModel.openTaskEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToTaskDetailsPreview(it)
        })

        viewModel.taskSwipeToDeletedEvent.observe(viewLifecycleOwner, EventObserver {
            //Log.d(TAG, "setupEventObservers - swipeToDelete")
            //adapter.notifyDataSetChanged()
        })
    }

    private fun setupDataObservers() {
        viewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->
            adapter.submitList(tasks)
        })
    }

    private fun setupSnackbar() {
        //Log.e(TAG, "setupSnackbar called")
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

    private fun setupOnClickListeners(){
        setupFab()

        binding.sortTextView.setOnClickListener{
            //Log.d(TAG, "setupOnClickListeners, clicked")
            viewModel.updateSortOrder()
        }
    }

    private fun setupFab(){
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