package com.ininmm.todoapp.ui.task

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.ininmm.todoapp.Event
import com.ininmm.todoapp.R
import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.domain.ActivateTaskUseCase
import com.ininmm.todoapp.domain.ClearCompletedTasksUseCase
import com.ininmm.todoapp.domain.CompleteTaskUseCase
import com.ininmm.todoapp.domain.GetTasksUseCase
import com.ininmm.todoapp.ui.ADD_EDIT_RESULT_OK
import com.ininmm.todoapp.ui.DELETE_RESULT_OK
import com.ininmm.todoapp.ui.EDIT_RESULT_OK
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val clearCompletedTasksUseCase: ClearCompletedTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val activateTaskUseCase: ActivateTaskUseCase
) : ViewModel() {

    private val _items = MediatorLiveData<List<Task>>().apply {
        value = emptyList()
    }
    val items: LiveData<List<Task>>
        get() = _items

    private val _dataLoading = MediatorLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _currentFilteringLabel = MediatorLiveData<Int>()
    val currentFilteringLabel: LiveData<Int>
        get() = _currentFilteringLabel

    private val _noTasksLabel = MediatorLiveData<Int>()
    val noTasksLabel: LiveData<Int>
        get() = _noTasksLabel

    private val _noTaskIconRes = MediatorLiveData<Int>()
    val noTaskIconRes: LiveData<Int>
        get() = _noTaskIconRes

    private val _tasksAddViewVisible = MediatorLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean>
        get() = _tasksAddViewVisible

    private val _snackbarMessage = MediatorLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarMessage

    private val _openTaskEvent = MediatorLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent

    private val _newTaskEvent = MediatorLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    private var _currentFiltering = TasksFilterType.ALL_TASKS

    val isDataLoadingError = MediatorLiveData<Boolean>()

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        // 設置初始狀態
        setFiltering(TasksFilterType.ALL_TASKS)
    }

    fun loadTasks(forceUpdate: Boolean) {
        _dataLoading.value = true
        isDataLoadingError.removeSource(getTasksUseCase.observe())

        wrapEspressoIdlingResource {
            viewModelScope.launch {
                isDataLoadingError.addSource(getTasksUseCase.observe()) {

                    when (it) {
                        is Success -> {
                            isDataLoadingError.postValue(false)
                            _openTaskEvent.postValue(Event(it.data[0].id))
                            Timber.e("Success:Snackbar post")
                            _items.postValue(it.data)
                        }
                        is Error -> {
                            isDataLoadingError.postValue(true)
                            _openTaskEvent.postValue(Event(it.exception.message.toString()))
                            _items.postValue(emptyList())
                            showSnackbarMessage(R.string.loading_tasks_error)
                            Timber.e("Error:Snackbar post")
                        }
                    }
                    _dataLoading.value = false
                }
                getTasksUseCase.execute(
                    GetTasksUseCase.Params(
                        forceUpdate,
                        _currentFiltering
                    )
                )
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
            clearCompletedTasksUseCase(Unit)
            showSnackbarMessage(R.string.completed_tasks_cleared)
            loadTasks(false)
        }
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            completeTaskUseCase(CompleteTaskUseCase.Params(task))
            showSnackbarMessage(R.string.tasks_marked_complete)
        } else {
            activateTaskUseCase(ActivateTaskUseCase.Params(task))
            showSnackbarMessage(R.string.task_marked_active)
        }

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
        _snackbarMessage.value = Event(message)
    }
}

enum class TasksFilterType {
    ALL_TASKS,
    ACTIVE_TASKS,
    COMPLETED_TASKS
}