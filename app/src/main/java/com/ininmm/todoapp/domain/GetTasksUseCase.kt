package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.ui.task.TasksFilterType
import com.ininmm.todoapp.ui.task.TasksFilterType.*
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : MediatorUseCase<GetTasksUseCase.Params, List<Task>>(ioDispatcher) {
    override suspend fun execute(parameters: Params) {
        wrapEspressoIdlingResource {

            val tasksResult = tasksRepository.getTasks(parameters.forceUpdate)

            if (tasksResult is Success && parameters.currentFiltering != ALL_TASKS) {
                val tasks = tasksResult.data

                val tasksToShow = mutableListOf<Task>()

                for (task in tasks) {
                    when (parameters.currentFiltering) {
                        ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                        else -> result.postValue(Error(Exception("NotImplemented")))
                    }
                }
                result.postValue(Success(tasksToShow))
            }
            result.postValue(tasksResult)
            Timber.e("postValue")
        }
    }

    data class Params(
        val forceUpdate: Boolean,
        val currentFiltering: TasksFilterType = ALL_TASKS
    )
}