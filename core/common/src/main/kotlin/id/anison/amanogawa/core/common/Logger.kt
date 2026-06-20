package id.anison.amanogawa.core.common

import co.touchlab.kermit.Logger as KermitLogger

object Log {
    fun d(tag: String, message: () -> String) {
        KermitLogger.d(tag = tag, message = message)
    }

    fun i(tag: String, message: () -> String) {
        KermitLogger.i(tag = tag, message = message)
    }

    fun e(tag: String, message: () -> String) {
        KermitLogger.e(tag = tag, message = message)
    }
}
