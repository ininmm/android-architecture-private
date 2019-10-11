package com.ininmm.todoapp.ui.task

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.ininmm.todoapp.*
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.domain.ActivateTaskUseCase
import com.ininmm.todoapp.domain.ClearCompletedTasksUseCase
import com.ininmm.todoapp.domain.CompleteTaskUseCase
import com.ininmm.todoapp.domain.GetTasksUseCase
import com.ininmm.todoapp.ui.ADD_EDIT_RESULT_OK
import com.ininmm.todoapp.ui.DELETE_RESULT_OK
import com.ininmm.todoapp.ui.EDIT_RESULT_OK
import com.ininmm.todoapp.ui.task.TasksFilterType.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TasksViewModelTest {

    private lateinit var tasksViewModel: TasksViewModel

    @MockK
    private lateinit var tasksRepository: ITasksRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        tasksViewModel = TasksViewModel(
            GetTasksUseCase(tasksRepository, Dispatchers.Main),
            ClearCompletedTasksUseCase(tasksRepository, Dispatchers.Main),
            CompleteTaskUseCase(tasksRepository, Dispatchers.Main),
            ActivateTaskUseCase(tasksRepository, Dispatchers.Main)
        )
    }

    @Test
    fun loadAlTasksFromRepositoryThenLoadingTogglesAndDataLoaded() {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTasks())

        mainCoroutineRule.pauseDispatcher()
        tasksViewModel.setFiltering(ALL_TASKS)
        tasksViewModel.loadTasks(true)
        assertThat(tasksViewModel.dataLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        val result = tasksViewModel.dataLoading.getOrAwaitValue()

        assertThat(result, `is`(false))
        assertThat(tasksViewModel.items.getOrAwaitValue().size, `is`(3))
    }

    @Test
    fun loadActiveTasksFromRepositoryThenLoadIntoView() {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTasks())

        tasksViewModel.setFiltering(ACTIVE_TASKS)

        tasksViewModel.loadTasks(true)

        assertThat(tasksViewModel.dataLoading.getOrAwaitValue(), `is`(false))
        assertThat(tasksViewModel.items.getOrAwaitValue().size, `is`(1))
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() {

        coEvery { tasksRepository.getTasks(true) } returns Success(createTasks())

        tasksViewModel.setFiltering(COMPLETED_TASKS)

        tasksViewModel.loadTasks(true)

        assertFalse(tasksViewModel.dataLoading.getOrAwaitValue())
        assertThat(tasksViewModel.items.getOrAwaitValue().size, `is`(2))
    }

    @Test
    fun loadTasksThenError() {
        coEvery { tasksRepository.getTasks(any()) } throws Exception()

        tasksViewModel.loadTasks(true)

        assertFalse(tasksViewModel.dataLoading.getOrAwaitValue())
        assertTrue(tasksViewModel.items.getOrAwaitValue().isEmpty())

        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.loading_tasks_error)
    }

    @Test
    fun clickOnFabThenShowsAddTasksUi() {
        tasksViewModel.addNewTask()

        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        Truth.assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickOnOpenTaskThenSetsEvent() {
        val taskId = "1234"
        tasksViewModel.openTask(taskId)

        assertLiveDataEventTriggered(tasksViewModel.openTaskEvent, taskId)
    }

    @Test
    fun clearCompletedTasksThenClearsTasks() = mainCoroutineRule.runBlockingTest {
        coEvery { tasksRepository.clearCompletedTasks() } just Runs
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTasks().filter { it.isActive })

        tasksViewModel.clearCompletedTasks()
        tasksViewModel.loadTasks(true)

        val allTasks = tasksViewModel.items.getOrAwaitValue()
        val completedTasks = allTasks.filter { it.isCompleted }

        Truth.assertThat(completedTasks).isEmpty()
        Truth.assertThat(allTasks).hasSize(1)
        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.completed_tasks_cleared)
    }

    @Test
    fun showEditResultMessageAndEditOkThenSnackbarUpdated() {
        tasksViewModel.showEditResultMessage(EDIT_RESULT_OK)

        assertSnackbarmessage(
            tasksViewModel.snackbarMessage,
            R.string.successfully_saved_task_message
        )
    }

    @Test
    fun showEditResultMessageAndAddOkThenSnackbarUpdated() {
        tasksViewModel.showEditResultMessage(ADD_EDIT_RESULT_OK)

        assertSnackbarmessage(
            tasksViewModel.snackbarMessage,
            R.string.successfully_added_task_message
        )
    }

    @Test
    fun showEditResultMessageAndDeleteOkThenSnackbarUpdated() {
        tasksViewModel.showEditResultMessage(DELETE_RESULT_OK)

        assertSnackbarmessage(
            tasksViewModel.snackbarMessage,
            R.string.successfully_deleted_task_message
        )
    }

    @Test
    fun completeTaskThenDataAndSnackbarUpdated() {
        val task = createTasks()[0]
        coEvery { tasksRepository.completeTask(task) } just Runs
        coEvery { tasksRepository.getTasks(any()) } returns Success(emptyList())

        tasksViewModel.completeTask(task, true)

        coVerify { tasksRepository.completeTask(task) }

        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.task_marked_complete)
    }

    @Test
    fun activateTaskThenDataAndSnackbarUpdated() {
        val task = createTasks()[1]
        coEvery { tasksRepository.completeTask(task) } just Runs
        coEvery { tasksRepository.getTasks(any()) } returns Success(emptyList())

        tasksViewModel.completeTask(task, false)

        coVerify { tasksRepository.activateTask(task) }

        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.task_marked_active)
    }

    @Test
    fun getTasksAddViewVisible() {
        tasksViewModel.setFiltering(ALL_TASKS)

        Truth.assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue()).isTrue()
    }

    @After
    fun dropDown() {
    }

    private fun createTasks() = mutableListOf(
        Task("Title1", "Description1"),
        Task("Title2", "Description2", true),
        Task("Title3", "Description3", true)
    )
}