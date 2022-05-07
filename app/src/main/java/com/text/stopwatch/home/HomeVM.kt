package com.text.stopwatch.home

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.text.stopwatch.BR
import com.text.stopwatch.util.Event

class HomeVM : ViewModel(), Observable {

    private val propertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        propertyChangeRegistry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        propertyChangeRegistry.remove(callback)
    }

    var izPaused: Boolean = false
        @Bindable
        get
        set(value) {
            field = value
            propertyChangeRegistry.notifyCallbacks(this, BR.izPaused, null)
        }

    var izStarted: Boolean = false
        @Bindable
        get
        set(value) {
            field = value
            propertyChangeRegistry.notifyCallbacks(this, BR.izStarted, null)
        }

    var time: String = "00:00:00"
        @Bindable
        get
        set(value) {
            field = value
            propertyChangeRegistry.notifyCallbacks(this, BR.time, null)
        }

    val starService: MutableLiveData<Event<Any?>> = MutableLiveData()
    val stopService: MutableLiveData<Event<Any?>> = MutableLiveData()
    val pauseTimer: MutableLiveData<Event<Any?>> = MutableLiveData()
    val resumeTimer: MutableLiveData<Event<Any?>> = MutableLiveData()

    fun start() {
        izStarted = true
        izPaused = false
        time = "00:00:00"
        starService.value = Event(null)
    }

    fun stop() {
        izStarted = false
        izPaused = false
        time = "00:00:00"
        stopService.value = Event(null)
    }

    fun pauseResume() {
        izPaused = !izPaused
        if (izPaused) {
            pauseTimer.value = Event(null)
        } else {
            resumeTimer.value = Event(null)
        }
    }
}