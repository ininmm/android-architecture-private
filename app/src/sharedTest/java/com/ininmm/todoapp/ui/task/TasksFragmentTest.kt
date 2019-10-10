package com.ininmm.todoapp.ui.task

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.ininmm.todoapp.DaggerTestApplicationRule
import com.ininmm.todoapp.R
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.ui.TasksActivity
import com.ininmm.todoapp.util.deleteAllTasksBlocking
import com.ininmm.todoapp.util.saveTaskBlocking
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

// TODO - Use FragmentScenario, see: https://github.com/android/android-test/issues/291
@RunWith(AndroidJUnit4::class)
@MediumTest
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
@ExperimentalCoroutinesApi
class TasksFragmentTest {

    private lateinit var repository: ITasksRepository

    @get:Rule
    val rule = DaggerTestApplicationRule()

    @MockK(relaxUnitFun = true)
    private lateinit var navController: NavController

    @Before
    fun setupDaggerComponent() {
        MockKAnnotations.init(this)
        repository = rule.component.tasksRepository
        repository.deleteAllTasksBlocking()
    }

    @Test
    fun displayTaskWhenRepositoryHasData() {
        // GIVEN
        repository.saveTaskBlocking(createTasks().first())

        // WHEN
        launchActivity()

        // THEN
        onView(withText("Title1")).check(matches(isDisplayed()))
    }

    @Test
    fun displayActiveTask() {
        repository.saveTaskBlocking(createTasks().first())

        launchActivity()

        onView(withText("Title1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).check(matches(isDisplayed()))
    }

    @Test
    fun displayCompletedTask() {
        repository.saveTaskBlocking(createTasks()[1])

        launchActivity()

        onView(withText("Title2")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("Title2")).check(matches(not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("Title2")).check(matches(isDisplayed()))
    }

    @Test
    fun deleteOneTask() {
        repository.saveTaskBlocking(createTasks().first())

        launchActivity()

        onView(withText("Title1")).perform(click())
        // TODO: 2019-09-02 需要實作 TasksDetail
    }

    @Test
    fun deleteOneOfTwoTasks() {
        // TODO: 2019-09-02 需要實作 TasksDetail
    }

    @Test
    fun markTaskAsComplete() {
        repository.saveTaskBlocking(createTasks().first())

        launchActivity()

        // mark the task as complete
        onView(checkboxWithText("Title1")).perform(click())

        // Verify task is shown as complete
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("Title1")).check(matches(not(isDisplayed())))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))
    }

    @Test
    fun markTaskAsActive() {
        repository.saveTaskBlocking(createTasks()[1])

        launchActivity()

        onView(checkboxWithText("Title2")).perform(click())

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("Title2")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("Title2")).check(matches(isDisplayed()))

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("Title2")).check(matches(not(isDisplayed())))
    }

    @Test
    fun showAllTasks() {
        val tasks = createTasks()
        repository.saveTaskBlocking(tasks.first())
        repository.saveTaskBlocking(tasks[1])

        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(matches(isDisplayed()))
    }

    @Test
    fun showActiveTasks() {
        val tasks = createTasks()
        repository.saveTaskBlocking(tasks[0])
        repository.saveTaskBlocking(tasks[1])
        repository.saveTaskBlocking(tasks[2])

        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(doesNotExist())
        onView(withText("Title3")).check(doesNotExist())
    }

    @Test
    fun showCompletedTasks() {
        val tasks = createTasks()
        repository.saveTaskBlocking(tasks[0])
        repository.saveTaskBlocking(tasks[1])
        repository.saveTaskBlocking(tasks[2])

        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())
        onView(withText("Title1")).check(doesNotExist())
        onView(withText("Title2")).check(matches(isDisplayed()))
        onView(withText("Title3")).check(matches(isDisplayed()))
    }

    @Test
    fun clearCompletedTasks() {
        val tasks = createTasks()
        repository.saveTaskBlocking(tasks[0])
        repository.saveTaskBlocking(tasks[1])

        launchActivity()

        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.menu_clear)).perform(click())

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())

        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(doesNotExist())
    }

    @Test
    fun whenNoTasksAndClickAllTasksFilterThenShowNoTasks() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())

        onView(withText(R.string.no_tasks_all)).check(matches(isDisplayed()))
    }

    @Test
    fun whenNoTasksAndClickCompletedTasksFilterThenShowNoCompletedTasks() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_completed)).perform(click())

        onView(withText(R.string.no_tasks_completed)).check(matches(isDisplayed()))
    }

    @Test
    fun whenNoTasksAndClickActiveTasksFilterThenShowNoActiveTasks() {
        launchActivity()

        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_active)).perform(click())

        onView(withText(R.string.no_tasks_active)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAddTaskButtonThenNavigateToAddEditFragment() {
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.taskFabAddTask)).perform(click())

        val navDirections = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
            null,
            getApplicationContext<Context>().getString(R.string.add_task)
        )
        every { navController.navigate(navDirections) } just Runs
        verify { navController.navigate(navDirections) }
    }

    private fun checkboxWithText(text: String): Matcher<View>? {
        return allOf(withId(R.id.itemComplete), hasSibling(withText(text)))
    }

    private fun launchActivity(): ActivityScenario<TasksActivity>? {
        val activityScenario = launch(TasksActivity::class.java)
        activityScenario.onActivity { activity ->
            // 因為現在的 Activity 在開啟時都會打開許多渲染及預設的動畫效果，
            // 我們可以在這裡關閉一些不必要的功能以縮短測試執行時間
            (activity.findViewById(R.id.tasksList) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun createTasks() = mutableListOf(
        Task("Title1", "Description1"),
        Task("Title2", "Description2", true),
        Task("Title3", "Description3", true)
    )
}