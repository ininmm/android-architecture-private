package com.ininmm.todoapp.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ininmm.todoapp.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * 使用 [CoroutineDispatcher] 處理邏輯的同步與非同步
 */
abstract class UseCase<in P, R>(private val ioDispatcher: CoroutineDispatcher) {

    /**
     * 非同步處理邏輯並將結果封裝為 [Result] 放入 [MutableLiveData]
     */
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

    /**
     * 非同步處理邏輯並將結果封裝為帶著 [Result] 的 [LiveData] 回傳
     */
    suspend operator fun invoke(parameters: P): LiveData<Result<R>> {
        val liveCallback: MutableLiveData<Result<R>> = MutableLiveData()
        this(parameters, liveCallback)
        return liveCallback
    }

    /**
     * 同步執行程式，回傳 [Result]
     */
    suspend fun executeNow(parameters: P): Result<R> {
        return try {
            Result.Success(execute(parameters))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * override 此方法並把邏輯寫在這裡
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

suspend operator fun <R> UseCase<Unit, R>.invoke(): LiveData<Result<R>> = this(Unit)

suspend operator fun <R> UseCase<Unit, R>.invoke(result: MutableLiveData<Result<R>>) = this(Unit, result)