package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
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
    var timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_bin)
        startTimer()
        val binControlBtn = findViewById<ToggleButton>(R.id.binControl)
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
        binControlBtn.setOnClickListener{it
            if(binControlBtn.isChecked){
                startTimer()
                Toast.makeText(applicationContext, "Opening Bin Lid..", Toast.LENGTH_SHORT).show()
                ref.child("BinLid").setValue("open")
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle("Confirm")
                builder.setIcon(R.drawable.logout_icon)
                builder.setMessage("Close bin and disconnect..")
                builder.setPositiveButton("YES") { dialog, which ->
                    terminateBinProcess()
                    timer.cancel()
                    startActivity(Intent(applicationContext, Facts::class.java))
                    finish()
                    Toast.makeText(applicationContext, "Closing Bin Lid..", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("NO"){ dialog, _ ->
                    startTimer()
                    binControlBtn.isChecked = true
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }
    private fun startTimer() {
        // TODO Auto-generated method stub
        super.onUserInteraction()
        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                terminateBinProcess()
                startActivity(Intent(applicationContext, Facts::class.java))
                finish()
                timer.cancel() //this will cancel the timer of the system
            }
        }, 30 * 1000)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        terminateBinProcess()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
    private fun terminateBinProcess(){
        var newWeight: Double
        var userWeight: Double
        val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
        var userBin = intent.getStringExtra("userBin").toString()
        var bin_type = intent.getStringExtra("binType").toString()
        var waste_type = intent.getStringExtra("wasteType").toString()
        val ref = FirebaseDatabase.getInstance().getReference(userBin).child(bin_type)
        ref.child("OTP").setValue(0)
        ref.child("Status").setValue("end")
        ref.child("BinLid").setValue("close")
        ref.child("Weight").get().addOnSuccessListener {
            Log.i("TAG", "Got new value ${it.value}")
            newWeight = it.value.toString().toDouble()
            userWeight = newWeight - oldWeight
            var pointsEarned = (userWeight*10).toInt()
            Log.i("TAG", "Date ${Calendar.getInstance().time}")
            if(pointsEarned!= 0) {
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
        }.addOnFailureListener{
            Log.e("TAG", "Error getting data", it)
        }
    }
}
