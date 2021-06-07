package com.example.trashdeal

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FullBins : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_bins)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val listview = findViewById<ListView>(R.id.listview)
        val doc: CollectionReference = fStore.collection("binLocation")
        val fullBins : ArrayList<FullBin> = ArrayList()
        doc.get().addOnSuccessListener {
            for(document in it){
                val binTypes = arrayOf("DryWaste","EWaste","PlasticBin","WetWaste")
                val refMain = FirebaseDatabase.getInstance().getReference(document.data["Bin Name"].toString())
                for(bin in binTypes){
                    refMain.child(bin).child("WasteLevel").get().addOnSuccessListener {
                        if(it.value.toString().toInt() >= 90){
                            var binLocation: Location = Location("")
                            val binName = document.data["Bin Name"].toString()
                            binLocation.latitude = document.data["Latitude"] as Double
                            binLocation.longitude = document.data["Longitude"] as Double
                            val fullBin = FullBin(binName,bin,getCityName(binLocation.latitude,binLocation.longitude))
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
}