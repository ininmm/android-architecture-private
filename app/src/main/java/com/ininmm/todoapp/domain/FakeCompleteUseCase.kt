package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result.Error
import com.ininmm.todoapp.Result.Success
import com.ininmm.todoapp.data.model.Task
import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FakeCompleteUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository,
    ioDispatcher: CoroutineDispatcher
) : MediatorUseCase<Task, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: Task) {

        wrapEspressoIdlingResource {
            try {

                tasksRepository.completeTask(parameters)
                result.postValue(Success(Unit))
            } catch (e: Exception) {
                result.postValue(Error(e))
            }
        }
    }
}