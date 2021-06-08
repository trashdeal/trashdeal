package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityAdminHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class adminHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityAdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Admin Dashboard"
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
        val editsPointsBtn = findViewById<Button>(R.id.editPoints)
        editsPointsBtn.setOnClickListener{
            startActivity(Intent(applicationContext, EditPoints::class.java))
        }

        val manageBinBtn = findViewById<Button>(R.id.manageBin)
        manageBinBtn.setOnClickListener{
            startActivity(Intent(applicationContext, ManageBins::class.java))
        }

        val fullBinsBtn = findViewById<Button>(R.id.fullBinsBtn)
        fullBinsBtn.setOnClickListener{
            startActivity(Intent(applicationContext, FullBins::class.java))
        }

        val addCollectorBtn = findViewById<Button>(R.id.addCollectorBtn)
        addCollectorBtn.setOnClickListener{
            startActivity(Intent(applicationContext, AddCollector::class.java))
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}