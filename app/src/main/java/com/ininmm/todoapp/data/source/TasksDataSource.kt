package com.ininmm.todoapp.data.source

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.data.model.Task

interface TasksDataSource {

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task)

    suspend fun activateTask(taskId: String)

    suspend fun cleanCompleteTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}