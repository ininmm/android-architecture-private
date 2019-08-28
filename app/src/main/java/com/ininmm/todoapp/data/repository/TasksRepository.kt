package com.ininmm.todoapp.data.repository

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.source.TasksDataSource
import com.ininmm.todoapp.di.module.data.DataModule
import com.ininmm.todoapp.util.EspressoIdlingResource
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import timber.log.Timber
import java.net.ConnectException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

class TasksRepository @Inject constructor(
    @DataModule.TasksRemoteData private val tasksRemoteDataSource: TasksDataSource,
    @DataModule.TasksLocalData private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ITasksRepository {

    private var cachedTasks: ConcurrentMap<String, Task>? = null

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                if (!forceUpdate) {
                    cachedTasks?.let { cachedTasks ->
                        return@withContext Success(cachedTasks.values.sortedBy { it.id })
                    }
                }

                val newTask = fetchTasksFromRemoteOrLocal(forceUpdate)

                (newTask as? Success)?.let { refreshCache(it.data) }

                cachedTasks?.values?.let { tasks ->
                    return@withContext Success(tasks.sortedBy { it.id })
                }

                (newTask as? Success)?.let {
                    if (it.data.isEmpty()) {
                        return@withContext Success(it.data)
                    }
                }

                (newTask as? Error)?.let {
                    return@withContext it
                }

                return@withContext Error(Exception("Illegal state"))
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                if (!forceUpdate) {
                    getTaskWithId(taskId)?.let {
                        EspressoIdlingResource.decrement()
                        return@withContext Success(it)
                    }
                }

                val newTask = fetchSingleTaskFromRemoteOrLocal(taskId, forceUpdate)

                (newTask as? Success)?.let { cacheTask(it.data) }

                (newTask as? Error)?.let {
                    return@withContext it
                }

                return@withContext newTask
            }
        }
    }

    override suspend fun saveTask(task: Task) {
        cacheAndPerform(task) {
            coroutineScope {
                launch { tasksRemoteDataSource.saveTask(it) }
                launch { tasksLocalDataSource.saveTask(it) }
            }
        }
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        cacheAndPerform(task) {
            it.isCompleted = true
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(it) }
                launch { tasksRemoteDataSource.completeTask(it) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            getTaskWithId(taskId)?.let {
                completeTask(it)
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        cacheAndPerform(task) {
            it.isCompleted = false
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(it) }
                launch { tasksLocalDataSource.activateTask(it) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            getTaskWithId(taskId)?.let {
                activateTask(it)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksRemoteDataSource.cleanCompleteTasks() }
            launch { tasksLocalDataSource.cleanCompleteTasks() }
        }
        withContext(ioDispatcher) {
            cachedTasks?.entries?.removeAll { it.value.isCompleted }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteAllTasks() }
                launch { tasksLocalDataSource.deleteAllTasks() }
            }
        }
        cachedTasks?.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { tasksRemoteDataSource.deleteTask(taskId) }
            launch { tasksLocalDataSource.deleteTask(taskId) }
        }

        cachedTasks?.remove(taskId)
    }

    private suspend fun fetchTasksFromRemoteOrLocal(forceUpdate: Boolean): Result<List<Task>> {
        val remoteTasks = tasksRemoteDataSource.getTasks()

        when (remoteTasks) {
            is Success -> {
                refreshLocalDataSource(remoteTasks.data)
                return remoteTasks
            }
            is Error -> Timber.w("Remote data source fetch failed")
            else -> throw IllegalStateException()
        }

        if (forceUpdate) {
            return Error(ConnectException("Can' t force refresh: remote data source is unavailable."))
        }

        val localTasks = tasksLocalDataSource.getTasks()
        if (localTasks is Success) return localTasks
        return Error(Exception("Error fetching from remote and local"))
    }

    private suspend fun fetchSingleTaskFromRemoteOrLocal(
        taskId: String,
        forceUpdate: Boolean
    ): Result<Task> {
        val remoteTask = tasksRemoteDataSource.getTask(taskId)
        when (remoteTask) {
            is Success -> {
                refreshLocalDataSource(remoteTask.data)
                return remoteTask
            }
            is Error -> Timber.w("Remote data source fetch failed")
            else -> throw IllegalStateException()
        }

        if (forceUpdate) {
            return Error(ConnectException("Refresh failed"))
        }

        val localTask = tasksLocalDataSource.getTask(taskId)
        if (localTask is Success) return localTask
        return Error(Exception("Error fetching from remote and local"))
    }

    private suspend fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        tasks.forEach {
            tasksLocalDataSource.saveTask(it)
        }
    }

    private suspend fun refreshLocalDataSource(task: Task) {
        tasksLocalDataSource.saveTask(task)
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks?.clear()
        tasks.sortedBy { it.id }.forEach {
            cacheAndPerform(it)
        }
    }

    private fun getTaskWithId(id: String) = cachedTasks?.get(id)

    private fun cacheTask(task: Task): Task {
        val cachedTask = Task(task.title, task.description, task.isCompleted, task.id)
        if (cachedTasks == null) {
            cachedTasks = ConcurrentHashMap()
        }
        cachedTasks?.put(cachedTask.id, cachedTask)
        return cachedTask
    }

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit = {}) {
        val cachedTask = cacheTask(task)
        perform(cachedTask)
    }
}