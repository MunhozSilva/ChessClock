package com.example.chessclock.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

class LowerClockService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val time = intent.getDoubleExtra(LOWER_TIME_EXTRA, 180.0)
        timer.scheduleAtFixedRate(TimeTask(time), 0, 100)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask() {
        override fun run() {
            val intent = Intent(LOWER_TIMER_UPDATED)
                time -= 0.1
            intent.putExtra(LOWER_TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    companion object {
        const val LOWER_TIMER_UPDATED = "lowerTimerUpdated"
        const val LOWER_TIME_EXTRA = "lowerTimerExtra"
    }
}