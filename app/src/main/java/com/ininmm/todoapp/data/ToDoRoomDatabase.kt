package com.ininmm.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ininmm.todoapp.data.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoRoomDatabase : RoomDatabase(), ToDoDatabase