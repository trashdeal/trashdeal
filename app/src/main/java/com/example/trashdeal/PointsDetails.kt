package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityPointsDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PointsDetails : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityPointsDetailsBinding
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Point Details"
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
                    R.id.miItem1 -> startActivity(Intent(this, MainActivity::class.java));
                    R.id.miItem2 -> startActivity(Intent(this, MyProfile::class.java));
                    R.id.miItem4 -> startActivity(Intent(this, PointsDetails::class.java));
                    R.id.miItem6 -> startActivity(Intent(this, TandC2::class.java));
                    R.id.miItem3 -> startActivity(Intent(this, Help::class.java));
                    R.id.miItem8 -> startActivity(Intent(this, MoreInformation::class.java));
                    R.id.miItem7 -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Logout")
                        builder.setIcon(R.drawable.logout_icon)
                        builder.setMessage("Are you sure you want to Logout?")
                        builder.setPositiveButton("YES") { dialog, which ->
                            startActivity(Intent(this, MobnoRegister::class.java))
                        }
                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, which -> dialog.dismiss() }
                        builder.show()
                    }
                }
                true
            }
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val listview = findViewById<ListView>(R.id.listView)
        val doc: CollectionReference = fStore.collection("trash_value")
        doc.get().addOnSuccessListener {
            val pointsList : ArrayList<Trash_Value> = ArrayList()
            for(document in it){
                val wasteWeight = document.data["waste_weight"].toString()+"Kg"
                val wasteType = document.data["waste_type"].toString()
                var wastePoints = document.data["waste_points"].toString().toInt()
                val trashDetails = Trash_Value(document.id ,wasteWeight,wasteType,wastePoints)
                pointsList.add(trashDetails)
            }
            val adapter = PointsDetailsAdapter(this, pointsList)
            listview.adapter = adapter
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}