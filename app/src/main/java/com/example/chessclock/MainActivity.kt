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
    private var timerStarted = false
    private lateinit var upperClockServiceIntent: Intent
    //private lateinit var lowerClockServiceIntent: Intent
    private var upperClockTime = 300.0
    //private var lowerCLockTime = 300.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.upperClockButton.setOnClickListener { startLowerStopUpper() }
        binding.lowerClockButton.setOnClickListener { startUpperStopLower() }

        binding.configurationButton.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        upperClockServiceIntent = Intent(applicationContext, UpperClockService::class.java)
        registerReceiver(updateUpperClockTime, IntentFilter(UpperClockService.TIMER_UPDATED))

        //ADD LOWER CLOCK SERVICE INTENT AND REGISTER IT'S RECEIVER
    }

    private fun startUpperStopLower() {
        pauseLowerClock()
        startUpperClock()
    }

    private fun pauseLowerClock() {
        //stopService(upperClockServiceIntent)
        //timerStarted = false
    }

    private fun startUpperClock() {
        upperClockServiceIntent.putExtra(UpperClockService.TIME_EXTRA, upperClockTime)
        startService(upperClockServiceIntent)
        timerStarted = true
    }

    private val updateUpperClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            upperClockTime = intent.getDoubleExtra(UpperClockService.TIME_EXTRA, 300.0)
            binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(minutes, seconds)
    }

    private fun makeTimeString(min: Int, sec: Int): String = String.format("%02d:%02d", min, sec)
}