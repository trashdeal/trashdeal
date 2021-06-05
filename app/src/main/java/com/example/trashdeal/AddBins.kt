package com.example.trashdeal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddBins : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var PERMISSION_ID = 1000
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var binLocation: Location = Location("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bins)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        var binName = findViewById<EditText>(R.id.binNameInp)
        var binLocationlatitude = findViewById<EditText>(R.id.binLatInp)
        var binLocationlongitude = findViewById<EditText>(R.id.binLongInp)
        var binAddress = findViewById<EditText>(R.id.binAddressInp)
        val doc: DocumentReference = fStore.collection("binLocation").document()
        val addBtn = findViewById<Button>(R.id.addBinBtn)
        addBtn.setOnClickListener{
            if(binName.text.toString().isEmpty() || binLocationlatitude.text.toString().isEmpty() ||
                binLocationlongitude.text.toString().isEmpty() || binAddress.text.toString().isEmpty() ){
                Toast.makeText(this,"Empty Fields", Toast.LENGTH_SHORT).show()
            }else{
                val binLoc = hashMapOf("Bin Name" to binName.text.toString(),
                    "Latitude" to binLocationlatitude.text.toString().toDouble(),
                    "Longitude" to binLocationlongitude.text.toString().toDouble())
                doc.set(binLoc).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val refMain = FirebaseDatabase.getInstance().getReference(binName.text.toString())
                        fun addRTBin(binType: String){
                            val ref = refMain.child(binType)
                            ref.child("BinLid").setValue("close")
                            ref.child("Language").setValue("none")
                            ref.child("OTP").setValue(0)
                            ref.child("Status").setValue("free")
                            ref.child("WasteLevel").setValue(0)
                            ref.child("Weight").setValue(0)
                        }
                        addRTBin("PlasticBin")
                        addRTBin("EWaste")
                        addRTBin("DryWaste")
                        addRTBin("WetWaste")
                        Toast.makeText(this,"Bin ${binName.text} Added!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ManageBins::class.java))
                        finish()
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this,"Couldn't Add Info", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        val currentLocBtn = findViewById<Button>(R.id.currentLocBtn)
        currentLocBtn.setOnClickListener{
            binLocationlatitude.setText(binLocation.latitude.toString())
            binLocationlongitude.setText(binLocation.longitude.toString())
            binAddress.setText(getCityName(binLocation.latitude,binLocation.longitude))
        }
    }
    private fun CheckPermission():Boolean{
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    private fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false


        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }
    private fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location: Location? = task.result
                    if(location == null){
                        NewLocationData()
                    }else{
                        binLocation = location
                        Log.d("Debug:" ,"Your Longitude: ${location.longitude} Latitude: ${location.latitude}"  )
                    }
                }
            }else{
                Toast.makeText(this,"Please Turn on Your device Location", Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }
    private fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            binLocation = lastLocation
            Log.d("Debug:","Your LAst Longitude: ${lastLocation.longitude} Latitude: ${lastLocation.latitude}")
        }
    }
    private fun getCityName(lat: Double,long: Double):String{
        var cityName = ""
        var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(lat,long,3)
        cityName = Adress[0].locality
        countryName = Adress[0].countryName
        Log.d("Debug:", "Your City: $cityName ; your Country $countryName")
        return "$cityName,$countryName"
    }
}