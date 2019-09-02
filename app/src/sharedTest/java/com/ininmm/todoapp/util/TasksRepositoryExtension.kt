package com.ininmm.todoapp.util

import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import kotlinx.coroutines.runBlocking

fun ITasksRepository.saveTaskBlocking(task: Task) = runBlocking {
    this@saveTaskBlocking.saveTask(task)
}

fun ITasksRepository.getTasksBlocking(forceUpdate: Boolean) = runBlocking {
    this@getTasksBlocking.getTasks(forceUpdate)
}

fun ITasksRepository.deleteAllTasksBlocking() = runBlocking {
    this@deleteAllTasksBlocking.deleteAllTasks()
}