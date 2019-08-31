package com.ininmm.todoapp.di.module.ui.task

import androidx.lifecycle.ViewModel
import com.ininmm.todoapp.di.ViewModelKey
import com.ininmm.todoapp.di.module.ui.FragmentScoped
import com.ininmm.todoapp.ui.task.TasksFragment
import com.ininmm.todoapp.ui.task.TasksViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class TasksModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeTasksFragment(): TasksFragment

    @Binds
    @IntoMap
    @ViewModelKey(TasksViewModel::class)
    abstract fun bindTasksViewModel(viewModel: TasksViewModel): ViewModel
}