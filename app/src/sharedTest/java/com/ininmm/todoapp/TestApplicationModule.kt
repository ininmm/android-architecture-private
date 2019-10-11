package com.ininmm.todoapp

import com.ininmm.todoapp.data.api.SearchService
import com.ininmm.todoapp.data.repository.FakeRepository
import com.ininmm.todoapp.data.repository.ITasksRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class TestApplicationModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideRepository(): ITasksRepository = FakeRepository()

    @Singleton
    @Provides
    fun provideGithubService(): SearchService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchService::class.java)
    }
}