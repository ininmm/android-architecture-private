package com.ininmm.todoapp

import com.ininmm.todoapp.data.repository.FakeRepository
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.di.module.data.DataSourceModule
import com.ininmm.todoapp.di.module.data.local.DatabaseModule
import com.ininmm.todoapp.di.module.data.remote.NetworkModule
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(
    includes = [
        DataSourceModule::class,
        DatabaseModule::class,
        NetworkModule::class
    ]
)
class TestDataModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksLocalData

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksRemoteData

    @Provides
    @Singleton
    fun provideRepository(): ITasksRepository = FakeRepository()
}