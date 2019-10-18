package com.ininmm.todoapp.ui.taskdetail

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.ininmm.todoapp.Event
import com.ininmm.todoapp.R
import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.domain.ActivateTaskUseCase
import com.ininmm.todoapp.domain.CompleteTaskUseCase
import com.ininmm.todoapp.domain.DeleteTaskUseCase
import com.ininmm.todoapp.domain.GetTaskUseCase
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskDetailViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val activateTaskUseCase: ActivateTaskUseCase
) : ViewModel() {

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskCommand = MutableLiveData<Event<Unit>>()
    val editTaskCommand: LiveData<Event<Unit>> = _editTaskCommand

    private val _deleteTaskCommand = MutableLiveData<Event<Unit>>()
    val deleteTaskCommand: LiveData<Event<Unit>> = _deleteTaskCommand

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private val taskId: String?
        get() = _task.value?.id

    val completed: LiveData<Boolean> = Transformations.map(_task) { input: Task? ->
        input?.isCompleted ?: false
    }

    fun deleteTask() = viewModelScope.launch {
        taskId?.let {
            deleteTaskUseCase(DeleteTaskUseCase.Params(it))
            _deleteTaskCommand.value = Event(Unit)
        }
    }

    fun editTask() {
        _editTaskCommand.value = Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (completed) {
            completeTaskUseCase(CompleteTaskUseCase.Params(task))
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            activateTaskUseCase(ActivateTaskUseCase.Params(task))
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun start(taskId: String?, forceRefresh: Boolean = false) {
        if (_isDataAvailable.value == true && !forceRefresh || _dataLoading.value == true) {
            return
        }

        _dataLoading.value = true

        wrapEspressoIdlingResource {
            viewModelScope.launch {
                if (taskId != null) {
                    getTaskUseCase(GetTaskUseCase.Params(taskId, forceRefresh)).let { result ->
                        if (result is Success) {
                            onTaskLoaded(result.data)
                        } else {
                            onDataNotAvailable(result)
                        }
                    }
                }
                _dataLoading.value = false
            }
        }
    }

    fun refresh() {
        taskId?.let { start(it, true) }
    }

    private fun onTaskLoaded(task: Task?) {
        setTask(task)
    }

    private fun setTask(task: Task?) {
        _task.value = task
        _isDataAvailable.value = task != null
    }

    private fun onDataNotAvailable(result: Result<Task>) {
        _task.value = null
        _isDataAvailable.value = false
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}