package com.ininmm.todoapp.di

import com.ininmm.todoapp.ui.TasksActivity
import dagger.Binds
import dagger.Module

@Module
abstract class TasksActivityModule {

    @Binds
    abstract fun contributeTasksActivity(tasksActivity: TasksActivity): TasksActivity
}