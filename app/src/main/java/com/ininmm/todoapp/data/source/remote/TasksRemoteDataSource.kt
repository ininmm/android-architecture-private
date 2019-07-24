package com.ininmm.todoapp.data.source.remote

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.*
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.source.TasksDataSource
import kotlinx.coroutines.delay

object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLS = 2000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost.")
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASKS_SERVICE_DATA[newTask.id] = newTask
    }

    override suspend fun getTasks(): Result<List<Task>> {
        // 模擬 API 等待一段時間後回應
        val tasks = TASKS_SERVICE_DATA.values.toList()
        delay(SERVICE_LATENCY_IN_MILLS)
        return Success(tasks)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        delay(SERVICE_LATENCY_IN_MILLS)
        TASKS_SERVICE_DATA[taskId]?.let {
            return Success(it)
        }
        return Error(NullPointerException("Task not found."))
    }

    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        TASKS_SERVICE_DATA[task.id] = completedTask
    }

    override suspend fun completeTask(taskId: String) {
        // fixme : Not required
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        TASKS_SERVICE_DATA[task.id] = activeTask
    }

    override suspend fun activateTask(taskId: String) {
        // fixme : Not required
    }

    override suspend fun cleanCompleteTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}