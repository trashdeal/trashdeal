package com.example.trashdeal

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class ConnectBin : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_bin)
        auth = FirebaseAuth.getInstance()
        val plasticBtn = findViewById<Button>(R.id.plasticBtn)
        val ewasteBtn = findViewById<Button>(R.id.ewasteBtn)
        val dryBtn = findViewById<Button>(R.id.dryBtn)
        val wetBtn = findViewById<Button>(R.id.wetBtn)
        val getDirectionBtn = findViewById<Button>(R.id.getDirectionBtn)
        var userBinId = intent.getStringExtra("userBin").toString()
        var userBin = ""
        var binLocation = Location("")
        fStore = FirebaseFirestore.getInstance()
        val doc: DocumentReference = fStore.collection("binLocation").document(userBinId)
        doc.get().addOnSuccessListener {
            userBin = it.get("Bin Name").toString()
            binLocation.latitude = it.get("Latitude").toString().toDouble()
            binLocation.longitude = it.get("Longitude").toString().toDouble()
            var plastic_bin = Bin("",0.0,0,0,"","")
            val ref = FirebaseDatabase.getInstance().getReference(userBin)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    plastic_bin = dataSnapshot.child("PlasticBin").getValue(Bin::class.java)!!
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            })
            getDirectionBtn.setOnClickListener{
                val mapsLink = "https://www.google.co.in/maps/dir//${binLocation.latitude},${binLocation.longitude}"
                val uri: Uri? = Uri.parse(mapsLink)
                val intent = Intent(Intent.ACTION_VIEW,uri)
                intent.setPackage("com.google.android.apps.maps")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            plasticBtn.setOnClickListener{
                if (plastic_bin.Status == "start") {
                    Toast.makeText(applicationContext, "Sorry! Bin is in Use", Toast.LENGTH_SHORT).show()
                }
                else if (plastic_bin.WasteLevel >= 90) {
                    Toast.makeText(applicationContext, "Sorry! Bin is Full", Toast.LENGTH_SHORT).show()
                }
                else {
                    ref.child("PlasticBin").child("Status").setValue("start")
                    var OTP = (Math.random() * (99999 - 10000 + 1) + 10000).toInt()
                    ref.child("PlasticBin").child("OTP").setValue(OTP)
                    var otpInp = ""
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Enter OTP displayed on the bin")
                    val input = EditText(this)
                    input.inputType =
                        InputType.TYPE_CLASS_NUMBER
                    builder.setView(input)
                    builder.setPositiveButton("Verify",
                        DialogInterface.OnClickListener { dialog, which ->
                            otpInp = input.text.toString()
                            if (otpInp == "" || OTP != otpInp.toInt()) {
                                ref.child("PlasticBin").child("Status").setValue("end")
                                Toast.makeText(applicationContext, "Invalid OTP..", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val doc1: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
                                doc1.set(hashMapOf("DefaultBin" to userBinId), SetOptions.merge())
                                startActivity(Intent(applicationContext, UseBin::class.java).apply {
                                    putExtra("userBin", userBin)
                                    putExtra("binType", "PlasticBin")
                                })
                            }
                        })
                    builder.setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                            ref.child("PlasticBin").child("Status").setValue("end")
                        })
                    builder.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ -> // Prevent dialog close on back press button
                        keyCode == KeyEvent.KEYCODE_BACK
                    })
                    builder.show()
                }
            }
            ewasteBtn.setOnClickListener{
                Toast.makeText(applicationContext, "Work in Progress", Toast.LENGTH_SHORT).show()
            }
            dryBtn.setOnClickListener{
                Toast.makeText(applicationContext, "Work in Progress", Toast.LENGTH_SHORT).show()
            }
            wetBtn.setOnClickListener{
                Toast.makeText(applicationContext, "Work in Progress", Toast.LENGTH_SHORT).show()
            }
        }
    }
}