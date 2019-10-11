package com.ininmm.todoapp.ui.taskdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.domain.ActivateTaskUseCase
import com.ininmm.todoapp.domain.CompleteTaskUseCase
import com.ininmm.todoapp.domain.DeleteTaskUseCase
import com.ininmm.todoapp.domain.GetTasksUseCase
import javax.inject.Inject

class TaskDetailViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val activateTaskUseCase: ActivateTaskUseCase
) : ViewModel() {

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

//    private val _isDataAvailable = MediatorLiveData<Boolean>()
//    val isDataAvailable: LiveData<Boolean>()
}