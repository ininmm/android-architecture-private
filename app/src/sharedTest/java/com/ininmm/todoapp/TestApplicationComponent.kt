package com.ininmm.todoapp

import android.content.Context
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.di.ViewModelBuilder
import com.ininmm.todoapp.di.module.data.local.DatabaseModule
import com.ininmm.todoapp.di.module.data.remote.NetworkModule
import com.ininmm.todoapp.di.module.ui.ActivityBindingModule
import com.ininmm.todoapp.di.module.ui.task.TasksModule
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
        ActivityBindingModule::class,
        TasksModule::class,
        ViewModelBuilder::class,
        DatabaseModule::class,
        NetworkModule::class,
        TestDataModule::class
    ]
)
interface TestApplicationComponent : AndroidInjector<TestTodoApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestApplicationComponent
    }

    val tasksRepository: ITasksRepository
}