package com.ininmm.todoapp.ui.task

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.ininmm.todoapp.R
import com.ininmm.todoapp.databinding.FragmentTasksBinding
import com.ininmm.todoapp.util.observeEventNotNull
import com.ininmm.todoapp.util.setupRefreshLayout
import com.ininmm.todoapp.util.setupSnackbar
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class TasksFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TasksViewModel> { viewModelFactory }

    private val args: TasksFragmentArgs by navArgs()

    private lateinit var viewDataBinding: FragmentTasksBinding

    private lateinit var listAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = FragmentTasksBinding.inflate(inflater, container, false).apply {
            this.viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }
            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.tasksRefreshLayout, viewDataBinding.tasksList)
        setupNavigation()
        setupFab()

        viewModel.loadTasks(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarMessage, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                viewModel.loadTasks(false)
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.taskFabAddTask)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
            .actionTasksFragmentToAddEditTaskFragment(
                null,
                resources.getString(R.string.add_task)
            )
        findNavController().navigate(action)
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observeEventNotNull(this) {
            Timber.e("Observe LiveData: $it")
            openTaskDetails(it)
        }
        viewModel.newTaskEvent.observeEventNotNull(this) {
            navigateToAddNewTask()
        }
    }

    private fun openTaskDetails(taskid: String) {
        val action = TasksFragmentDirections
            .actionTasksFragmentToTaskDetailFragment(taskid)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
