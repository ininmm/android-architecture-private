package com.ininmm.todoapp.di.module.data.local

import android.content.Context
import androidx.room.Room
import com.ininmm.todoapp.data.ToDoDatabase
import com.ininmm.todoapp.data.ToDoRoomDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModuleBinds::class])
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(context: Context): ToDoRoomDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ToDoRoomDatabase::class.java,
            "Task.db"
        ).build()
    }

    @Provides
    fun provideTasksDao(db: ToDoDatabase) = db.tasksDao()
}