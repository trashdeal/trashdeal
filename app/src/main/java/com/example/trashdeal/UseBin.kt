package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*

class UseBin : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var oldWeight = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_bin)
        val openBtn = findViewById<Button>(R.id.openBtn)
        val closeBtn = findViewById<Button>(R.id.closeBtn)
        val disconnectBtn = findViewById<Button>(R.id.disconnectBtn)
        val binNameView = findViewById<TextView>(R.id.binName)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        var userBin = intent.getStringExtra("userBin").toString()
        var binType = intent.getStringExtra("binType").toString()
        val ref = FirebaseDatabase.getInstance().getReference(userBin).child(binType)
        binNameView.text = userBin
        ref.child("Weight").get().addOnSuccessListener {
            Log.i("TAG", "Got value ${it.value}")
            oldWeight = it.value.toString().toDouble()
        }.addOnFailureListener{
            Log.e("TAG", "Error getting data", it)
        }
        openBtn.setOnClickListener{
            Toast.makeText(applicationContext,"Opening Bin Lid..", Toast.LENGTH_SHORT).show()
            ref.child("BinLid").setValue("open")
        }
        closeBtn.setOnClickListener{
            Toast.makeText(applicationContext,"Closing Bin Lid..", Toast.LENGTH_SHORT).show()
            ref.child("BinLid").setValue("close")
        }
        disconnectBtn.setOnClickListener{
            Toast.makeText(applicationContext,"Disconnected..", Toast.LENGTH_SHORT).show()
            terminateBinProcess()
            startActivity(Intent(applicationContext, Facts::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        terminateBinProcess()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun terminateBinProcess(){
        var newWeight = 0.0
        var userWeight = 0.0
        val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
        var userBin = intent.getStringExtra("userBin").toString()
        var bin_type = intent.getStringExtra("binType").toString()
        val ref = FirebaseDatabase.getInstance().getReference(userBin).child(bin_type)
        ref.child("OTP").setValue(0)
        ref.child("Status").setValue("end")
        ref.child("BinLid").setValue("close")
        ref.child("Weight").get().addOnSuccessListener {
            Log.i("TAG", "Got new value ${it.value}")
            newWeight = it.value.toString().toDouble()
            userWeight = newWeight - oldWeight
            var pointsEarned = userWeight*10
            Log.i("TAG", "Date ${Calendar.getInstance().time}")
            doc.get().addOnSuccessListener {
                var userPoints =pointsEarned + it.data?.get("Points").toString().toInt()
                doc.set(hashMapOf("Points" to userPoints.toInt()), SetOptions.merge())
                var calendar = Calendar.getInstance()
                var simpleDateFormat = SimpleDateFormat("LLL dd,yyyy")
                var dateTime = simpleDateFormat.format(calendar.time).toString()
                var userTransaction = hashMapOf(
                    "Date" to dateTime,
                    "PointsEarned" to pointsEarned,
                    "WasteType" to bin_type,
                    "Bin" to userBin,
                    "WasteWeight" to userWeight
                )
                doc.collection("transactions").add(userTransaction)
            }
        }.addOnFailureListener{
            Log.e("TAG", "Error getting data", it)
        }
    }
}
