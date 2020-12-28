package com.charlezz.pickle.util

import android.util.Log
import kotlin.reflect.KClass

@Deprecated("use Timber instead")
class Logger constructor(val tag: String? = "Pickle") {

    companion object{
        @JvmStatic
        fun getLogger(tag:String): Logger {
            return Logger(tag)
        }

        fun getLogger(tag: KClass<*>): Logger {
            return Logger(tag.simpleName)
        }
    }

    fun v(message: String) {
        Log.v(tag, message)
    }

    fun i(message: String) {
        Log.i(tag, message)
    }

    fun d(message: String) {
        Log.d(tag, message)
    }

    fun w(message: String) {
        Log.w(tag, message)
    }

    fun w(message: String?, tr: Throwable?) {
        Log.w(tag, message, tr)
    }

    fun e(message: String) {
        Log.e(tag, message)
    }

    fun e(message: String?, tr: Throwable?) {
        Log.e(tag, message, tr)
    }
}