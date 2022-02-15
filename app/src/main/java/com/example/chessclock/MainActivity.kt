package com.example.chessclock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toast.makeText
import com.example.chessclock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val configButton = findViewById<ImageButton>(R.id.configurationButton)
        val toastMessage: String = resources.getString(R.string.toast_text)

        binding.configurationButton.setOnClickListener {
            makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }
    }
}