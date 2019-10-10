package com.ininmm.todoapp

import android.content.Context
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.di.ViewModelBuilder
import com.ininmm.todoapp.di.module.ui.TasksActivityBinds
import com.ininmm.todoapp.di.module.ui.statistics.StatisticsModule
import com.ininmm.todoapp.di.module.ui.task.TasksModule
import com.ininmm.todoapp.di.module.ui.taskdetail.TaskDetailModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        TestApplicationModule::class,
        AndroidSupportInjectionModule::class,
        TasksActivityBinds::class,
        TasksModule::class,
        TaskDetailModule::class,
        StatisticsModule::class,
        TasksModule::class,
        ViewModelBuilder::class
    ]
)
interface TestApplicationComponent : AndroidInjector<TestTodoApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestApplicationComponent
    }

    val tasksRepository: ITasksRepository
}