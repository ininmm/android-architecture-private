package com.ininmm.todoapp.util

import androidx.test.espresso.IdlingResource
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

/**
 * [IdlingResource] 的計數器模式實現，計數器歸零時表示閒置，整體思路可參考 [java.util.concurrent.Semaphore]
 * 的行為
 */
class SimpleCountingIdlingResource(private val resourceName: String) : IdlingResource {

    private val counter = AtomicInteger(0)

    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = resourceName

    override fun isIdleNow() = counter.get() == 0

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = resourceCallback
    }

    /**
     * 將進行中的資源增加到監控中
     */
    fun increment() {
        val count = counter.getAndIncrement()
        Timber.i("IdlingResource counter increment: $count.")
    }

    /**
     * 計數器 -1 ，小於 0 發生異常
     *
     * @throws IllegalStateException 當計數器 < 0
     */
    fun decrement() {
        val counterVal = counter.decrementAndGet()
        Timber.i("IdlingResource counter decrement: $counterVal.")
        if (counterVal == 0) {
            // 從非 0 變為 0 ，表示當前閒置，通知 espresso
            resourceCallback?.onTransitionToIdle()
        } else if (counterVal < 0) {
            throw IllegalStateException("Counter has been corrupted!")
        }
    }
}