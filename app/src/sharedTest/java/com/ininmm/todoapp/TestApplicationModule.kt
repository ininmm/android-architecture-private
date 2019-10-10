package com.ininmm.todoapp

import com.ininmm.todoapp.data.repository.FakeRepository
import com.ininmm.todoapp.data.repository.ITasksRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class TestApplicationModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideRepository(): ITasksRepository = FakeRepository()
}