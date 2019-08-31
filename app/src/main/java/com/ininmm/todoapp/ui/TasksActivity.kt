package com.ininmm.todoapp.ui

import android.app.Activity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.ininmm.todoapp.R
import com.ininmm.todoapp.data.ToDoDatabase
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.data.source.local.TasksLocalDataSource
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class TasksActivity : DaggerAppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var roomDatabase: ToDoDatabase

    @Inject
    lateinit var tasksLocalDataSource: TasksLocalDataSource

    @Inject
    lateinit var repository: ITasksRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        AndroidInjection.inject(this)
        initView()

        GlobalScope.launch {
            val task = Task(title = "abc", description = "Haha", isCompleted = false)
            tryIo()

            val tasks = roomDatabase.tasksDao().getTasks()
            Timber.i("All Tasks from dao: $tasks")

            val tasksLocal = tasksLocalDataSource.getTasks()
            Timber.i("All Tasks from local data: $tasksLocal")

            val tasksRepository = repository.getTasks()
            Timber.e("All Tasks from repository: $tasksRepository")
        }
    }

    private suspend fun tryIo() {
        withContext(ioDispatcher) {
            Timber.i("Current Thread: ${Thread.currentThread().name}")
        }
    }

    private fun initView() {
        setupNavigationDrawer()
        setSupportActionBar(findViewById(R.id.toolbar))

        val navController: NavController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.tasksFragment,
            R.id.statisticsFragment
        )
            .setDrawerLayout(drawerLayout)
            .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
    }
}

// Keys fot navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
