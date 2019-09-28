package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.di.module.data.local.DatabaseModule
import com.ininmm.todoapp.di.module.data.remote.NetworkModule
import dagger.Module
import javax.inject.Qualifier

@Module(
    includes = [
        DataSourceModule::class,
        RepositoryBinds::class,
        DatabaseModule::class,
        NetworkModule::class
    ]
)
class DataModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksLocalData

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TasksRemoteData
}