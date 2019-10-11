package com.ininmm.todoapp.domain

import com.ininmm.todoapp.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * 使用 [CoroutineDispatcher] 處理邏輯的同步與非同步
 */
abstract class UseCase<in P, R>(private val ioDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: P): Result<R> {
        return withContext(ioDispatcher) {
            return@withContext executeNow(parameters)
        }
    }

    /**
     * 同步執行程式，回傳 [Result]
     */
    suspend fun executeNow(parameters: P): Result<R> {
        return try {
            execute(parameters)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * override 此方法並把邏輯寫在這裡
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): Result<R>
}

suspend operator fun <R> UseCase<Unit, R>.invoke(): Result<R> = this(Unit)

suspend fun <R : Any> UseCase<Unit, R>.executeNow(): Result<R> = executeNow(Unit)
