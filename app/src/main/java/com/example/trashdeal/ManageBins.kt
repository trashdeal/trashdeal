package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityAdminHomeBinding
import com.example.trashdeal.databinding.ActivityEditPointsBinding
import com.example.trashdeal.databinding.ActivityManageBinsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ManageBins : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityManageBinsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBinsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Manage Bins"
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
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
        val listview = findViewById<ListView>(R.id.listView3)
        val doc: CollectionReference = fStore.collection("binLocation")
        val allBins : ArrayList<BinLocation> = ArrayList()
        doc.get().addOnSuccessListener {
            for(document in it){
                var binLocation: Location = Location("")
                val binName = document.data["Bin Name"].toString()
                binLocation.latitude = document.data["Latitude"] as Double
                binLocation.longitude = document.data["Longitude"] as Double
                val currentBin = BinLocation(document.id, binName,getCityName(binLocation.latitude, binLocation.longitude),0.0)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}