package com.ininmm.todoapp.di.module.data

import com.ininmm.todoapp.di.module.data.local.DatabaseModule
import com.ininmm.todoapp.di.module.data.remote.NetworkModule
import dagger.Module

@Module(includes = [DatabaseModule::class, NetworkModule::class])
class DataModule