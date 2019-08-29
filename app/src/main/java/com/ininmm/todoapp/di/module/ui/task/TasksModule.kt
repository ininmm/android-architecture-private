package com.ininmm.todoapp.di.module.ui.task

import com.ininmm.todoapp.di.module.ui.FragmentScoped
import com.ininmm.todoapp.ui.task.TasksFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class TasksModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeTasksFragment(): TasksFragment
}