package com.text.stopwatch.services

import android.os.Binder

class TimerBinder(private var service: TimerService) : Binder() {
    fun getService(): TimerService = service
}