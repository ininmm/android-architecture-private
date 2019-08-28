package com.ininmm.todoapp.data.repository

import com.google.common.truth.Truth
import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.source.TasksDataSource
import com.ininmm.todoapp.succeeded
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.ConnectException

@ExperimentalCoroutinesApi
class TasksRepositoryTest {

    private lateinit var tasksLocalDataSource: TasksDataSource
    private lateinit var tasksRemoteDataSource: TasksDataSource

    private lateinit var tasksRepository: ITasksRepository

    @Before
    fun setup() {

        tasksLocalDataSource = mockk()
        tasksRemoteDataSource = mockk()

        tasksRepository = TasksRepository(
            tasksRemoteDataSource,
            tasksLocalDataSource,
            Dispatchers.Unconfined
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTasksEmptyRepositoryThenReturnRemoteData() = runBlockingTest {

        // GIVEN
        val tasksLocal = listOf(
            Task("Title1", "Description1"),
            Task("Title2", "Description2")
        ).sortedBy { it.id }
        val tasksRemote = listOf(
            Task("Title3", "Description3"),
            Task("Title4", "Description4")
        ).sortedBy { it.id }
        coEvery { tasksRemoteDataSource.getTasks() } returns Success(tasksRemote)
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.getTasks() } returns Success(tasksLocal)

        // WHEN
        val result = tasksRepository.getTasks()

        // THEN
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data[0].title, `is`(tasksRemote[0].title))
        assertThat(result.data[1].title, `is`(tasksRemote[1].title))
    }

    @Test
    fun getTasksFromRepositoryAfterFirstApiCallThenReturnSameResult() = runBlockingTest {
        // GIVEN
        val tasksRemoteFirst = listOf(
            Task("Title1", "Description1"),
            Task("Title2", "Description2")
        ).sortedBy { it.id }
        val tasksRemoteSecond = listOf(
            Task("Title3", "Description3"),
            Task("Title4", "Description4")
        ).sortedBy { it.id }
        coEvery { tasksRemoteDataSource.getTasks() } answers {
            Success(tasksRemoteFirst)
        } andThen {
            Success(tasksRemoteSecond)
        }
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.getTasks() } returns Success(tasksRemoteFirst)

        // WHEN
        val firstResult = tasksRepository.getTasks()

        val secondResult = tasksRepository.getTasks()

