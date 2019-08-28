package com.ininmm.todoapp.di.module.data.local

import com.ininmm.todoapp.data.ToDoDatabase
import com.ininmm.todoapp.data.ToDoRoomDatabase
import dagger.Binds
import dagger.Module

@Module
abstract class DatabaseModuleBinds {

    @Binds
    abstract fun bindToDoDatabase(roomDatabase: ToDoRoomDatabase): ToDoDatabase
}