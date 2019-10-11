package com.ininmm.todoapp.di.module.data.remote

import com.ininmm.todoapp.data.api.SearchService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

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