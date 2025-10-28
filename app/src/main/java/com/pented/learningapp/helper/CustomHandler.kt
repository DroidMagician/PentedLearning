package com.pented.learningapp.helper

import android.os.Handler
import android.os.Message
import java.util.*

class CustomHandler: Handler() {

    var s = Stack<Message>()
    var is_paused = false

    @Synchronized
    fun pause() {
        is_paused = true
    }

    @Synchronized
    fun resume() {
        is_paused = false
        while (!s.empty()) {
            sendMessageAtFrontOfQueue(s.pop())
        }
    }

    override fun dispatchMessage(msg: Message) {
        if (is_paused) {
            s.push(Message.obtain(msg))
            return
        } else {
            super.dispatchMessage(msg)
        }
    }
}