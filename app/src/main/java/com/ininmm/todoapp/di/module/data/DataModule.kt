package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.data.source.TasksDataSource
import com.ininmm.todoapp.data.source.remote.TasksRemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module(
    includes = [
        DataBinds::class,
        RepositoryBinds::class
    ]
)
class DataModule {

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
}