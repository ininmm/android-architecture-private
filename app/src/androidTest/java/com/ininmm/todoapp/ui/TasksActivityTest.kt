package com.ininmm.todoapp.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ininmm.todoapp.DaggerTestApplicationRule
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: ITasksRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val rule = DaggerTestApplicationRule()

    @Before
    fun setupDaggerComponent() {
        repository = rule.component.tasksRepository
        repository.deleteAllTasksBlocking()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editTask() {
        repository.saveTaskBlocking(Task("TITLE1", "DESCRIPTION"))

        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // TODO: 2019-10-09 完成 addEditFragment
    }

    @Test
    fun createOneTaskThenDeleteTask() {
        // TODO: 2019-10-09
    }

    fun createTwoTasksThenDeleteOneTask() {
        // TODO: 2019-10-09
    }

    @Test
    fun markTaskAsCompleteOnDetailScreenThenTaskIsCompleteInList() {
        // TODO: 2019-10-09
    }

    @Test
    fun markTaskAsActiveOnDetailScreenThenIsActiveInList() {
        // TODO: 2019-10-09
    }

    @Test
    fun markTaskAsCompleteAndActiveOnDetailScreenThenTaskIsActiveInList() {
        // TODO: 2019-10-09
    }

    @Test
    fun markTaskAsActiveAndCompleteOnDetailScreenThenTaskIsCompleteInList() {
        // TODO: 2019-10-09
    }

    @Test
    fun createTask() {
        // TODO: 2019-10-09
    }
}