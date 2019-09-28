package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.data.source.TasksDataSource
import com.ininmm.todoapp.data.source.remote.TasksRemoteDataSource
import dagger.Module
import dagger.Provides

@Module(includes = [DataSourceBinds::class])
class DataSourceModule {

    @DataModule.TasksRemoteData
    @Provides
    fun provideTasksRemoteDataSource(): TasksDataSource {
        return TasksRemoteDataSource
    }
}