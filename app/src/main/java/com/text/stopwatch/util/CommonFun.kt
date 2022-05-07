package com.text.stopwatch.util

import java.util.concurrent.TimeUnit

object CommonFun {

    fun millisToTime(millis: Long): String {
        val hour = TimeUnit.MILLISECONDS.toHours(millis)
        val min = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hour)
        val sec = TimeUnit.MILLISECONDS.toSeconds(millis) - (TimeUnit.HOURS.toSeconds(hour) + TimeUnit.MINUTES.toSeconds(min))

        return if (hour < 100) String.format("%02d:%02d:%02d", hour, min, sec)
        else String.format("%d:%02d:%02d", hour, min, sec)
    }

}