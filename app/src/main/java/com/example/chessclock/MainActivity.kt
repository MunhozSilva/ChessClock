package com.example.chessclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.chessclock.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var upperTimerStarted = false
    private var lowerTimerStarted = false
    private lateinit var upperClockServiceIntent: Intent
    private lateinit var lowerClockServiceIntent: Intent
    private var upperClockTime = 300.0
    private var lowerClockTime = 35.0
    private var handlerAnimation = Handler()
    private var lowerClockStatusAnimation = true

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
        registerReceiver(updateUpperClockTime, IntentFilter(UpperClockService.UPPER_TIMER_UPDATED))

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
        binding.lowerClockButton.setEnabled(false)
        binding.lowerClockButton.visibility = View.INVISIBLE
        binding.lowerClockImageAnimationOne.visibility = View.INVISIBLE
        binding.lowerClockImageAnimationTwo.visibility = View.INVISIBLE
        handlerAnimation.removeCallbacks(runnable)
    }

    private fun startUpperClock() {
        upperClockServiceIntent.putExtra(UpperClockService.UPPER_TIME_EXTRA, upperClockTime)
        startService(upperClockServiceIntent)
        upperTimerStarted = true
        binding.upperClockButton.setEnabled(true)
        binding.upperClockButton.visibility = View.VISIBLE
    }

    private val updateUpperClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (upperClockTime != 0.0) {
                upperClockTime = intent.getDoubleExtra(UpperClockService.UPPER_TIME_EXTRA, 300.0)
                binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
            } else {
                upperTimerStarted = false // future improvement: create a function to deal when time hits 0
            }
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
        binding.upperClockButton.setEnabled(false)
        binding.upperClockButton.visibility = View.INVISIBLE
    }

    private fun startLowerClock() {
        lowerClockServiceIntent.putExtra(LowerClockService.LOWER_TIME_EXTRA, lowerClockTime)
        startService(lowerClockServiceIntent)
        lowerTimerStarted = true
        binding.lowerClockButton.setEnabled(true)
        binding.lowerClockButton.visibility = View.VISIBLE
    }

    private val updateLowerClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(lowerClockTime != 0.0) {
                if(lowerClockTime < 31.0) {
                    binding.lowerClockImageAnimationOne.visibility = View.VISIBLE
                    binding.lowerClockImageAnimationTwo.visibility = View.VISIBLE
                    startPulse()
                }
                lowerClockTime = intent.getDoubleExtra(LowerClockService.LOWER_TIME_EXTRA, 35.0)
                binding.lowerClockText.text = getTimeStringFromDouble(lowerClockTime)
            } else {
                lowerTimerStarted = false // future improvement: create a function to deal when time hits 0
            }
        }
    }

    // LOW TIME WARNING ANIMATION
    private fun startPulse() {
            runnable.run()
    }

    private var runnable = object : Runnable {
        override fun run() {

            if (lowerClockStatusAnimation) {
                binding.lowerClockImageAnimationOne.animate().scaleX(70f).scaleY(70f).alpha(0f)
                    .setDuration(900)
                    .withEndAction {
                        binding.lowerClockImageAnimationOne.scaleX = 1f
                        binding.lowerClockImageAnimationOne.scaleY = 1f
                        binding.lowerClockImageAnimationOne.alpha = 1f
                    }

                binding.lowerClockImageAnimationTwo.animate().scaleX(70f).scaleY(70f).alpha(0f)
                    .setDuration(700)
                    .withEndAction {
                        binding.lowerClockImageAnimationTwo.scaleX = 1f
                        binding.lowerClockImageAnimationTwo.scaleY = 1f
                        binding.lowerClockImageAnimationTwo.alpha = 1f
                    }

                handlerAnimation.postDelayed(this, 1000)

            }
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