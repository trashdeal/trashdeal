package com.example.trashdeal


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        if(auth.currentUser != null) {
            val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
            doc.get().addOnSuccessListener {
                if(it.exists()){
                    setContentView(R.layout.activity_main)
                    val useBin = findViewById<Button>(R.id.buttonbin)
                    val points = findViewById<TextView>(R.id.points)
                    val pointsBtn = findViewById<Button>(R.id.buttonscore)
                    val binNearMeBtn = findViewById<Button>(R.id.buttonbinsnear)
                    doc.get().addOnSuccessListener { points.text = it.data?.get("Points").toString() }
                    val user = it.data
                    useBin.setOnClickListener{
                        if(!user?.get("DefaultBin")?.equals("")!!){
                            startActivity(Intent(applicationContext, ConnectBin::class.java).apply {
                                putExtra("userBin", user?.get("DefaultBin").toString())
                            })
                        }else{
                            startActivity(Intent(applicationContext, BinsNearMe::class.java))
                        }
                    }
                    pointsBtn.setOnClickListener{
                        startActivity(Intent(applicationContext, UserTransactions::class.java))
                    }
                    binNearMeBtn.setOnClickListener{
                        startActivity(Intent(applicationContext, BinsNearMe::class.java))
                    }
                }else {
                    startActivity(Intent(applicationContext, RegisterUser::class.java))
                    finish()
                }
            }
        }else{
            startActivity(Intent(this, GettingStarted::class.java))
            finish()
        }
    }
}