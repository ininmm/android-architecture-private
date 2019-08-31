package com.ininmm.todoapp.ui.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.ininmm.todoapp.R
import com.ininmm.todoapp.util.observe
import com.ininmm.todoapp.util.observeEvent
import com.ininmm.todoapp.util.observeNotNull
import com.ininmm.todoapp.util.setupSnackbar
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

class TasksFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TasksViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.isDataLoadingError.observe(this) {
            Timber.e("Observe isDataLoadingError: $it")
        }
        viewModel.openTaskEvent.observeEvent(this) {
            Timber.e("Observe LiveData: $it")
        }
        viewModel.items.observe(this) {
            Timber.e("Observe LiveData: $it")
        }
        viewModel.dataLoading.observeNotNull(this) {
            Timber.e("Observe dataLoading: $it")
        }
        setupSnackbar()

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
    }
}
