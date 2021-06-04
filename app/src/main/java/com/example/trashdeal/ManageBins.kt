package com.example.trashdeal

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ManageBins : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_bins)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val listview = findViewById<ListView>(R.id.listView3)
        val doc: CollectionReference = fStore.collection("binLocation")
        val allBins : ArrayList<BinLocation> = ArrayList()
        doc.get().addOnSuccessListener {
            for(document in it){
                var binLocation: Location = Location("")
                val binName = document.data["Bin Name"].toString()
                binLocation.latitude = document.data["Latitude"] as Double
                binLocation.longitude = document.data["Longitude"] as Double
                val currentBin = BinLocation(document.id, binName,getCityName(binLocation.latitude, binLocation.longitude),"")
                allBins.add(currentBin)
            }
            val adapter = ManageBinsAdapter(this, allBins)
            listview.adapter = adapter
            val searchBar = findViewById<SearchView>(R.id.searchBin)
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
        }
        val addBinsBtn = findViewById<Button>(R.id.addBinsBtn)
        addBinsBtn.setOnClickListener{
            startActivity(Intent(this, AddBins::class.java))
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
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, adminHome::class.java))
    }
}