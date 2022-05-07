package com.text.stopwatch.home

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.text.stopwatch.R
import com.text.stopwatch.databinding.ActivityHomeBinding
import com.text.stopwatch.services.TimerBinder
import com.text.stopwatch.services.TimerService
import com.text.stopwatch.util.CommonFun

class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(HomeVM::class.java) }
    private val binding: ActivityHomeBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_home) }

    private val timerIntent by lazy { Intent(this, TimerService::class.java) }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            val timerBinder = iBinder as TimerBinder
            val service = timerBinder.getService()
            viewModel.izStarted = true
            viewModel.izPaused = service.isPaused
            service.timeMillis.observe(this@HomeActivity) {
                viewModel.time = CommonFun.millisToTime(it)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            viewModel.izStarted = false
            viewModel.izPaused = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_first)
        binding.viewModel = viewModel

        viewModel.starService.observe(this) {
            //val data = it.getContentIfNotHandledOrReturnNull()
            bindService(timerIntent, serviceConnection, Context.BIND_ABOVE_CLIENT)
            restartTimerService(this)
        }
        viewModel.stopService.observe(this) {
            unbindService(serviceConnection)
            stopTimerService(this)
        }
        viewModel.pauseTimer.observe(this) {
            val switchIntent = Intent(this, TimerService::class.java)
            switchIntent.action = "pause"
            ContextCompat.startForegroundService(this, switchIntent)
        }
        viewModel.resumeTimer.observe(this) {
            val switchIntent = Intent(this, TimerService::class.java)
            switchIntent.action = "resume"
            ContextCompat.startForegroundService(this, switchIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(timerIntent, serviceConnection, Context.BIND_ABOVE_CLIENT)
    }

    private fun startTimerService(context: Context) {
        val intent = Intent(context, TimerService::class.java)
        //intent.putExtra(IntentKeys.MOBILE_NUMBER, number)
        if (!isTimerServiceRunning(context)) ContextCompat.startForegroundService(context, intent)
        //ContextCompat.startForegroundService(context, intent)
    }

    private fun stopTimerService(context: Context) {
        val intent = Intent(context, TimerService::class.java)
        if (isTimerServiceRunning(context)) context.stopService(intent)
        //context.stopService(intent)
    }

    private fun restartTimerService(context: Context) {
        stopTimerService(context)
        startTimerService(context)
    }

    private fun isTimerServiceRunning(context: Context): Boolean {
        val serviceName = "TimerService"

        val activityManager = context.getSystemService(ActivityManager::class.java)
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className.contains(serviceName) && service.service.packageName == context.packageName) return true
        }

        return false
    }

}