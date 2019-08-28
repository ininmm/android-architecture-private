package com.ininmm.todoapp.data.source

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.data.model.Task

object FakeFailingTasksRemoteDataSource : TasksDataSource {

    override suspend fun getTasks(): Result<List<Task>> {
        return Error(Exception("Test"))
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return Error(Exception(Exception("Test")))
    }

    override suspend fun saveTask(task: Task) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun completeTask(task: Task) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun completeTask(taskId: String) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun activateTask(task: Task) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun activateTask(taskId: String) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun cleanCompleteTasks() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteAllTasks() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}