package com.charlezz.pickle.util

import timber.log.Timber

class CrashReportingTree : Timber.Tree(){
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // TODO: 2020/12/31 Records logs in db
    }
}