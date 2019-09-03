package com.ininmm.todoapp.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.ininmm.todoapp.Event
import com.ininmm.todoapp.EventObserver

fun <P, R> LiveData<P>.map(body: (P) -> R): LiveData<R> {
    return Transformations.map(this, body)
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}

inline fun <T> LiveData<T>.observeNotNull(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    this.observe(owner, Observer { it?.run(observer) })
}

inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline eventObserver: (T?) -> Unit
) {
    this.observe(owner, EventObserver { eventObserver(it) })
}

inline fun <T> LiveData<Event<T>>.observeEventNotNull(
    owner: LifecycleOwner,
    crossinline eventObserver: (T) -> Unit
) {
    this.observe(owner, EventObserver { it?.run(eventObserver) })
}