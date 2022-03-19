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
    private lateinit var upperClockServiceIntent: Intent
    private lateinit var lowerClockServiceIntent: Intent
    private var upperClockTime = 180.0
    private var lowerClockTime = 180.0
    private var handlerAnimation = Handler()
    private var upperClockStatusAnimation = false
    private var lowerClockStatusAnimation = false
    private var timeConfigurationOptions = arrayOf("1 min", "1 min +1s", "3 min", "3 min +2s", "5 min", "5 min +5s", "10 min", "30 min")
    private var gameModeSelector = "blitzThreeMinGameMode"
    private var incrementValue = 0.0
    private var endgameFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Buttons
        binding.upperClockButton.setOnClickListener { startLowerPauseUpper() }
        binding.lowerClockButton.setOnClickListener { startUpperPauseLower() }
        binding.resetButton.setOnClickListener { resetDialog() }
        binding.configurationButton.setOnClickListener { configurationDialog() }


        // Services intent
        upperClockServiceIntent = Intent(applicationContext, UpperClockService::class.java)
        registerReceiver(updateUpperClockTime, IntentFilter(UpperClockService.UPPER_TIMER_UPDATED))

        lowerClockServiceIntent = Intent(applicationContext, LowerClockService::class.java)
        registerReceiver(updateLowerClockTime, IntentFilter(LowerClockService.LOWER_TIMER_UPDATED))
    }

    // UPPER CLOCK FUNCTIONS
    private fun startUpperPauseLower() {

        // First pause the running clock to make sure the increment is added if needed
        pauseLowerClock()
        startUpperClock()
    }

    private fun pauseLowerClock() {

        // Prevent increment when game is over
        if (!endgameFlag) {
            incrementLowerClock()
        }

        // Core commands to pause the clock
        stopService(lowerClockServiceIntent)
        binding.lowerClockButton.isEnabled = false
        binding.lowerClockButton.visibility = View.INVISIBLE

        // Pause animation
        binding.lowerClockImageAnimationOne.visibility = View.INVISIBLE
        binding.lowerClockImageAnimationTwo.visibility = View.INVISIBLE
        lowerClockStatusAnimation = false
        handlerAnimation.removeCallbacks(runnable)
    }

    private fun startUpperClock() {
        upperClockServiceIntent.putExtra(UpperClockService.UPPER_TIME_EXTRA, upperClockTime)
        startService(upperClockServiceIntent)
        binding.upperClockButton.isEnabled = true
        binding.upperClockButton.visibility = View.VISIBLE
    }

    private val updateUpperClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            // Check if game is not over
            if (upperClockTime != 0.0) {

                // Animation effect when time is low
                if(upperClockTime < 46.0) {
                    upperClockStatusAnimation = true
                    binding.upperClockImageAnimationOne.visibility = View.VISIBLE
                    binding.upperClockImageAnimationTwo.visibility = View.VISIBLE
                    startPulse()
                }

                // Update clock time
                upperClockTime = intent.getDoubleExtra(UpperClockService.UPPER_TIME_EXTRA, 180.0)
                binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
            } else {
                endGameState()
            }
        }
    }

    // LOWER CLOCK FUNCTIONS
    private fun startLowerPauseUpper() {

        // First pause the running clock to make sure the increment is added if needed
        pauseUpperClock()
        startLowerClock()
    }

    private fun pauseUpperClock() {

        // Prevent increment when game is over
        if (!endgameFlag) {
            incrementUpperClock()
        }

        // Core commands to pause the clock
        stopService(upperClockServiceIntent)
        binding.upperClockButton.isEnabled = false
        binding.upperClockButton.visibility = View.INVISIBLE

        // Pause animation
        binding.upperClockImageAnimationOne.visibility = View.INVISIBLE
        binding.upperClockImageAnimationTwo.visibility = View.INVISIBLE
        upperClockStatusAnimation = false
        handlerAnimation.removeCallbacks(runnable)
    }

    private fun startLowerClock() {
        lowerClockServiceIntent.putExtra(LowerClockService.LOWER_TIME_EXTRA, lowerClockTime)
        startService(lowerClockServiceIntent)
        binding.lowerClockButton.isEnabled = true
        binding.lowerClockButton.visibility = View.VISIBLE
    }

    private val updateLowerClockTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            // Check if game is not over
            if(lowerClockTime != 0.0) {

                // Animation effect when time is low
                if(lowerClockTime < 46.0) {
                    lowerClockStatusAnimation = true
                    binding.lowerClockImageAnimationOne.visibility = View.VISIBLE
                    binding.lowerClockImageAnimationTwo.visibility = View.VISIBLE
                    startPulse()
                }

                // Update clock time
                lowerClockTime = intent.getDoubleExtra(LowerClockService.LOWER_TIME_EXTRA, 180.0)
                binding.lowerClockText.text = getTimeStringFromDouble(lowerClockTime)
            } else {
                endGameState()
            }
        }
    }

    // RESET CLOCKS FUNCTION
    private fun resetClocks() {
        endGameState()
        gameMode()
        endgameFlag = false
        binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
        binding.lowerClockText.text = getTimeStringFromDouble(lowerClockTime)
        binding.upperClockButton.visibility = View.VISIBLE
        binding.lowerClockButton.visibility = View.VISIBLE
        binding.upperClockButton.isEnabled = true
        binding.lowerClockButton.isEnabled = true
    }

    // END GAME STATE
    private fun endGameState() {
        endgameFlag = true
        pauseUpperClock()
        pauseLowerClock()
    }

    // GAME MODE FUNCTIONS
    private fun gameMode() {
        if (gameModeSelector == "bulletGameMode") {
            upperClockTime = 60.0
            lowerClockTime = 60.0
            incrementValue = 0.0
        }
        if (gameModeSelector == "bulletWithIncrementGameMode") {
            upperClockTime = 60.0
            lowerClockTime = 60.0
            incrementValue = 1.0
        }
        if (gameModeSelector == "blitzThreeMinGameMode") {
            upperClockTime = 180.0
            lowerClockTime = 180.0
            incrementValue = 0.0
        }
        if (gameModeSelector == "blitzThreeMinWithIncrementGameMode") {
            upperClockTime = 180.0
            lowerClockTime = 180.0
            incrementValue = 2.0
        }
        if (gameModeSelector == "blitzFiveMinGameMode") {
            upperClockTime = 300.0
            lowerClockTime = 300.0
            incrementValue = 0.0
        }
        if (gameModeSelector == "blitzFiveMinWithIncrementGameMode") {
            upperClockTime = 300.0
            lowerClockTime = 300.0
            incrementValue = 5.0
        }
        if (gameModeSelector == "rapidTenMinGameMode") {
            upperClockTime = 600.0
            lowerClockTime = 600.0
            incrementValue = 0.0
        }
        if (gameModeSelector == "rapidThirtyMinGameMode") {
            upperClockTime = 1800.0
            lowerClockTime = 1800.0
            incrementValue = 0.0
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
            setTitle(R.string.configuration_dialog_title)
            setSingleChoiceItems(timeConfigurationOptions, -1){ dialog, which ->
                when(which) {
                    0 -> gameModeSelector = "bulletGameMode"
                    1 -> gameModeSelector = "bulletWithIncrementGameMode"
                    2 -> gameModeSelector = "blitzThreeMinGameMode"
                    3 -> gameModeSelector = "blitzThreeMinWithIncrementGameMode"
                    4 -> gameModeSelector = "blitzFiveMinGameMode"
                    5 -> gameModeSelector = "blitzFiveMinWithIncrementGameMode"
                    6 -> gameModeSelector = "rapidTenMinGameMode"
                    7 -> gameModeSelector = "rapidThirtyMinGameMode"
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

    // LOW TIME WARNING ANIMATION FUNCTIONS
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

    // INCREMENT FUNCTIONS
    private fun incrementUpperClock() {

        // Check if there is an increment to be done and if it's not the first movement of the game
        if (incrementValue != 0.0 && !binding.lowerClockButton.isEnabled) {
            when(incrementValue) {
                1.0 -> upperClockTime += 1.0
                2.0 -> upperClockTime += 2.0
                5.0 -> upperClockTime += 5.0
            }

            // Update the UI
            binding.upperClockText.text = getTimeStringFromDouble(upperClockTime)
        }
    }

    private fun incrementLowerClock() {

        // Check if there is an increment to be done and if it's not the first movement of the game
        if (incrementValue != 0.0 && !binding.upperClockButton.isEnabled) {
            when(incrementValue) {
                1.0 -> lowerClockTime += 1.0
                2.0 -> lowerClockTime += 2.0
                5.0 -> lowerClockTime += 5.0
            }

            // Update the UI
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