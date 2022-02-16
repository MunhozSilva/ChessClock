package com.example.chessclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chessclock.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var upperTimerStarted = false
    private var lowerTimerStarted = false
    private lateinit var upperClockServiceIntent: Intent
    private lateinit var lowerClockServiceIntent: Intent
    private var upperClockTime = 300.0
    private var lowerClockTime = 300.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upperClockButton.setOnClickListener { startLowerStopUpper() }
        binding.lowerClockButton.setOnClickListener { startUpperStopLower() }

        binding.configurationButton.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        upperClockServiceIntent = Intent(applicationContext, UpperClockService::class.java)
        registerReceiver(updateUpperClockTime, IntentFilter(UpperClockService.TIMER_UPDATED))

        lowerClockServiceIntent = Intent(applicationContext, LowerClockService::class.java)
        registerReceiver(updateLowerClockTime, IntentFilter(LowerClockService.LOWER_TIMER_UPDATED))
    }

    // UPPER CLOCK FUNCTIONS
    private fun startUpperStopLower() {
        startUpperClock()
        pauseLowerClock()
    }

    private fun pauseLowerClock() {
        stopService(lowerClockServiceIntent)
        lowerTimerStarted = false
    }

    private fun startUpperClock() {
        upperClockServiceIntent.putExtra(UpperClockService.TIME_EXTRA, upperClockTime)
        startService(upperClockServiceIntent)
        upperTimerStarted = true
    }

    private val updateUpperClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            upperClockTime = intent.getDoubleExtra(UpperClockService.TIME_EXTRA, 300.0)
            binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
        }
    }

    // LOWER CLOCK FUNCTIONS
    private fun startLowerStopUpper() {
        startLowerClock()
        pauseUpperClock()
    }

    private fun pauseUpperClock() {
        stopService(upperClockServiceIntent)
        upperTimerStarted = false
    }

    private fun startLowerClock() {
        lowerClockServiceIntent.putExtra(LowerClockService.LOWER_TIME_EXTRA, lowerClockTime)
        startService(lowerClockServiceIntent)
        lowerTimerStarted = true
    }

    private val updateLowerClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            lowerClockTime = intent.getDoubleExtra(LowerClockService.LOWER_TIME_EXTRA, 300.0)
            binding.lowerClockText.text = getTimeStringFromDouble(lowerClockTime)
        }
    }

    // TIME FORMAT MANIPULATION
    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(minutes, seconds)
    }

    private fun makeTimeString(min: Int, sec: Int): String = String.format("%02d:%02d", min, sec)
}