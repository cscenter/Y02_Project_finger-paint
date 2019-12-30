package ru.cscenter.fingerpaint.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object MyVibrator {
    const val LENGTH_SHORT = 1L
    const val LENGTH_LONG = 600L

    fun vibrate(context: Context, duration: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(duration)
        }
    }
}