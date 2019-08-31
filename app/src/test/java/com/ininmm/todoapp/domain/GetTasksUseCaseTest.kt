package com.ininmm.todoapp.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ininmm.todoapp.LiveDataTestUtil
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.succeeded
import com.ininmm.todoapp.ui.task.TasksFilterType
import com.ininmm.todoapp.ui.task.TasksFilterType.*
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GetTasksUseCaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var tasksRepository: ITasksRepository

    private lateinit var getTasksUseCase: GetTasksUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        getTasksUseCase = GetTasksUseCase(tasksRepository, Dispatchers.Main)
    }

    @Test
    fun loadTasks_noFilter_empty() = runBlockingTest {
        // GIVEN
        coEvery { tasksRepository.getTasks(any()) } returns Success(emptyList())

        // WHEN
        val resultLiveData = getTasksUseCase.observe()
        getTasksUseCase.execute(createParams(true, ALL_TASKS))

        // THEN
        val result = LiveDataTestUtil.getValue(resultLiveData)
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun loadTasks_error() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } throws Exception()

        val resultLiveData = getTasksUseCase.observe()
        getTasksUseCase.execute(createParams(false, ALL_TASKS))

        val result = LiveDataTestUtil.getValue(resultLiveData)
        assertFalse(result.succeeded)
    }

    @Test
    fun loadTasks_noFilter() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTestTasks())

        val resultLiveData = getTasksUseCase.observe()
        getTasksUseCase.execute(createParams(false, ALL_TASKS))

        val result = LiveDataTestUtil.getValue(resultLiveData)
        assertTrue(result.succeeded)
        result as Success
        assertThat(result.data.size, `is`(3))
    }

    @Test
    fun loadTasks_completedFilter() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTestTasks())

        val resultLiveData = getTasksUseCase.observe()
        getTasksUseCase.execute(createParams(false, COMPLETED_TASKS))

        val result = LiveDataTestUtil.getValue(resultLiveData)
        assertTrue(result.succeeded)
        result as Success
        assertThat(result.data.size, `is`(2))
    }

    @Test
    fun loadTasks_activeFilter() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTestTasks())

        val resultLiveData = getTasksUseCase.observe()
        getTasksUseCase.execute(createParams(false, ACTIVE_TASKS))

        val result = LiveDataTestUtil.getValue(resultLiveData)
        assertTrue(result.succeeded)
        result as Success
        assertThat(result.data.size, `is`(1))
    }

    @After
    fun dropDown() {
    }

    private fun createTestTasks() = mutableListOf(
        Task("title1", "desc1", false),
        Task("title2", "desc2", true),
        Task("title3", "desc3", true)
    )

    private fun createParams(
        forceUpdate: Boolean,
        currentFiltering: TasksFilterType
    ) = GetTasksUseCase.Params(forceUpdate, currentFiltering)
}