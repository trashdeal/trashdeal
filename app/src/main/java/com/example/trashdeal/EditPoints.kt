package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityEditPointsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditPoints : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityEditPointsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Edit Points"
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
        val listview = findViewById<ListView>(R.id.listview)
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
            val adapter = EditPointsAdapter(this, pointsList)
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