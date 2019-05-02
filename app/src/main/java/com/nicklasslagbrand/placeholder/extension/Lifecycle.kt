package com.nicklasslagbrand.placeholder.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, NonNullObserver(body))

private class NonNullObserver<T : Any>(val body: (T) -> Unit) : Observer<T> {
    override fun onChanged(data: T?) {
        requireNotNull(data)

        body(data)
    }
}
