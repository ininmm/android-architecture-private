package com.ininmm.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ininmm.todoapp.data.dao.TasksDao
import com.ininmm.todoapp.data.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {

    abstract fun tasksDao(): TasksDao
}