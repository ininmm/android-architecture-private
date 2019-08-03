package com.ininmm.todoapp.data

import com.ininmm.todoapp.data.dao.TasksDao

interface ToDoDatabase {
    fun tasksDao(): TasksDao
}