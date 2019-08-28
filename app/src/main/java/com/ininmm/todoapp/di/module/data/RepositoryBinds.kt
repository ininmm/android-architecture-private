package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.data.repository.TasksRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryBinds {

    @Binds
    abstract fun bindToTasksRepository(tasksRepository: TasksRepository): ITasksRepository
}