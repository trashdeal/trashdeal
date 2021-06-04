package com.example.trashdeal

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
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
        }
        val searchBar = findViewById<SearchView>(R.id.searchBin)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchBar.clearFocus()
                for(d in allBins){
                    if(d.binName == query ){
                        val allBins : ArrayList<BinLocation> = ArrayList()
                        doc.document(d.binID).get().addOnSuccessListener {
                            var binLocation: Location = Location("")
                            val binName = it.data?.get("Bin Name").toString()
                            binLocation.latitude = it.data?.get("Latitude") as Double
                            binLocation.longitude = it.data?.get("Longitude") as Double
                            val currentBin = BinLocation(it.id, binName,getCityName(binLocation.latitude, binLocation.longitude),"")
                            allBins.add(currentBin)
                        }
                        val adapter = ManageBinsAdapter(applicationContext, allBins)
                        listview.adapter = adapter
                    }else{
                        Toast.makeText(applicationContext, "Item not found", Toast.LENGTH_SHORT)
                    }
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        var countryName = ""
        var postalCode = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)

        cityName = Adress.get(0).locality
        countryName = Adress.get(0).countryName
        postalCode = Adress.get(0).postalCode
        Log.d("Debug:", "Your City: $cityName ; your Country $countryName")
        return "$cityName,$countryName-$postalCode"
    }
}