package com.ininmm.todoapp.domain

import com.ininmm.todoapp.data.repository.ITasksRepository
import com.ininmm.todoapp.util.wrapEspressoIdlingResource
import javax.inject.Inject

class FakeClearUseCase @Inject constructor(
    private val tasksRepository: ITasksRepository
) {
    suspend operator fun invoke() {

        wrapEspressoIdlingResource {
            tasksRepository.clearCompletedTasks()
        }
    }
}