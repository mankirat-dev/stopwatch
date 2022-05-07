package com.text.stopwatch.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.text.stopwatch.util.CommonFun
import com.text.stopwatch.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerService : Service() {

    private fun log(msg: String, e: Throwable? = null) {
        Log.e("TimerService", msg, e)
    }

    companion object {
        const val NOTIFICATION_ID = 9
        const val CHANNEL_ID = "my_channel_98547"
    }

    private val timerBinder by lazy { TimerBinder(this) }
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val timeMillisTemp = MutableLiveData<Long>(0)
    val timeMillis: LiveData<Long> = timeMillisTemp
    private val timerJob by lazy {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (!isPaused) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        timeMillisTemp.value = timeMillisTemp.value?.plus(1000)
                        startForeground(NOTIFICATION_ID, createNotification())
                    }
                }
            }
        }
    }
    var isPaused = false

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onDestroy() {
        log("onDestroy")
        timeMillisTemp.value = 0
        isPaused = false
        timerJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return timerBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            "pause" -> {
                isPaused = true
            }
            "resume" -> {
                isPaused = false
            }
            else -> {
                timeMillisTemp.value = 0
                isPaused = false
                startForeground(NOTIFICATION_ID, createNotification())
                timerJob.start()
            }
        }

        return START_NOT_STICKY//super.onStartCommand(intent, flags, startId)
    }


    private fun createNotification(): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.layout_notification).apply {
            setTextViewText(R.id.tv_time, CommonFun.millisToTime(timeMillisTemp.value ?: 0))
        }

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, createNotificationChannel())
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(this)
        }

        return notificationBuilder
            .setContentTitle("content_title")
            .setContentText("content_text")
            .setSmallIcon(R.drawable.ic_watch)
            .setOnlyAlertOnce(true)
            .setContent(remoteViews)
            .setAutoCancel(false)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val notificationChannel = NotificationChannel(CHANNEL_ID, "channel_name", NotificationManager.IMPORTANCE_LOW)

        notificationChannel.apply {
            setShowBadge(false)
            description = "channel_description"
            enableLights(true)
            lightColor = Color.RED
        }

        notificationManager.createNotificationChannel(notificationChannel)

        return CHANNEL_ID
    }

}