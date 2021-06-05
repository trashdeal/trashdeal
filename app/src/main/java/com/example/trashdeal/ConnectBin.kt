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
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityConnectBinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*


class ConnectBin : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityConnectBinBinding
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        var userBinId = intent.getStringExtra("userBin").toString()
        var userBin = ""
        var binLocation = Location("")
        fStore = FirebaseFirestore.getInstance()
        val doc: DocumentReference = fStore.collection("binLocation").document(userBinId)
        doc.get().addOnSuccessListener {
            userBin = it.get("Bin Name").toString()
            val ref = FirebaseDatabase.getInstance().getReference(userBin)
            val binNameText = findViewById<TextView>(R.id.binName)
            binNameText.text = "Welcome to $userBin"
            binLocation.latitude = it.get("Latitude").toString().toDouble()
            binLocation.longitude = it.get("Longitude").toString().toDouble()
            var plasticBin = Bin("", 0.0, 0, 0, "", "")
            var ewasteBin = Bin("", 0.0, 0, 0, "", "")
            var dryBin = Bin("", 0.0, 0, 0, "", "")
            var wetBin = Bin("", 0.0, 0, 0, "", "")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    plasticBin = dataSnapshot.child("PlasticBin").getValue(Bin::class.java)!!
                    ewasteBin = dataSnapshot.child("EWaste").getValue(Bin::class.java)!!
                    dryBin = dataSnapshot.child("DryWaste").getValue(Bin::class.java)!!
                    wetBin = dataSnapshot.child("WetWaste").getValue(Bin::class.java)!!
                    fun updateBinRT(binType: Bin, imageViewID: Int){
                        val binIcon = findViewById<ImageView>(imageViewID)
                        when {
                            binType.WasteLevel in 0..30 -> {
                                binIcon.setImageDrawable(resources.getDrawable(R.drawable.binempty))
                            }
                            binType.WasteLevel in 31..50 -> {
                                binIcon.setImageDrawable(resources.getDrawable(R.drawable.binalmostlow))
                            }
                            binType.WasteLevel in 51..89 -> {
                                binIcon.setImageDrawable(resources.getDrawable(R.drawable.binalmostfull))
                            }
                            binType.WasteLevel >=90 -> {
                                binIcon.setImageDrawable(resources.getDrawable(R.drawable.binfull))
                            }
                        }
                    }
                    updateBinRT(plasticBin,R.id.pbinicon)
                    updateBinRT(ewasteBin,R.id.ewasteicon)
                    updateBinRT(dryBin,R.id.drywasteicon)
                    updateBinRT(wetBin,R.id.wetwasteicon)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            })
            val getDirectionBtn = findViewById<Button>(R.id.getLocationBtn)
            val plasticBtn = findViewById<Button>(R.id.plasticBtn)
            val ewasteBtn = findViewById<Button>(R.id.ewasteBtn)
            val dryBtn = findViewById<Button>(R.id.dryBtn)
            val wetBtn = findViewById<Button>(R.id.wetBtn)
            getDirectionBtn.setOnClickListener{
                val mapsLink = "https://www.google.co.in/maps/dir//${binLocation.latitude},${binLocation.longitude}"
                val uri: Uri? = Uri.parse(mapsLink)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            fun useBin(bin: Bin, binType: String, wasteType: String, ){
                when {
                    bin.Status != "free" -> {
                        Toast.makeText(
                            applicationContext,
                            "Sorry! Bin is in Use",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    bin.WasteLevel >= 90 -> {
                        Toast.makeText(applicationContext, "Sorry! Bin is Full", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val timer = Timer()
                        ref.child(binType).child("Status").setValue("start")
                        var OTP = (Math.random() * (99999 - 10000 + 1) + 10000).toInt()
                        ref.child(binType).child("OTP").setValue(OTP)
                        var otpInp = ""
                        val builder: android.app.AlertDialog.Builder =
                            android.app.AlertDialog.Builder(this)
                        builder.setTitle("Enter OTP displayed on the bin")
                        val input = EditText(this)
                        input.inputType =
                            InputType.TYPE_CLASS_NUMBER
                        builder.setView(input)
                        builder.setPositiveButton("Verify",
                            DialogInterface.OnClickListener { dialog, which ->
                                timer.cancel()
                                otpInp = input.text.toString()
                                if (otpInp == "" || OTP != otpInp.toInt()) {
                                    ref.child(binType).child("Status").setValue("end")
                                    ref.child(binType).child("OTP").setValue(1111)
                                    Toast.makeText(applicationContext, "Invalid OTP..", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    ref.child(binType).child("OTP").setValue(2222)
                                    val doc1: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
                                    doc1.set(hashMapOf("DefaultBin" to userBinId), SetOptions.merge())
                                    startActivity(Intent(applicationContext, UseBin::class.java).apply {
                                        putExtra("userBin", userBin)
                                        putExtra("binType", binType)
                                        putExtra("wasteType", wasteType)
                                    })
                                }
                            })
                        builder.setNegativeButton("Cancel",
                            DialogInterface.OnClickListener { dialog, _ ->
                                timer.cancel()
                                dialog.cancel()
                                ref.child(binType).child("Status").setValue("end")
                                ref.child(binType).child("OTP").setValue(0)
                            })
                        builder.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ -> // Prevent dialog close on back press button
                            keyCode == KeyEvent.KEYCODE_BACK
                        })
                        val dialog: android.app.AlertDialog? = builder.create()
                        dialog?.show()
                        dialog?.setCancelable(false)
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                ref.child(binType).child("Status").setValue("end")
                                ref.child(binType).child("OTP").setValue(0)
                                dialog?.dismiss()
                                timer.cancel() //this will cancel the timer of the system
                            }
                        }, 120 * 1000) //120 seconds delay
                    }
                }
            }
            plasticBtn.setOnClickListener{
//                when {
//                    plasticBin.Status != "free" -> {
//                        Toast.makeText(
//                            applicationContext,
//                            "Sorry! Bin is in Use",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    plasticBin.WasteLevel >= 90 -> {
//                        Toast.makeText(applicationContext, "Sorry! Bin is Full", Toast.LENGTH_SHORT).show()
//                    }
//                    else -> {
//                        val timer = Timer()
//                        ref.child("PlasticBin").child("Status").setValue("start")
//                        var OTP = (Math.random() * (99999 - 10000 + 1) + 10000).toInt()
//                        ref.child("PlasticBin").child("OTP").setValue(OTP)
//                        var otpInp = ""
//                        val builder: android.app.AlertDialog.Builder =
//                            android.app.AlertDialog.Builder(this)
//                        builder.setTitle("Enter OTP displayed on the bin")
//                        val input = EditText(this)
//                        input.inputType =
//                            InputType.TYPE_CLASS_NUMBER
//                        builder.setView(input)
//                        builder.setPositiveButton("Verify",
//                            DialogInterface.OnClickListener { dialog, which ->
//                                timer.cancel()
//                                otpInp = input.text.toString()
//                                if (otpInp == "" || OTP != otpInp.toInt()) {
//                                    ref.child("PlasticBin").child("Status").setValue("end")
//                                    ref.child("PlasticBin").child("OTP").setValue(1111)
//                                    Toast.makeText(applicationContext, "Invalid OTP..", Toast.LENGTH_SHORT)
//                                        .show()
//                                } else {
//                                    ref.child("PlasticBin").child("OTP").setValue(2222)
//                                    val doc1: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
//                                    doc1.set(hashMapOf("DefaultBin" to userBinId), SetOptions.merge())
//                                    startActivity(Intent(applicationContext, UseBin::class.java).apply {
//                                        putExtra("userBin", userBin)
//                                        putExtra("binType", "PlasticBin")
//                                        putExtra("wasteType", "Plastic")
//                                    })
//                                }
//                            })
//                        builder.setNegativeButton("Cancel",
//                            DialogInterface.OnClickListener { dialog, _ ->
//                                timer.cancel()
//                                dialog.cancel()
//                                ref.child("PlasticBin").child("Status").setValue("end")
//                                ref.child("PlasticBin").child("OTP").setValue(0)
//                            })
//                        builder.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ -> // Prevent dialog close on back press button
//                            keyCode == KeyEvent.KEYCODE_BACK
//                        })
//                        val dialog: android.app.AlertDialog? = builder.create()
//                        dialog?.show()
//                        dialog?.setCancelable(false)
//                        timer.schedule(object : TimerTask() {
//                            override fun run() {
//                                ref.child("PlasticBin").child("Status").setValue("end")
//                                ref.child("PlasticBin").child("OTP").setValue(0)
//                                dialog?.dismiss()
//                                timer.cancel() //this will cancel the timer of the system
//                            }
//                        }, 120 * 1000) //120 seconds delay
//                    }
//                }
                useBin(plasticBin,"PlasticBin","Plastic-waste")
            }
            ewasteBtn.setOnClickListener{
                useBin(ewasteBin,"EWaste","E-Waste")
            }
            dryBtn.setOnClickListener{
                useBin(dryBin,"DryWaste","Dry-Waste")
            }
            wetBtn.setOnClickListener{
                useBin(wetBin,"WetWaste","Wet-Waste")
            }
        }
        binding = ActivityConnectBinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "My Bin"
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.naView
            .setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.miItem1 -> startActivity(Intent(this, MainActivity::class.java))
                    R.id.miItem2 -> startActivity(Intent(this, MyProfile::class.java))
                    R.id.miItem4 -> startActivity(Intent(this, PointsDetails::class.java))
                    R.id.miItem6 -> startActivity(Intent(this, TandC2::class.java))
                    R.id.miItem3 -> startActivity(Intent(this, Help::class.java))
                    R.id.miItem8 -> startActivity(Intent(this, MoreInformation::class.java))
                    R.id.miItem7 -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Logout")
                        builder.setIcon(R.drawable.logout_icon)
                        builder.setMessage("Are you sure you want to Logout?")
                        builder.setPositiveButton("YES") { dialog, which ->
                            auth.signOut()
                            startActivity(Intent(this, MobnoRegister::class.java))
                            finish()
                        }
                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }
                }
                true
            }

        val adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                conditionNull()
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long ) {
                if(binding.spinner.selectedItemPosition==0){
                }
                if(binding.spinner.selectedItemPosition==1){
                    condition1()
                }
                if(binding.spinner.selectedItemPosition==2){
                    condition2()
                }
                if(binding.spinner.selectedItemPosition==3){
                    condition3()
                }
            }
        }
    }
    private fun conditionNull() {
        Toast.makeText(this, "Nothing selected please select an option", Toast.LENGTH_LONG).show()
    }
    private fun condition1() {
        Toast.makeText(this, "Selected language is " + binding.spinner.selectedItem, Toast.LENGTH_LONG).show()
        val ref = FirebaseDatabase.getInstance().getReference("TestBin")
        ref.child("PlasticBin").child("Language").setValue("english")

    }
    private fun condition2() {
        Toast.makeText(this, "चयनित भाषा हिंदी है (" + binding.spinner.selectedItem + ")", Toast.LENGTH_LONG).show()
        val ref = FirebaseDatabase.getInstance().getReference("TestBin")
        ref.child("PlasticBin").child("Language").setValue("hindi")
    }
    private fun condition3() {
        Toast.makeText(this, "चयनित भाषा कोंकणी है (" + binding.spinner.selectedItem + ")", Toast.LENGTH_LONG).show()
        val ref = FirebaseDatabase.getInstance().getReference("TestBin")
        ref.child("PlasticBin").child("Language").setValue("konkani")
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}