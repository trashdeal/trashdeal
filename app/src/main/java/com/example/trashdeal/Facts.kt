package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Facts : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)
        val factView = findViewById<TextView>(R.id.factView)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        var time:Long = 1000
        val doc: DocumentReference = fStore.collection("facts").document("0EaVbIPo1AnbSaBp0Ypj")
        doc.get().addOnSuccessListener {
            val index = (Math.random() * (33 - 1 + 1) + 1).toInt().toString()
            val fact = it.data?.get(index).toString()
            factView.text = fact
            if(fact.length in 0..30){
                time = 4000
            }else if(fact.length in 31..90){
                time = 6000
            }else if(fact.length in 91..200){
                time = 7000
            }
            @Suppress("DEPRECATION")
            Handler().postDelayed({
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent) //start new activity here
                finish()
            }, time)
        }
    }
}