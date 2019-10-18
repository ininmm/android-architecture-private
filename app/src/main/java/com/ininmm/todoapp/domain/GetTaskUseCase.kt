package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : UseCase<GetTaskUseCase.Params, Task>(ioDispatcher) {

    override suspend fun execute(parameters: GetTaskUseCase.Params): Result<Task> {
        wrapEspressoIdlingResource {
            return tasksRepository.getTask(
                parameters.taskId,
                parameters.forceUpdate
            )
        }
    }

    data class Params(
        val taskId: String,
        val forceUpdate: Boolean = false
    )
}
