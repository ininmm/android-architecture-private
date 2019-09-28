package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.data.source.TasksDataSource
import com.ininmm.todoapp.data.source.local.TasksLocalDataSource
import dagger.Binds
import dagger.Module

@Module
abstract class DataSourceBinds {

    @DataModule.TasksLocalData
    @Binds
    abstract fun bindToTasksLocalData(tasksLocalDataSource: TasksLocalDataSource): TasksDataSource
}

