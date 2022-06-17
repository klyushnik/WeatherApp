package com.example.weatherapp

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ReleaseTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if(priority == Log.ERROR || priority == Log.WARN) {
            //sending reports to crashlytics
            val crashlytics = Firebase.crashlytics
            crashlytics.log(t?.message!!)
            crashlytics.recordException(t)
        }
    }

    override fun log(priority: Int, t: Throwable?) {
        super.log(priority, t)
    }
}