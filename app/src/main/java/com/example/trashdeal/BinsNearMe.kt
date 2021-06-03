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
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.slider.Slider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class BinsNearMe : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var PERMISSION_ID = 1000
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var myLocation: Location = Location("")
    var binLocation: Location = Location("")
    var distanceSet = 20.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bins_near_me)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val backBtn = findViewById<ImageView>(R.id.back_btn2)
        backBtn.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        val listview = findViewById<ListView>(R.id.listView2)
        val distanceSlider = findViewById<Slider>(R.id.slider)
        distanceSlider.setLabelFormatter { value: Float ->
            value.toInt().toString()+"Km"
        }
        fun binList(){
            val doc: CollectionReference = fStore.collection("binLocation")
            doc.get().addOnSuccessListener {
                val binList : ArrayList<BinLocation> = ArrayList()
                for(document in it){
                    binLocation.latitude = document.data["Latitude"] as Double
                    binLocation.longitude = document.data["Longitude"] as Double
                    var results = FloatArray(10)
                    Location.distanceBetween(myLocation.latitude,myLocation.longitude,binLocation.latitude, binLocation.longitude ,results)
                    val distance = String.format("%.1f",results[0]/1000)
                    if(distance.toFloat() <= distanceSet){
                        val currentBin = BinLocation(document.id, document.data["Bin Name"].toString(),getCityName(binLocation.latitude, binLocation.longitude),distance)
                        binList.add(currentBin)
                    }
                }
                val adapter = BinLocationAdapter(this, binList)
                listview.adapter = adapter
                listview.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id -> // set an Intent to Another Activity
                        Log.i("TAG", "Tapped on ${binList[position].binID}")
                        val intent = Intent(this, ConnectBin::class.java)
                        intent.putExtra("userBin", binList[position].binID)
                        startActivity(intent)
                    }
            }
        }
        binList()
        distanceSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                Log.d("TAG", "starteddd $slider")
            }

            override fun onStopTrackingTouch(slider: Slider) {
                Log.d("TAG", "eddd ${slider.value}")
                distanceSet = slider.value.toDouble()
                binList()
            }
        })
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


        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
                        myLocation = location
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
            myLocation = lastLocation
            Log.d("Debug:","Your LAst Longitude: ${lastLocation.longitude} Latitude: ${lastLocation.latitude}")
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
        return cityName+","+countryName
    }
}