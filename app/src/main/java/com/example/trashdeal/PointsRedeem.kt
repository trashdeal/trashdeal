package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityPointsRedeemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PointsRedeem : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityPointsRedeemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsRedeemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Redeem Points"
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
                        builder.setPositiveButton("YES") { _, _ ->
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
        findViewById<TextView>(R.id.ptsdisplay).text = intent.getStringExtra("redeemPoints").toString()
        val redeem = findViewById<Button>(R.id.redeemptsbtn)
        redeem.setOnClickListener{
            Toast.makeText(applicationContext, "Work in Progress", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}