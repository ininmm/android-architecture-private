package com.ininmm.todoapp.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.ininmm.todoapp.MainCoroutineRule
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.ToDoRoomDatabase
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.source.local.TasksLocalDataSource
import com.ininmm.todoapp.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for [TasksDataSource]
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var roomDatabase: ToDoRoomDatabase

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        roomDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoRoomDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        localDataSource = TasksLocalDataSource(roomDatabase.tasksDao(), Dispatchers.Main)
    }

    @After
    fun dropDown() {
        roomDatabase.close()
    }

    @Test
    fun when_save_task_then_retrieves_task_success() = runBlockingTest {
        // GIVEN
        val newTask = Task("title", "description", true)
        localDataSource.saveTask(newTask)

        // WHEN
        val result = localDataSource.getTask(newTask.id)

        // THEN
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.isCompleted, `is`(true))
    }

    @Test
    fun when_complete_task_then_retrieved_task_is_complete() = runBlockingTest {
        val newTask = Task("title")
        localDataSource.saveTask(newTask)

        localDataSource.completeTask(newTask)
        val result = localDataSource.getTask(newTask.id)

        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`(newTask.title))
        assertThat(result.data.isCompleted, `is`(true))
    }

    @Test
    fun when_activate_task_then_retrieved_task_is_active() = runBlockingTest {
        val newTask = Task("Some title", "Some description", true)
        localDataSource.saveTask(newTask)

        localDataSource.activateTask(newTask)

        val result = localDataSource.getTask(newTask.id)
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.title, `is`(newTask.title))
        assertThat(result.data.isCompleted, `is`(false))
    }

    @Test
    fun when_clear_completed_task_then_task_not_retrievable() = runBlockingTest {
        // GIVEN 2 completed task and 1 active task
        val newTask1 = Task("title")
        val newTask2 = Task("title2")
        val newTask3 = Task("title3")
        localDataSource.apply {
            saveTask(newTask1)
            completeTask(newTask1)

            saveTask(newTask2)
            completeTask(newTask2)

            saveTask(newTask3)
        }

        // WHEN
        localDataSource.cleanCompleteTasks()

        // THEN the completed cannot be retrieved but the active can
        assertThat(localDataSource.getTask(newTask1.id).succeeded, `is`(false))
        assertThat(localDataSource.getTask(newTask2.id).succeeded, `is`(false))

        val result3 = localDataSource.getTask(newTask3.id)
        assertThat(result3.succeeded, `is`(true))
        result3 as Success
        assertThat(result3.data, `is`(newTask3))
    }

    @Test
    fun when_get_tasks_then_retrieve_save_tasks() = runBlockingTest {
        // GIVEN
        val newTask1 = Task("title1")
        val newTask2 = Task("title2")
        localDataSource.saveTask(newTask1)
        localDataSource.saveTask(newTask2)

        // WHEN
        val result = localDataSource.getTasks()

        // THEN
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.size, `is`(2))
    }

    @Test
    fun when_delete_all_tasks_then_retrieved_empty_task_list() = runBlockingTest {
        val newTask = Task("title")
        localDataSource.saveTask(newTask)

        localDataSource.deleteAllTasks()

        val result = localDataSource.getTasks()
        assertThat(result.succeeded, `is`(true))
        result as Success
        assertThat(result.data.isEmpty(), `is`(true))
    }
}