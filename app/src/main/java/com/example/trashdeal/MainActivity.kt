package com.example.trashdeal


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        if(auth.currentUser != null) {
            val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
            doc.get().addOnSuccessListener {
                if(it.exists()){
                    binding = ActivityMainBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    supportActionBar!!.title = "My Dashboard"
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
                                    builder.setPositiveButton("YES") { dialog, which ->
                                        auth.signOut()
                                        startActivity(Intent(this, MobnoRegister::class.java))
                                        finish()
                                    }
                                    builder.setNegativeButton(
                                        "NO"
                                    ) { dialog, which -> dialog.dismiss() }
                                    builder.show()
                            }
                        }
                        true
                    }
                    val useBin = findViewById<Button>(R.id.buttonbin)
                    val points = findViewById<TextView>(R.id.points)
                    val pointsBtn = findViewById<Button>(R.id.buttonscore)
                    val trashHistory = findViewById<Button>(R.id.buttonhistory)
                    val binNearMeBtn = findViewById<Button>(R.id.buttonbinsnear)
                    doc.get().addOnSuccessListener { points.text = it.data?.get("Points").toString() }
                    val user = it.data
                    useBin.setOnClickListener{
                        if(!user?.get("DefaultBin")?.equals("")!!){
                            startActivity(Intent(applicationContext, ConnectBin::class.java).apply {
                                putExtra("userBin", user?.get("DefaultBin").toString())
                            })
                        }else{
                            startActivity(Intent(applicationContext, BinsNearMe::class.java))
                        }
                    }
                    pointsBtn.setOnClickListener{
                        startActivity(Intent(applicationContext, PointsRedeem::class.java))
                    }
                    trashHistory.setOnClickListener{
                        startActivity(Intent(applicationContext, UserTransactions::class.java))
                    }
                    binNearMeBtn.setOnClickListener{
                        startActivity(Intent(applicationContext, BinsNearMe::class.java))
                    }
                }else {
                    startActivity(Intent(applicationContext, RegisterUser::class.java))
                    finish()
                }
            }
        }else{
            startActivity(Intent(this, GettingStarted::class.java))
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}