        // THEN
        assertThat(firstResult.succeeded, `is`(true))
        assertThat(secondResult.succeeded, `is`(true))
        Truth.assertThat(secondResult).isEqualTo(firstResult)
    }

    @Test
    fun forceGetTasksFromRepositoryThenReturnConnectException() = runBlockingTest {
        // GIVEN
        val tasks = listOf(
            Task("Title1", "Description1"),
            Task("Title2", "Description2")
        ).sortedBy { it.id }
        coEvery { tasksRemoteDataSource.getTasks() } returns Error(IllegalAccessException())
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.getTasks() } returns Success(tasks)

        // WHEN
        val result = tasksRepository.getTasks(true)

        // THEN
        assertThat(result.succeeded, `is`(false))
        result as Error
        assertThat(result.exception is ConnectException, `is`(true))
    }

    @Test
    fun getTaskFromRepositoryCachesThenReturnLocalData() = runBlockingTest {
        // GIVEN
        val taskLocal = Task("Title1", "Description1")
        val taskRemote = Task("Title2", "Description2")
        coEvery { tasksRemoteDataSource.getTask(taskLocal.id) } returns Success(taskLocal)
        coEvery { tasksLocalDataSource.getTask(taskLocal.id) } returns Success(taskRemote)
        coEvery { tasksLocalDataSource.saveTask(taskLocal) } just Runs

        // WHEN
        val result = tasksRepository.getTask(taskLocal.id)

        // THEN
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.id, `is`(taskLocal.id))
    }

    @Test
    fun getTaskFromRepositoryAfterFirstApiCallThenReturnSameResult() = runBlockingTest {
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2")

        coEvery { tasksRemoteDataSource.getTask(task1.id) } returns Success(task1)
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        tasksRepository.getTask(task1.id)

        coEvery { tasksRemoteDataSource.getTask(task2.id) } returns Success(task2)

        val task1SecondTime = tasksRepository.getTask(task1.id)
        val task2SecondTime = tasksRepository.getTask(task2.id)

        assertThat(task1SecondTime.succeeded, `is`(true))
        task1SecondTime as Success
        Truth.assertThat(task1SecondTime.data.id).isEqualTo(task1.id)
        assertThat(task2SecondTime.succeeded, `is`(true))
        task2SecondTime as Success
        Truth.assertThat(task2SecondTime.data.id).isEqualTo(task2.id)
    }

    @Test
    fun forceGetTaskFromRepositoryThenReturnConnectException() = runBlockingTest {
        // GIVEN
        val task = Task("Title1", "Description1")
        coEvery { tasksRemoteDataSource.getTask(task.id) } returns Error(IllegalAccessException())
        coEvery { tasksLocalDataSource.getTask(task.id) } returns Success(task)
        coEvery { tasksLocalDataSource.saveTask(task) } just Runs

        // WHEN
        val result = tasksRepository.getTask(task.id, true)

        // THEN
        assertThat(result.succeeded, `is`(false))
        result as Error
        assertThat(result.exception is ConnectException, `is`(true))
    }

    @Test
    fun getTasksWithDirtyCacheThenForceGetTasksAreRetrievedFromRemote() = runBlockingTest {
        // 先驗證多次呼叫 getTasks 皆是回傳第一次調用時存在 local cache 的 remote 資料
        // 接著強迫 refresh 並驗證呼叫 getTasks 後會獲得新的 remote data

        // GIVEN
        val tasksRemoteFirst = listOf(
            Task("Title1", "Description1"),
            Task("Title2", "Description2")
        ).sortedBy { it.id }
        val tasksRemoteSecond = listOf(
            Task("Title3", "Description3"),
            Task("Title4", "Description4")
        ).sortedBy { it.id }
        coEvery { tasksRemoteDataSource.getTasks() } answers {
            Success(tasksRemoteFirst)
        } andThen {
            Success(tasksRemoteSecond)
        }
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.getTasks() } returns Success(tasksRemoteFirst)

        // WHEN
        val firstResult = tasksRepository.getTasks()
        val cachedTasks = tasksRepository.getTasks()
        // Check tasks are loaded from cache, and also equal to first call remote data
        assertThat(cachedTasks.succeeded, `is`(true))
        Truth.assertThat(cachedTasks).isEqualTo(firstResult)
        cachedTasks as Success
        Truth.assertThat(cachedTasks.data).isEqualTo(tasksRemoteFirst)
        // Now force remote loading
        val refreshedTasks = tasksRepository.getTasks(true)

        // THEN
        assertThat(refreshedTasks.succeeded, `is`(true))
        refreshedTasks as Success
        Truth.assertThat(refreshedTasks.data).isEqualTo(tasksRemoteSecond)
    }

    @Test
    fun saveTaskThenSaveToCacheLocalAndRemote() = runBlockingTest {

        val newTask = Task("Title new", "Description new")
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksRemoteDataSource.saveTask(any()) } just Runs

        tasksRepository.saveTask(newTask)

        coVerify { tasksLocalDataSource.saveTask(any()) }
        coVerify { tasksRemoteDataSource.saveTask(any()) }
    }

    @Test
    fun completeTaskToServiceAPIUpdateCache() = runBlockingTest {
        // GIVEN
        val newTask = Task("Title new", "Description new")
        coEvery { tasksRemoteDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksRemoteDataSource.getTask(newTask.id) } returns Success(newTask)
        coEvery { tasksLocalDataSource.getTask(newTask.id) } returns Success(newTask)
        coEvery {
            tasksLocalDataSource.completeTask(newTask)
        } just Runs
        coEvery {
            tasksRemoteDataSource.completeTask(newTask)
        } just Runs

        // Make sure it's active
        tasksRepository.saveTask(newTask)
        Truth.assertThat((tasksRepository.getTask(newTask.id) as Success).data.isCompleted).isFalse()

        // WHEN
        newTask.isCompleted = true
        tasksRepository.completeTask(newTask.id)

        // THEN
        Truth.assertThat((tasksRepository.getTask(newTask.id) as Success).data.isCompleted).isTrue()
    }

    @Test
    fun completeTaskAndActivateTaskToServiceAPIUpdateCache() = runBlockingTest {
        // GIVEN
        val newTask = Task("Title new", "Description new")
        coEvery { tasksRemoteDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery {
            tasksLocalDataSource.completeTask(newTask)
        } just Runs
        coEvery {
            tasksRemoteDataSource.completeTask(newTask)
        } just Runs
        coEvery { tasksRemoteDataSource.getTask(newTask.id) } returns Success(newTask)
        coEvery { tasksLocalDataSource.getTask(newTask.id) } returns Success(newTask)
        coEvery { tasksRemoteDataSource.activateTask(newTask) } just Runs
        coEvery { tasksLocalDataSource.activateTask(newTask) } just Runs

        // Make sure it's completed
        tasksRepository.saveTask(newTask)
        newTask.isCompleted = true
        tasksRepository.completeTask(newTask.id)
        Truth.assertThat((tasksRepository.getTask(newTask.id) as Success).data.isActive).isFalse()

        // WHEN
        newTask.isCompleted = false
        tasksRepository.activateTask(newTask.id)

        // THEN
        val result = tasksRepository.getTask(newTask.id) as Success
        Truth.assertThat(result.data.isActive).isTrue()
    }

    @Test
    fun deleteAllTasks() = runBlockingTest {
        val activateTask = Task("Title2", "Description2", false)
        val sourceTasks = mutableListOf(activateTask)
        coEvery { tasksRemoteDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksRemoteDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksRemoteDataSource.deleteAllTasks() } just Runs

        val initialTasks = (tasksRepository.getTasks() as? Success)?.data
        tasksRepository.deleteAllTasks()
        coVerify { tasksRemoteDataSource.deleteAllTasks() }
        coVerify { tasksLocalDataSource.deleteAllTasks() }

        coEvery { tasksRemoteDataSource.getTasks() } returns Success(emptyList())
        coEvery { tasksLocalDataSource.getTasks() } returns Success(emptyList())
        val afterDeleteTasks = (tasksRepository.getTasks() as? Success)?.data

        Truth.assertThat(initialTasks).isNotEmpty()
        Truth.assertThat(afterDeleteTasks).isEmpty()
    }

    @Test
    fun clearCompletedTasks() = runBlockingTest {
        val completeTask = Task("Title1", "Description1", true)
        val activateTask = Task("Title2", "Description2", false)
        val sourceTasks = mutableListOf(activateTask)
        coEvery { tasksRemoteDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksRemoteDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksRemoteDataSource.deleteAllTasks() } just Runs
        coEvery { tasksRemoteDataSource.cleanCompleteTasks() } just Runs
        coEvery { tasksLocalDataSource.cleanCompleteTasks() } just Runs

        tasksRepository.clearCompletedTasks()

        coVerify { tasksRemoteDataSource.cleanCompleteTasks() }
        coVerify { tasksLocalDataSource.cleanCompleteTasks() }

        val tasks = (tasksRepository.getTasks() as? Success)?.data

        Truth.assertThat(tasks).hasSize(1)
        Truth.assertThat(tasks).contains(activateTask)
        Truth.assertThat(tasks).doesNotContain(completeTask)
    }

    @Test
    fun deleteSingleTask() = runBlockingTest {
        val completeTask = Task("Title1", "Description1", true)
        val activateTask = Task("Title2", "Description2", false)
        var sourceTasks = mutableListOf(completeTask, activateTask)
        coEvery { tasksRemoteDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.saveTask(any()) } just Runs
        coEvery { tasksRemoteDataSource.saveTask(any()) } just Runs
        coEvery { tasksLocalDataSource.deleteAllTasks() } just Runs
        coEvery { tasksRemoteDataSource.deleteAllTasks() } just Runs
        coEvery { tasksLocalDataSource.deleteTask(activateTask.id) } just Runs
        coEvery { tasksRemoteDataSource.deleteTask(activateTask.id) } just Runs

        val initialTasks = (tasksRepository.getTasks() as? Success)?.data
        tasksRepository.deleteTask(activateTask.id)
        coVerify { tasksRemoteDataSource.deleteTask(activateTask.id) }
        coVerify { tasksLocalDataSource.deleteTask(activateTask.id) }

        sourceTasks = mutableListOf(completeTask)
        coEvery { tasksRemoteDataSource.getTasks() } returns Success(sourceTasks)
        coEvery { tasksLocalDataSource.getTasks() } returns Success(sourceTasks)
        val afterDeleteTasks = (tasksRepository.getTasks() as? Success)?.data

        Truth.assertThat(afterDeleteTasks?.size).isEqualTo(initialTasks!!.size - 1)
        Truth.assertThat(afterDeleteTasks).doesNotContain(activateTask)
    }

    @After
    fun dropDown() {
    }
}