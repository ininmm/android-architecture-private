package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.data.repository.TasksRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryBinds {
    @Singleton
    @Binds
    abstract fun bindToTasksRepository(tasksRepository: TasksRepository): ITasksRepository
}