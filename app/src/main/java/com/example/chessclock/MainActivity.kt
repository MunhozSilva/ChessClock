package com.example.chessclock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toast.makeText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val configButton = findViewById<ImageButton>(R.id.configurationButton)
        val toastMessage: String = resources.getString(R.string.toast_text)

        configButton.setOnClickListener {
            makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }
    }
}