package com.nicklasslagbrand.placeholder.data.viewmodel

import androidx.lifecycle.LiveData

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * This should be used for listening single events in [LiveData].
 */
data class ConsumableEvent<out T>(private val content: T) {
    private var hasBeenConsumed = false

    /**
     * Returns the content and prevents its use again.
     */
    fun handleIfNotConsumed(block: (T) -> Boolean) {
        if (!hasBeenConsumed) {
            hasBeenConsumed = block(content)
        }
    }
}
