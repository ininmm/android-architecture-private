package com.ininmm.todoapp.ui.taskdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.ininmm.todoapp.MainCoroutineRule
import com.ininmm.todoapp.R
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.assertSnackbarmessage
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.domain.ActivateTaskUseCase
import com.ininmm.todoapp.domain.CompleteTaskUseCase
import com.ininmm.todoapp.domain.DeleteTaskUseCase
import com.ininmm.todoapp.domain.GetTaskUseCase
import com.ininmm.todoapp.getOrAwaitValue
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskDetailViewModelTest {

    private lateinit var taskDetailViewModel: TaskDetailViewModel

    @MockK
    private lateinit var tasksRepository: ITasksRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    var task = Task("Title1", "Description1")

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        taskDetailViewModel = TaskDetailViewModel(
            GetTaskUseCase(tasksRepository, Dispatchers.Main),
            DeleteTaskUseCase(tasksRepository, Dispatchers.Main),
            CompleteTaskUseCase(tasksRepository, Dispatchers.Main),
            ActivateTaskUseCase(tasksRepository, Dispatchers.Main)
        )
    }

    @Test
    fun getActiveTaskFromRepositoryAndLoadIntoView() {
        coEvery { tasksRepository.getTask(task.id, any()) } returns Success(task)

        taskDetailViewModel.start(task.id)

        assertThat(taskDetailViewModel.task.getOrAwaitValue().title).isEqualTo(task.title)
        assertThat(taskDetailViewModel.task.getOrAwaitValue().description).isEqualTo(task.description)
    }

    @Test
    fun completeTask() {
        coEvery { tasksRepository.completeTask(task = task) } just Runs
        coEvery { tasksRepository.getTask(task.id, any()) } returns Success(task)

        taskDetailViewModel.start(task.id)
        taskDetailViewModel.setCompleted(true)

        coVerify { tasksRepository.completeTask(task = task) }
        assertSnackbarmessage(taskDetailViewModel.snackbarMessage, R.string.task_marked_complete)
    }

    @Test
    fun activateTask() {
        task.isCompleted = true
        coEvery { tasksRepository.getTask(task.id, any()) } returns Success(task)

        taskDetailViewModel.start(task.id)
        taskDetailViewModel.setCompleted(false)

        coVerify { tasksRepository.activateTask(task = task) }
        assertSnackbarmessage(taskDetailViewModel.snackbarMessage, R.string.task_marked_active)
    }

    @Test
    fun taskDetailViewModelThrowRepositoryError() {
        coEvery { tasksRepository.getTask(task.id, any()) } throws Exception()

        taskDetailViewModel.start(task.id)

        assertThat(taskDetailViewModel.isDataAvailable.getOrAwaitValue()).isFalse()
    }

    @Test
    fun updateSnackbarThenNullValue() {
        val snackbarText = taskDetailViewModel.snackbarMessage.value
        assertThat(snackbarText).isNull()
    }

    @Test
    fun clickOnEditTaskThenSetsEvent() {
        taskDetailViewModel.editTask()

        val value = taskDetailViewModel.editTaskCommand.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun deleteTask() {
        coEvery { tasksRepository.getTask(task.id, any()) } returns Success(task)
        coEvery { tasksRepository.deleteTask(any()) } just Runs

        taskDetailViewModel.start(task.id)
        taskDetailViewModel.deleteTask()

        coVerify { tasksRepository.deleteTask(task.id) }
    }

    @Test
    fun loadTaskAndShowLoading() {
        mainCoroutineRule.pauseDispatcher()

        taskDetailViewModel.start(task.id)
        assertThat(taskDetailViewModel.dataLoading.getOrAwaitValue()).isTrue()
        mainCoroutineRule.resumeDispatcher()

        assertThat(taskDetailViewModel.dataLoading.getOrAwaitValue()).isFalse()
    }

    @After
    fun dropdown() {
    }
}