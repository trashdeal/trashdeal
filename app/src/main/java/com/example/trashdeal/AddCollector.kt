package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityAddCollectorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AddCollector : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityAddCollectorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCollectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Add Trash Collector"
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
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val cName = findViewById<EditText>(R.id.cName)
        val cPhone = findViewById<EditText>(R.id.cPhone)
        val cAddress = findViewById<EditText>(R.id.cAddress)
        val cEmail = findViewById<EditText>(R.id.cEmailAddress)
        val registerCollectorBtn = findViewById<Button>(R.id.registerCollectorBtn)
        registerCollectorBtn.setOnClickListener{
            if(cName.text.isEmpty() || cPhone.text.isEmpty() || cAddress.text.isEmpty() || cEmail.text.isEmpty()){
                Toast.makeText(this,"Empty Fields", Toast.LENGTH_SHORT).show()
            }else{
                val doc: DocumentReference = fStore.collection("TrashCollector").document()
                val collector = hashMapOf(
                    "Name" to cName.text.toString(),
                    "PhoneNumber" to cPhone.text.toString().toLong(),
                    "Address" to cAddress.text.toString(),
                    "Email" to cEmail.text.toString()
                )
                doc.set(collector).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Collector ${cName.text} Registered!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, adminHome::class.java))
                        finish()
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this,"Couldn't Add Info", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}