package com.example.chessclock

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

        configButton.setOnClickListener { makeText(this, "Ok", Toast.LENGTH_SHORT).show() }
    }
}