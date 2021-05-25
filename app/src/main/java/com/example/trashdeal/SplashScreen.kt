package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent) //start new activity here
            finish()
        }, 1500) //2000 is time delayed in millseconds
    }

}