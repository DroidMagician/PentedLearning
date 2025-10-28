package com.pented.learningapp.helper

import androidx.lifecycle.Observer

/*
 * Wrapper Observer class for our Event class.
 **/
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}