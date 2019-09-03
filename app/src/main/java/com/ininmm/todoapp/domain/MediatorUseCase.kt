package com.ininmm.todoapp.domain

import androidx.lifecycle.MediatorLiveData
import com.ininmm.todoapp.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * 在 [Dispatchers.IO] 中執行邏輯，並將結果轉為 Result<R> 並發射出去
 * 這種架構下 Handle Exception (emit [Result.Error]) 交由子類負責
 */
abstract class MediatorUseCase<in P, R>(
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected val result = MediatorLiveData<Result<R>>()

    open fun observe(): MediatorLiveData<Result<R>> {
        return result
    }

    abstract suspend fun execute(parameters: P)
}