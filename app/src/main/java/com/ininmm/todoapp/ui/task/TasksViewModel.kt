package com.ininmm.todoapp.ui.task

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.ininmm.todoapp.Event
import com.ininmm.todoapp.R
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.api.SearchService
import com.ininmm.todoapp.data.model.SearchRepo
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.domain.*
import com.ininmm.todoapp.ui.ADD_EDIT_RESULT_OK
import com.ininmm.todoapp.ui.DELETE_RESULT_OK
import com.ininmm.todoapp.ui.EDIT_RESULT_OK
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val clearCompletedTasksUseCase: ClearCompletedTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val activateTaskUseCase: ActivateTaskUseCase
) : ViewModel() {

    private val _items = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val items: LiveData<List<Task>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noTasksLabel = MutableLiveData<Int>()
    val noTasksLabel: LiveData<Int> = _noTasksLabel

    private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int> = _noTaskIconRes

    private val _tasksAddViewVisible = MutableLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean> = _tasksAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private var _currentFiltering = TasksFilterType.ALL_TASKS

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    @Inject
    lateinit var service: SearchService

    private val _searchRepo = MutableLiveData<SearchRepo>()
    val searchRepo: LiveData<SearchRepo> = _searchRepo

    init {
        // 設置初始狀態
        setFiltering(TasksFilterType.ALL_TASKS)
        loadTasks(true)
    }

    fun searchRepo() {
        viewModelScope.launch {
            _searchRepo.value = search()
        }
    }

    private suspend fun search() = withContext(Dispatchers.IO) {
        val result = service.searchRepo()
        result
    }

    fun loadTasks(forceUpdate: Boolean) {
        _dataLoading.value = true

        wrapEspressoIdlingResource {
            viewModelScope.launch {
                val tasksResult =
                    getTasksUseCase(GetTasksUseCase.Params(forceUpdate, _currentFiltering))
                if (tasksResult is Success) {
                    isDataLoadingError.value = false
                    Timber.e("loadTasks: ${tasksResult.data}")
                    _items.value = tasksResult.data
                } else {
                    isDataLoadingError.value = false
                    _items.value = emptyList()
                    showSnackbarMessage(R.string.loading_tasks_error)
                }

                _dataLoading.value = false
            }
        }
    }

    fun setFiltering(requestType: TasksFilterType) {
        _currentFiltering = requestType

        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(
                    R.string.label_all,
                    R.string.no_tasks_all,
                    R.drawable.logo_no_fill,
                    true
                )
            }
            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(
                    R.string.label_active,
                    R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp,
                    false
                )
            }
            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(
                    R.string.label_completed,
                    R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp,
                    false
                )
            }
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            clearCompletedTasksUseCase()
            showSnackbarMessage(R.string.completed_tasks_cleared)
            loadTasks(false)
        }
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            completeTaskUseCase(CompleteTaskUseCase.Params(task))
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            activateTaskUseCase(ActivateTaskUseCase.Params(task))
            showSnackbarMessage(R.string.task_marked_active)
        }
        // Refresh list to show the new state
        loadTasks(false)
    }

    /**
     * Called by Data Binding
     */
    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    fun openTask(taskid: String) {
        _openTaskEvent.value = Event(taskid)
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_task_message)
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_task_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_task_message)
        }
    }

    fun refresh() {
        loadTasks(true)
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noTasksLabelString: Int,
        @DrawableRes noTaskIconDrawable: Int,
        tasksAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTasksLabel.value = noTasksLabelString
        _noTaskIconRes.value = noTaskIconDrawable
        _tasksAddViewVisible.value = tasksAddVisible
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }
}

enum class TasksFilterType {
    ALL_TASKS,
    ACTIVE_TASKS,
    COMPLETED_TASKS
}