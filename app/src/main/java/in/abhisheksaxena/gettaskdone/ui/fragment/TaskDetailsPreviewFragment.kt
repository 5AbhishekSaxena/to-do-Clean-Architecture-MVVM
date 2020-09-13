package `in`.abhisheksaxena.gettaskdone.ui.fragment

import `in`.abhisheksaxena.gettaskdone.EventObserver
import `in`.abhisheksaxena.gettaskdone.R
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import `in`.abhisheksaxena.gettaskdone.databinding.FragmentTaskDetailsPreviewBinding
import `in`.abhisheksaxena.gettaskdone.ui.base.AbstractFragment
import `in`.abhisheksaxena.gettaskdone.util.Constants
import `in`.abhisheksaxena.gettaskdone.viewmodel.TaskDetailsPreviewViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author Abhishek Saxena
 * @since 02-07-2020 14:15
 */

@AndroidEntryPoint
class TaskDetailsPreviewFragment :
    AbstractFragment<FragmentTaskDetailsPreviewBinding, TaskDetailsPreviewViewModel>() {

    override var layoutRes: Int = R.layout.fragment_task_details_preview
    override val viewModel: TaskDetailsPreviewViewModel by viewModels()

    private lateinit var arguments: TaskDetailsPreviewFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // The drawing view is the id of the view above which the container transform
            // will play in z-space.
            drawingViewId = R.id.default_navHost_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            // Set the color of the scrim to transparent as we also want to animate the
            // list fragment out of view
            scrimColor = Color.TRANSPARENT
            // setAllContainerColors(requireContext().themeColor(R.attr.colorSurface)) fixme
        }
    }


    override fun onViewCreated(savedInstanceState: Bundle?) {

        arguments = TaskDetailsPreviewFragmentArgs.fromBundle(requireArguments())

        binding.viewmodel = viewModel
        binding.executePendingBindings()

        setupFab()
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
            navigateToHomeFragment(it)
        })
    }

    override fun setupSnackbar() {
        //view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        //Log.d(javaClass.name, "setupSnackbar, userMessage: ${arguments.userMessage}")
        super.setupSnackbar()

        viewModel.showSnackbarMessage(arguments.userMessage)

    }

    private fun navigateToTaskDetailsFragment() {
        val action =
            TaskDetailsPreviewFragmentDirections.actionTaskDetailsPreviewFragmentToTaskDetailsFragment(
                arguments.taskId,
                getString(R.string.edit_task_label)
            )
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment(task: Task) {
        val action =
            TaskDetailsPreviewFragmentDirections.actionTaskDetailsPreviewFragmentToHomeFragment(
                Constants.MESSAGE.DELETE_TASK_OK,
                task
            )
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