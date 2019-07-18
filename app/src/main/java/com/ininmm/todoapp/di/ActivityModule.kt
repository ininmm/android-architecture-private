package com.ininmm.todoapp.di

import com.ininmm.todoapp.ui.TasksActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [TasksActivityModule::class])
    abstract fun contributeTasksActivity(): TasksActivity
}