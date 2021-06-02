package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*

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
                var userBin = intent.getStringExtra("userBin").toString()
                var binType = intent.getStringExtra("binType").toString()
                var waste_type = intent.getStringExtra("wasteType").toString()
                var oldWeight = intent.getStringExtra("oldWeight").toString().toDouble()
                val ref = FirebaseDatabase.getInstance().getReference(userBin).child(binType)
                var newWeight: Double
                var userWeight: Double
                val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
                ref.child("Weight").get().addOnSuccessListener {
                    Log.i("TAG", "Got new value ${it.value}")
                    newWeight = it.value.toString().toDouble()
                    userWeight = newWeight - oldWeight
                    var pointsEarned = (userWeight*10).toInt()
                    Log.i("TAG", "Date ${Calendar.getInstance().time}")
                    if(pointsEarned != 0) {
                        doc.get().addOnSuccessListener {
                            var userPoints = pointsEarned + it.data?.get("Points").toString().toInt()
                            doc.set(hashMapOf("Points" to userPoints), SetOptions.merge())
                            var calendar = Calendar.getInstance()
                            var simpleDateFormat = SimpleDateFormat("LLL dd,yyyy")
                            var dateTime = simpleDateFormat.format(calendar.time).toString()
                            var userTransaction = hashMapOf(
                                "Date" to dateTime,
                                "PointsEarned" to pointsEarned,
                                "WasteType" to waste_type,
                                "Bin" to userBin,
                                "WasteWeight" to userWeight
                            )
                            doc.collection("transactions").add(userTransaction)
                        }
                    }
                    ref.child("Status").setValue("free")
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener{
                    Log.e("TAG", "Error getting data", it)
                }
            }, time)
        }
    }
}