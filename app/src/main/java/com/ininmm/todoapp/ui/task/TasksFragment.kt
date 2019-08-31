package com.ininmm.todoapp.ui.task

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ininmm.todoapp.R
import com.ininmm.todoapp.util.observe
import com.ininmm.todoapp.util.observeEvent
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isDataLoadingError.observe(this) {
            Timber.e("Observe isDataLoadingError: ${it.toString()}")
        }
        viewModel.openTaskEvent.observeEvent(this) {
            Timber.e("Observe LiveData: ${it.toString()}")
        }
        viewModel.items.observe(this) {
            Timber.e("Observe LiveData: ${it.toString()}")
        }
        viewModel.loadTask(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
}
