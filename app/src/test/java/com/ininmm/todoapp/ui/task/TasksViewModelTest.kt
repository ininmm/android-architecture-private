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
    fun loadAlTasksFromRepositoryThenLoadingTogglesAndDataLoaded() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTasks())

        mainCoroutineRule.pauseDispatcher()
        tasksViewModel.setFiltering(ALL_TASKS)
        tasksViewModel.isDataLoadingError.observeForever {}
        tasksViewModel.loadTasks(true)
        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        val result = LiveDataTestUtil.getValue(tasksViewModel.dataLoading)

        assertThat(result, `is`(false))
        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items).size, `is`(3))
    }

    @Test
    fun loadActiveTasksFromRepositoryThenLoadIntoView() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTasks())

        tasksViewModel.setFiltering(ACTIVE_TASKS)

        tasksViewModel.isDataLoadingError.observeForever {}
        tasksViewModel.loadTasks(true)

        assertThat(LiveDataTestUtil.getValue(tasksViewModel.dataLoading), `is`(false))
        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items).size, `is`(1))
    }

    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView() = runBlockingTest {

        coEvery { tasksRepository.getTasks(true) } returns Success(createTasks())

        tasksViewModel.setFiltering(COMPLETED_TASKS)

        tasksViewModel.isDataLoadingError.observeForever {}
        tasksViewModel.loadTasks(true)

        assertFalse(LiveDataTestUtil.getValue(tasksViewModel.dataLoading))
        assertThat(LiveDataTestUtil.getValue(tasksViewModel.items).size, `is`(2))
    }

    @Test
    fun loadTasksThenError() = runBlockingTest {
        coEvery { tasksRepository.getTasks(any()) } throws Exception()

        tasksViewModel.isDataLoadingError.observeForever {}
        tasksViewModel.loadTasks(true)

        assertFalse(LiveDataTestUtil.getValue(tasksViewModel.dataLoading))
        assertTrue(LiveDataTestUtil.getValue(tasksViewModel.items).isEmpty())

        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.loading_tasks_error)
    }

    @Test
    fun clickOnFabThenShowsAddTasksUi() = runBlockingTest {
        tasksViewModel.addNewTask()

        val value = LiveDataTestUtil.getValue(tasksViewModel.newTaskEvent)
        Truth.assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clickOnOpenTaskThenSetsEvent() = runBlockingTest {
        val taskId = "1234"
        tasksViewModel.openTask(taskId)

        assertLiveDataEventTriggered(tasksViewModel.openTaskEvent, taskId)
    }

    @Test
    fun clearCompletedTasksThenClearsTasks() = mainCoroutineRule.runBlockingTest {
        coEvery { tasksRepository.clearCompletedTasks() } just Runs
        coEvery { tasksRepository.getTasks(any()) } returns Success(createTasks().filter { it.isActive })

        tasksViewModel.isDataLoadingError.observeForever {}
        tasksViewModel.clearCompletedTasks()
        tasksViewModel.loadTasks(true)

        val allTasks = LiveDataTestUtil.getValue(tasksViewModel.items)
        val completedTasks = allTasks.filter { it.isCompleted }

        Truth.assertThat(completedTasks).isEmpty()
        Truth.assertThat(allTasks).hasSize(1)
        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.completed_tasks_cleared)
    }

    @Test
    fun showEditResultMessageAndEditOkThenSnackbarUpdated() = runBlockingTest {
        tasksViewModel.showEditResultMessage(EDIT_RESULT_OK)

        assertSnackbarmessage(
            tasksViewModel.snackbarMessage,
            R.string.successfully_saved_task_message
        )
    }

    @Test
    fun showEditResultMessageAndAddOkThenSnackbarUpdated() = runBlockingTest {
        tasksViewModel.showEditResultMessage(ADD_EDIT_RESULT_OK)

        assertSnackbarmessage(
            tasksViewModel.snackbarMessage,
            R.string.successfully_added_task_message
        )
    }

    @Test
    fun showEditResultMessageAndDeleteOkThenSnackbarUpdated() = runBlockingTest {
        tasksViewModel.showEditResultMessage(DELETE_RESULT_OK)

        assertSnackbarmessage(
            tasksViewModel.snackbarMessage,
            R.string.successfully_deleted_task_message
        )
    }

    @Test
    fun completeTaskThenDataAndSnackbarUpdated() = runBlockingTest {
        val task = createTasks()[0]
        coEvery { tasksRepository.completeTask(task) } just Runs

        tasksViewModel.completeTask(task, true)
        coVerify { tasksRepository.completeTask(task) }

        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.tasks_marked_complete)
    }

    @Test
    fun activateTaskThenDataAndSnackbarUpdated() = runBlockingTest {
        val task = createTasks()[1]
        coEvery { tasksRepository.completeTask(task) } just Runs

        tasksViewModel.completeTask(task, false)
        coVerify { tasksRepository.activateTask(task) }

        assertSnackbarmessage(tasksViewModel.snackbarMessage, R.string.task_marked_active)
    }

    @Test
    fun getTasksAddViewVisible() = runBlockingTest {
        tasksViewModel.setFiltering(ALL_TASKS)

        Truth.assertThat(LiveDataTestUtil.getValue(tasksViewModel.tasksAddViewVisible)).isTrue()
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