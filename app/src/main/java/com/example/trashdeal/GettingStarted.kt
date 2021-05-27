package com.example.trashdeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GettingStarted : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getting_started)
        val getStartedbtn = findViewById<Button>(R.id.getStarted)
        getStartedbtn.setOnClickListener {
            startActivity(Intent(this, IntroVideo::class.java));
        }
    }
}