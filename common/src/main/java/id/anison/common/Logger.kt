package id.anison.common

import android.util.Log

interface Logger {
    fun i(tag: String, message: String)

    fun d(tag: String, message: String)

    fun e(tag: String, message: String)
}

class AndroidLogcatLogger : Logger {
    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
}