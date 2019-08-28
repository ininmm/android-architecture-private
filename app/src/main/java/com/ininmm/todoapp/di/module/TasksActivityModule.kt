package com.ininmm.todoapp.di.module

import com.ininmm.todoapp.ui.TasksActivity
import dagger.Binds
import dagger.Module

@Module
abstract class TasksActivityModule {

    @Binds
    abstract fun contributeTasksActivity(tasksActivity: TasksActivity): TasksActivity
}