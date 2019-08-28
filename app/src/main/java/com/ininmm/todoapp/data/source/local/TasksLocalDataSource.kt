package com.ininmm.todoapp.data.source.local

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.dao.TasksDao
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TasksLocalDataSource @Inject constructor(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {
    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tasksDao.getTasks())
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(NullPointerException("Task not found!"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext<Unit>(ioDispatcher) {
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateComplete(task.id, true)
    }

    override suspend fun completeTask(taskId: String) {
        tasksDao.updateComplete(taskId, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateComplete(task.id, false)
    }

    override suspend fun activateTask(taskId: String) {
        tasksDao.updateComplete(taskId, false)
    }

    override suspend fun cleanCompleteTasks() = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        tasksDao.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteTaskById(taskId)
    }
}