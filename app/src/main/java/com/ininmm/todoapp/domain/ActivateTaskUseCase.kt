package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ActivateTaskUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : UseCase<ActivateTaskUseCase.Params, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: ActivateTaskUseCase.Params): Result<Unit> {
        wrapEspressoIdlingResource {
            return Result.Success(tasksRepository.activateTask(task = parameters.task))
        }
    }

    data class Params(val task: Task)
}