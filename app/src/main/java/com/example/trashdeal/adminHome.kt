package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class adminHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val logout = findViewById<Button>(R.id.logoutBtn)
        logout.setOnClickListener{
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
            ) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
    }
}