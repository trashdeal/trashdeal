package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class Facts : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    var timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facts)
        val spinner = findViewById<ProgressBar>(R.id.progressBar1)
        spinner.setVisibility(View.VISIBLE);
        val factView = findViewById<TextView>(R.id.factView)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        var time:Long = 1000
        fun startTimer() {
            // TODO Auto-generated method stub
            super.onUserInteraction()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    timer.cancel()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish() //this will cancel the timer of the system
                }
            }, time)
        }
        val doc: DocumentReference = fStore.collection("facts").document("0EaVbIPo1AnbSaBp0Ypj")
        doc.get().addOnSuccessListener {
            val index = (Math.random() * (33 - 1 + 1) + 1).toInt().toString()
            val fact = it.data?.get(index).toString()
            factView.text = fact
            if(fact.length in 0..30){
                time = 3000
            }else if(fact.length in 31..90){
                time = 5000
            }else if(fact.length in 91..200){
                time = 6000
            }
            var flag = 1
            var userBin = intent.getStringExtra("userBin").toString()
            var binType = intent.getStringExtra("binType").toString()
            var wasteType = intent.getStringExtra("wasteType").toString()
            var oldWeight = intent.getStringExtra("oldWeight").toString().toDouble()
            val ref = FirebaseDatabase.getInstance().getReference(userBin).child(binType)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val status = dataSnapshot.child("Status").value
                    if (status == "done" && flag == 1) {
                        Log.d("TAGG", "Entered $status")
                        var newWeight: Double
                        var userWeight: Double
                        val doc: DocumentReference =
                            fStore.collection("user").document(auth.currentUser.uid)
                        ref.child("Weight").get().addOnSuccessListener {
                            Log.i("TAG", "Got new value ${it.value}")
                            newWeight = it.value.toString().toDouble()
                            userWeight = newWeight - oldWeight
                            var pointsEarned = (userWeight * 10).toInt() //need to change
                            Log.i("TAG", "Date ${Calendar.getInstance().time}")
                            if (pointsEarned != 0) {
                                doc.get().addOnSuccessListener {
                                    var userPoints =
                                        pointsEarned + it.data?.get("Points").toString().toInt()
                                    doc.set(hashMapOf("Points" to userPoints), SetOptions.merge())
                                    var calendar = Calendar.getInstance()
                                    var userTransaction = hashMapOf(
                                        "Date" to calendar.time,
                                        "PointsEarned" to pointsEarned,
                                        "WasteType" to wasteType,
                                        "Bin" to userBin,
                                        "WasteWeight" to String.format("%.2f",userWeight).toDouble()
                                    )
                                    doc.collection("transactions").add(userTransaction)
                                }
                            }
                            ref.child("Status").setValue("free")
                            ref.child("Language").setValue("none")
                        }.addOnFailureListener {
                            Log.e("TAG", "Error getting data", it)
                        }
                        flag = 0
                        startTimer()
                    }
                    Log.d("TAGG", "called $status")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            })
        }
    }
}