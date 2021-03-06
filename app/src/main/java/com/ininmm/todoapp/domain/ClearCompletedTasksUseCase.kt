package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ClearCompletedTasksUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(parameters: Unit): Result<Unit> {
        wrapEspressoIdlingResource {
            return Success(tasksRepository.clearCompletedTasks())
        }
    }
}