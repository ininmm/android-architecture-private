package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
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
) : UseCase<GetTasksUseCase.Params, List<Task>>(ioDispatcher) {
    override suspend fun execute(parameters: GetTasksUseCase.Params): Result<List<Task>> {

        wrapEspressoIdlingResource {

            val tasksResult = tasksRepository.getTasks(parameters.forceUpdate)
            Timber.e("GetTasksUseCase：$tasksResult")
            // Filter tasks
            if (tasksResult is Success && parameters.currentFiltering != ALL_TASKS) {
                val tasks = tasksResult.data

                val tasksToShow = mutableListOf<Task>()
                // We filter the tasks based on the requestType
                for (task in tasks) {
                    when (parameters.currentFiltering) {
                        ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                        else -> NotImplementedError()
                    }
                }
                return Success(tasksToShow)
            }
            return tasksResult
        }
    }

    data class Params(
        val forceUpdate: Boolean,
        val currentFiltering: TasksFilterType = ALL_TASKS
    )
}