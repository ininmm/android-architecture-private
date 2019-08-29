package com.ininmm.todoapp.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ininmm.todoapp.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class UseCase<in P, R>(private val ioDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P, result: MutableLiveData<Result<R>>) {
        withContext(ioDispatcher) {
            try {
                execute(parameters).let { useCaseResult ->
                    result.postValue(Result.Success(useCaseResult))
                }
            } catch (e: Exception) {
                Timber.d(e)
                result.postValue(Result.Error(e))
            }
        }
    }

    suspend operator fun invoke(parameters: P): LiveData<Result<R>> {
        val liveCallback: MutableLiveData<Result<R>> = MutableLiveData()
        this(parameters, liveCallback)
        return liveCallback
    }

    suspend fun executeNow(parameters: P): Result<R> {
        return try {
            Result.Success(execute(parameters))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

suspend operator fun <R> UseCase<Unit, R>.invoke(): LiveData<Result<R>> = this(Unit)

suspend operator fun <R> UseCase<Unit, R>.invoke(result: MutableLiveData<Result<R>>) = this(Unit, result)