package com.ininmm.todoapp

import androidx.lifecycle.LiveData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`

fun assertLiveDataEventTriggered(
    liveData: LiveData<Event<String>>,
    taskId: String
) {
    val value = LiveDataTestUtil.getValue(liveData)
    assertThat(value.getContentIfNotHandled(), `is`(taskId))
}

fun assertSnackbarmessage(
    snackbarLiveData: LiveData<Event<Int>>,
    messageId: Int
) {
    val value = LiveDataTestUtil.getValue(snackbarLiveData)
    assertThat(value.getContentIfNotHandled(), `is`(messageId))
}