package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : UseCase<DeleteTaskUseCase.Params, Unit>(ioDispatcher),
    ITasksRepository by tasksRepository {

    override suspend fun execute(parameters: DeleteTaskUseCase.Params): Result<Unit> {
        wrapEspressoIdlingResource {
            return Success(deleteTask(parameters.taskId))
        }
    }

    class Params(val taskId: String)
}