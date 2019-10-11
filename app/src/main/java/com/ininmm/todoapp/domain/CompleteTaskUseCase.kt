package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : UseCase<CompleteTaskUseCase.Params, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: CompleteTaskUseCase.Params): Result<Unit> {
        wrapEspressoIdlingResource {
            return Success(tasksRepository.completeTask(parameters.task))
        }
    }

    data class Params(val task: Task)
}