package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.adapter.TaskListAdapter
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentHomeBinding
import `in`.abhisheksaxena.gettaskdone.db.local.TaskDatabase
import `in`.abhisheksaxena.gettaskdone.viewmodel.AddTaskState
import `in`.abhisheksaxena.gettaskdone.viewmodel.HomeViewModel
import `in`.abhisheksaxena.gettaskdone.viewmodel.factory.HomeViewModelFactory
import android.os.Bundle
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

class HomeFragment : /*BaseFragment<FragmentHomeBinding, HomeViewModel, HomeViewModelFactory>*/
    Fragment() {

    /*
    override var layoutRes: Int = R.layout.fragment_home
    override var modelClass = HomeViewModel::class.java
    override fun getViewModelStoreOwner(): ViewModelStoreOwner? = this
    */

    private lateinit var binding: FragmentHomeBinding
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

        val database = TaskDatabase.getInstance(requireNotNull(this.activity).application)

        val factory = HomeViewModelFactory(database)

        val viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        val adapter = TaskListAdapter()
        binding.tasksRecyclerView.adapter = adapter
        binding.tasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.addItemFloatingActionButton.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToAddTaskFragment(AddTaskState.EDIT_STATE)
            findNavController().navigate(action)
            viewModel.navigateToAddTaskFragment()

        }

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddTaskFragment.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.doneNavigationToAddTask()
            }
        })
    }
}