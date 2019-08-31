package com.ininmm.todoapp.domain

import androidx.lifecycle.MediatorLiveData
import com.ininmm.todoapp.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class MediatorUseCase<in P, R>(
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected val result = MediatorLiveData<Result<R>>()

    open fun observe(): MediatorLiveData<Result<R>> {
        return result
    }

    abstract suspend fun execute(parameters: P)
}