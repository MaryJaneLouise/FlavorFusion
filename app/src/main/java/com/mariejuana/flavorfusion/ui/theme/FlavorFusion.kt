package com.mariejuana.flavorfusion.ui.theme

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors

class FlavorFusion : Application() {
    // Sets the color of the app based on the current color palette of the phone
    // Works on Android 12+
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
            private set

    }
}