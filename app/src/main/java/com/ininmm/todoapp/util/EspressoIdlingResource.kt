package com.ininmm.todoapp.util

import androidx.test.espresso.IdlingResource

/**
 * 實現一個靜態的 [IdlingResource]，要 mock 的時候才會用到
 */
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = SimpleCountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // TODO: 2019-08-25 Espresso 對 coroutines 還沒有很好的支持
    //  大神還在想辦法 https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment()
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement()
    }
}