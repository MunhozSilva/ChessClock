package com.example.chessclock

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.chessclock.databinding.ActivityMainBinding
import com.example.chessclock.service.LowerClockService
import com.example.chessclock.service.UpperClockService
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var upperTimerStarted = false
    private var lowerTimerStarted = false
    private lateinit var upperClockServiceIntent: Intent
    private lateinit var lowerClockServiceIntent: Intent
    private var upperClockTime = 35.0
    private var lowerClockTime = 35.0
    private var handlerAnimation = Handler()
    private var upperClockStatusAnimation = false
    private var lowerClockStatusAnimation = false
    private var timeConfigurationOptions = arrayOf("1 min", "1 min +1s", "3 min", "3 min +2s", "10 min", "10 min +10s")
    private var gameModeSelector = "blitzGameMode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.upperClockButton.setOnClickListener { startLowerStopUpper() }
        binding.lowerClockButton.setOnClickListener { startUpperStopLower() }
        binding.resetButton.setOnClickListener { resetDialog() }
        binding.configurationButton.setOnClickListener { configurationDialog() }

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
        binding.lowerClockButton.isEnabled = false
        binding.lowerClockButton.visibility = View.INVISIBLE
        binding.lowerClockImageAnimationOne.visibility = View.INVISIBLE
        binding.lowerClockImageAnimationTwo.visibility = View.INVISIBLE
        lowerClockStatusAnimation = false
        handlerAnimation.removeCallbacks(runnable)
    }

    private fun startUpperClock() {
        upperClockServiceIntent.putExtra(UpperClockService.UPPER_TIME_EXTRA, upperClockTime)
        startService(upperClockServiceIntent)
        upperTimerStarted = true
        binding.upperClockButton.isEnabled = true
        binding.upperClockButton.visibility = View.VISIBLE
    }

    private val updateUpperClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (upperClockTime != 0.0) {
                if(upperClockTime < 31.0) {
                    upperClockStatusAnimation = true
                    binding.upperClockImageAnimationOne.visibility = View.VISIBLE
                    binding.upperClockImageAnimationTwo.visibility = View.VISIBLE
                    startPulse()
                }
                upperClockTime = intent.getDoubleExtra(UpperClockService.UPPER_TIME_EXTRA, 35.0)
                binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
            } else {
                //upperTimerStarted = false // future improvement: create a function to deal when time hits 0
                endGameState()
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
        binding.upperClockButton.isEnabled = false
        binding.upperClockButton.visibility = View.INVISIBLE
        binding.upperClockImageAnimationOne.visibility = View.INVISIBLE
        binding.upperClockImageAnimationTwo.visibility = View.INVISIBLE
        upperClockStatusAnimation = false
        handlerAnimation.removeCallbacks(runnable)
    }

    private fun startLowerClock() {
        lowerClockServiceIntent.putExtra(LowerClockService.LOWER_TIME_EXTRA, lowerClockTime)
        startService(lowerClockServiceIntent)
        lowerTimerStarted = true
        binding.lowerClockButton.isEnabled = true
        binding.lowerClockButton.visibility = View.VISIBLE
    }

    private val updateLowerClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(lowerClockTime != 0.0) {
                if(lowerClockTime < 31.0) {
                    lowerClockStatusAnimation = true
                    binding.lowerClockImageAnimationOne.visibility = View.VISIBLE
                    binding.lowerClockImageAnimationTwo.visibility = View.VISIBLE
                    startPulse()
                }
                lowerClockTime = intent.getDoubleExtra(LowerClockService.LOWER_TIME_EXTRA, 35.0)
                binding.lowerClockText.text = getTimeStringFromDouble(lowerClockTime)
            } else {
                //lowerTimerStarted = false // future improvement: create a function to deal when time hits 0
                endGameState()
            }
        }
    }

    // RESET CLOCKS FUNCTION
    private fun resetClocks() {
        endGameState()
        gameMode()
        binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
        binding.lowerClockText.text = getTimeStringFromDouble(lowerClockTime)
        binding.upperClockButton.visibility = View.VISIBLE
        binding.lowerClockButton.visibility = View.VISIBLE
        binding.upperClockButton.isEnabled = true
        binding.lowerClockButton.isEnabled = true
    }

    // END GAME STATE
    private fun endGameState() {
        pauseUpperClock()
        pauseLowerClock()
    }

    // GAME MODE FUNCTIONS
    private fun gameMode() {
        if (gameModeSelector == "bulletGameMode") {
            upperClockTime = 60.0
            lowerClockTime = 60.0
        }
        if (gameModeSelector == "bulletWithIncrementGameMode") {
            upperClockTime = 60.0
            lowerClockTime = 60.0
            //increment()
        }
        if (gameModeSelector == "blitzGameMode") {
            upperClockTime = 180.0
            lowerClockTime = 180.0
        }
        if (gameModeSelector == "blitzWithIncrementGameMode") {
            upperClockTime = 180.0
            lowerClockTime = 180.0
            //increment()
        }
        if (gameModeSelector == "rapidGameMode") {
            upperClockTime = 600.0
            lowerClockTime = 600.0
        }
        if (gameModeSelector == "rapidWithIncrementGameMode") {
            upperClockTime = 600.0
            lowerClockTime = 600.0
            //increment()
        }
    }

    // RESET DIALOG
    private fun resetDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(R.string.reset_dialog_message)
            setPositiveButton(android.R.string.ok) { _, _ -> resetClocks() }
            setNegativeButton(android.R.string.cancel) { _, _ -> /* Dismiss dialog */ }
            setCancelable(true)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // CONFIGURATION DIALOG
    private fun configurationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Time")
            setSingleChoiceItems(timeConfigurationOptions, -1){ dialog, which ->
                when(which) {
                    0 -> gameModeSelector = "bulletGameMode"
                    1 -> gameModeSelector = "bulletWithIncrementGameMode"
                    2 -> gameModeSelector = "blitzGameMode"
                    3 -> gameModeSelector = "blitzWithIncrementGameMode"
                    4 -> gameModeSelector = "rapidGameMode"
                    5 -> gameModeSelector = "rapidWithIncrementGameMode"
                }
                resetClocks()
                dialog.dismiss()
            }
            setNegativeButton(android.R.string.cancel) { _, _ -> /* Dismiss dialog */ }
            setCancelable(true)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // LOW TIME WARNING ANIMATION
    private fun startPulse() {
            runnable.run()
    }

    private var runnable = object : Runnable {
        override fun run() {

            if (upperClockStatusAnimation) {
                binding.upperClockImageAnimationOne.animate().scaleX(70f).scaleY(70f).alpha(0f)
                    .setDuration(900)
                    .withEndAction {
                        binding.upperClockImageAnimationOne.scaleX = 1f
                        binding.upperClockImageAnimationOne.scaleY = 1f
                        binding.upperClockImageAnimationOne.alpha = 1f
                    }

                binding.upperClockImageAnimationTwo.animate().scaleX(70f).scaleY(70f).alpha(0f)
                    .setDuration(700)
                    .withEndAction {
                        binding.upperClockImageAnimationTwo.scaleX = 1f
                        binding.upperClockImageAnimationTwo.scaleY = 1f
                        binding.upperClockImageAnimationTwo.alpha = 1f
                    }

                handlerAnimation.postDelayed(this, 1000)
            }

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