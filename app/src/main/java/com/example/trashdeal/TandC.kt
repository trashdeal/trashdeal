package com.example.trashdeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TandC : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tand_c)
        val acceptbtn = findViewById<Button>(R.id.accept)
        val declinebtn = findViewById<Button>(R.id.decline)
        acceptbtn.setOnClickListener {
            val intent = Intent(this, MobnoRegister::class.java)
            startActivity(intent);
        }
        declinebtn.setOnClickListener {
            val intent = Intent(this, GettingStarted::class.java)
            startActivity(intent);
        }
    }
}