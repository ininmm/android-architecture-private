package com.ininmm.todoapp.di.module.ui.taskdetail

import androidx.lifecycle.ViewModel
import com.ininmm.todoapp.di.ViewModelKey
import com.ininmm.todoapp.di.module.ui.FragmentScoped
import com.ininmm.todoapp.ui.taskdetail.TaskDetailFragment
import com.ininmm.todoapp.ui.taskdetail.TaskDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class TaskDetailModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeTaskDetailFragment(): TaskDetailFragment

    @Binds
    @IntoMap
    @ViewModelKey(TaskDetailViewModel::class)
    abstract fun bindTaskDetailViewModel(viewModel: TaskDetailViewModel): ViewModel
}