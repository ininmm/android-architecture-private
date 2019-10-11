package com.ininmm.todoapp.ui

import android.view.Gravity
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ininmm.todoapp.DaggerTestApplicationRule
import com.ininmm.todoapp.R
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.getToolbarNavigationContentDescription
import com.ininmm.todoapp.util.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: ITasksRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val rule = DaggerTestApplicationRule()

    @Before
    fun setupDaggerComponent() {
        tasksRepository = rule.component.tasksRepository
        tasksRepository.deleteAllTasksBlocking()
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
    fun drawerNavigationFromTasksToStatistics() {
        val activityScenario = launchActivity()

        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(open())

        onView(withId(R.id.nav_view))
            .perform(navigateTo(R.id.statisticsFragment))

        // TODO: 2019-10-10 檢查 statistic fragment 是否顯示
        onView(withText("Hello blank fragment")).check(matches(isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))
            .perform(open()) // Open Drawer

        // TODO: 2019-10-10 需實作跳轉到 tasks fragment
    }

    @Test
    fun tasksScreenClickOnAndroidHomeIconThenOpenNavigation() {
        val activityScenario = launchActivity()

        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))

        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START)))
    }

    @Test
    fun statisticScreenClickOnAndroidHomeIconThenOpenNavigation() {
        val activityScenario = launchActivity()

        dataBindingIdlingResource.monitorActivity(activityScenario)

        activityScenario.onActivity {
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.statisticsFragment)
        }

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.START)))

        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        onView(withId(R.id.drawer_layout))
            .check(matches(isOpen(Gravity.START)))
    }

    @Test
    fun taskDetailScreenUIBackButton() {
        val task = Task("UI <- button", "Description")
        tasksRepository.saveTaskBlocking(task)
        val activityScenario = launchActivity()

        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withText("UI <- button")).perform(click())

        onView(
            withContentDescription(
                activityScenario
                    .getToolbarNavigationContentDescription()
            )
        ).perform(click())

        onView(withId(R.id.tasksContainer)).check(matches(isDisplayed()))
    }

    @Test
    fun taskDetailScreenBackButton() {
        val task = Task("Back button", "Description")
        tasksRepository.saveTaskBlocking(task)

        val activityScenario = launchActivity()

        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Back button")).perform(click())
        pressBack()
        onView(withId(R.id.tasksContainer)).check(matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<TasksActivity> {
        val activityScenario = launch(TasksActivity::class.java)
        return activityScenario
    }
}