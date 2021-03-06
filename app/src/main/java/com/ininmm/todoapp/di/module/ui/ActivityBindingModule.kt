package com.ininmm.todoapp.di.module.ui

import com.ininmm.todoapp.di.module.ui.statistics.StatisticsModule
import com.ininmm.todoapp.di.module.ui.task.TasksModule
import com.ininmm.todoapp.di.module.ui.taskdetail.TaskDetailModule
import com.ininmm.todoapp.ui.TasksActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {
    // Android Activity Injector 依此類推
    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            TasksActivityBinds::class,
            TasksModule::class,
            TaskDetailModule::class,
            StatisticsModule::class
        ]
    )
    abstract fun contributeTasksActivity(): TasksActivity
}