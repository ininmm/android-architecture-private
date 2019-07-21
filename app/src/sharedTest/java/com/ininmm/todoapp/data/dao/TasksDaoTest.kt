package com.ininmm.todoapp.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.ininmm.todoapp.MainCoroutineRule
import com.ininmm.todoapp.data.ToDoDatabase
import com.ininmm.todoapp.data.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    private lateinit var database: ToDoDatabase

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ToDoDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun dropdown() {
        database.close()
    }

    @Test
    fun when_insert_task_then_get_by_id_success() = runBlockingTest {
        // Given
        val task = Task("title", "description")
        database.tasksDao().insertTask(task)

        // When
        val loaded = database.tasksDao().getTaskById(task.id)

        // Then
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun when_insert_test_but_replace_on_conflict_then_get_by_id_success() = runBlockingTest {
        val task = Task("title", "description")
        database.tasksDao().insertTask(task)

        val newTask = Task("title2", "description2", true, task.id)
        database.tasksDao().insertTask(newTask)

        val loaded = database.tasksDao().getTaskById(task.id)
        assertThat(loaded?.id, `is`(task.id))
        assertThat(loaded?.title, `is`(newTask.title))
        assertThat(loaded?.description, `is`(newTask.description))
        assertThat(loaded?.isCompleted, `is`(newTask.isCompleted))
    }

    @Test
    fun when_insert_task_then_get_tasks_success() = runBlockingTest {

        val task = Task("title", "description")
        database.tasksDao().insertTask(task)

        val tasks = database.tasksDao().getTasks()

        assertThat(tasks.size, `is`(1))
        assertThat(tasks[0].id, `is`(task.id))
        assertThat(tasks[0].title, `is`(task.title))
        assertThat(tasks[0].description, `is`(task.description))
        assertThat(tasks[0].isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun when_update_task_then_get_by_id_success() = runBlockingTest {
        val originalTask = Task("title", "description")
        database.tasksDao().insertTask(originalTask)

        val updateTask = Task("new title", "new description", true, originalTask.id)
        database.tasksDao().updateTask(updateTask)

        val loaded = database.tasksDao().getTaskById(originalTask.id)
        assertThat(loaded?.id, `is`(originalTask.id))
        assertThat(loaded?.title, `is`("new title"))
        assertThat(loaded?.description, `is`("new description"))
        assertThat(loaded?.isCompleted, `is`(true))
    }

    @Test
    fun when_update_complete_then_get_by_id_success() = runBlockingTest {
        val task = Task("title", "description", true)
        database.tasksDao().insertTask(task)

        database.tasksDao().updateComplete(task.id, false)

        val loaded = database.tasksDao().getTaskById(task.id)
        assertThat(loaded?.id, `is`(task.id))
        assertThat(loaded?.title, `is`(task.title))
        assertThat(loaded?.description, `is`(task.description))
        assertThat(loaded?.isCompleted, `is`(false))
    }

    @Test
    fun when_delete_task_by_id_then_get_tasks_empty() = runBlockingTest {
        val task = Task("title", "description")
        database.tasksDao().insertTask(task)

        database.tasksDao().deleteTaskById(task.id)

        val tasks = database.tasksDao().getTasks()
        assertThat(tasks.isEmpty(), `is`(true))
    }

    @Test
    fun when_delete_tasks_then_get_tasks_empty() = runBlockingTest {
        val task = Task("title", "description")
        database.tasksDao().insertTask(task)

        database.tasksDao().deleteTasks()

        val tasks = database.tasksDao().getTasks()
        assertThat(tasks.isEmpty(), `is`(true))
    }

    @Test
    fun when_delete_completed_tasks_then_get_tasks_empty() = runBlockingTest {
        val task = Task("completed", "description", true)
        database.tasksDao().insertTask(task)

        database.tasksDao().deleteCOmpletedTasks()

        val tasks = database.tasksDao().getTasks()
        assertThat(tasks.isEmpty(), `is`(true))
    }
}