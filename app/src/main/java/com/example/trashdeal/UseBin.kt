package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class UseBin : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var oldWeight = 0.0
    var timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_bin)
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
        fun startTimer() {
            // TODO Auto-generated method stub
            super.onUserInteraction()
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    ref.child("OTP").setValue(0)
                    ref.child("Status").setValue("end")
                    ref.child("BinLid").setValue("close")
                    timer.cancel()
                    startActivity(Intent(applicationContext, Facts::class.java).apply {
                        putExtra("userBin", userBin)
                        putExtra("binType", binType)
                        putExtra("wasteType", intent.getStringExtra("wasteType").toString())
                        putExtra("oldWeight", oldWeight.toString())
                    })
                    finish() //this will cancel the timer of the system
                }
            }, 30 * 1000)
        }
        startTimer()
        binControlBtn.setOnClickListener{
            if(binControlBtn.isChecked){
                startTimer()
                Toast.makeText(applicationContext, "Opening Bin Lid..", Toast.LENGTH_SHORT).show()
                ref.child("BinLid").setValue("open")
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setTitle("Confirm")
                builder.setIcon(R.drawable.logout_icon)
                builder.setMessage("Close bin and disconnect")
                builder.setPositiveButton("YES") { dialog, which ->
                    timer.cancel()
                    ref.child("OTP").setValue(0)
                    ref.child("Status").setValue("end")
                    ref.child("BinLid").setValue("close")
                    Log.i("TAG", "Got old waste dfver value $oldWeight")
                    startActivity(Intent(applicationContext, Facts::class.java).apply {
                        putExtra("userBin", userBin)
                        putExtra("binType", binType)
                        putExtra("wasteType", intent.getStringExtra("wasteType").toString())
                        putExtra("oldWeight", oldWeight.toString())
                    })
                    finish()
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
    override fun onBackPressed() {
        super.onBackPressed()
        KeyEvent.KEYCODE_BACK
    }
}
