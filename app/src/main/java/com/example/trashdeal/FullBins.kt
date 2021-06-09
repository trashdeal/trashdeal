package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityFullBinsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FullBins : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityFullBinsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullBinsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Full Bins"
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.naView2
            .setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.miItem11 -> startActivity(Intent(this, adminHome::class.java))
                    R.id.miItem77 -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Logout")
                        builder.setIcon(R.drawable.logout_icon)
                        builder.setMessage("Are you sure you want to Logout?")
                        builder.setPositiveButton("YES") { _, _ ->
                            auth.signOut()
                            startActivity(Intent(this, MobnoRegister::class.java))
                            this.finish()
                        }
                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }
                }
                true
            }
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val listview = findViewById<ListView>(R.id.listview)
        val doc: CollectionReference = fStore.collection("binLocation")
        val fullBins : ArrayList<FullBin> = ArrayList()
        doc.get().addOnSuccessListener {
            for(document in it){
                val binTypes = arrayOf("DryWaste","EWaste","PlasticBin","WetWaste")
                val refMain = FirebaseDatabase.getInstance().getReference(document.data["Bin Name"].toString())
                val collector = document.data["TrashCollector"].toString()
                for(bin in binTypes){
                    refMain.child(bin).child("WasteLevel").get().addOnSuccessListener {
                        if(it.value.toString().toInt() >= 90){
                            var binLocation: Location = Location("")
                            val binName = document.data["Bin Name"].toString()
                            binLocation.latitude = document.data["Latitude"] as Double
                            binLocation.longitude = document.data["Longitude"] as Double
                            val fullBin = FullBin(binName,bin,getCityName(binLocation.latitude,binLocation.longitude),collector)
                            fullBins.add(fullBin)
                            val adapter = FullBinsAdapter(this, fullBins)
                            listview.adapter = adapter
                        }
                    }.addOnFailureListener {
                        Log.e("TAG", "Error getting data", it)
                    }
                }
            }
        }
    }
    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)
        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        Log.d("Debug:", "Your City: $cityName ; your Country $countryName")
        return "$cityName,$countryName"
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}