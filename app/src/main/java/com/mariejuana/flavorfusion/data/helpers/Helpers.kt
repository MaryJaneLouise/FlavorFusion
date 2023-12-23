package com.mariejuana.flavorfusion.data.helpers

import android.os.CountDownTimer
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.util.Locale

class Helpers {
    // Converts the timestamp into a day (Sunday, Monday, etc.)
    fun getDay(timeStamp: Long): String{
        return SimpleDateFormat("EEEE", Locale.ENGLISH).format(timeStamp * 1000)
    }

    // Converts the timestamp into a time (11:59 PM etc.)
    fun getTime(timeStamp: Long): String{
        return SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(timeStamp * 1000)
    }

    // Loads the Lottie thing
    // Just for fun hehe
    fun loadSplashScreen() {
        // binding.animationView.isVisible = true
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Nothing to do.. literally
            }

            override fun onFinish() {
                // binding.animationView.isVisible = false
            }
        }.start()
    }
}