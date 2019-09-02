package com.ininmm.todoapp

import com.ininmm.todoapp.data.repository.FakeRepository
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.data.source.TasksDataSource
import com.ininmm.todoapp.data.source.remote.TasksRemoteDataSource
import com.ininmm.todoapp.di.module.data.DataBinds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(
    includes = [
        DataBinds::class
    ]
)
class TestDataModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksLocalData

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksRemoteData

    @TasksRemoteData
    @Provides
    fun provideTasksRemoteDataSource(): TasksDataSource {
        return TasksRemoteDataSource
    }

    @Provides
    @Singleton
    fun provideRepository(): ITasksRepository = FakeRepository()
}