package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUser : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    var userID:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = auth.currentUser.uid
        val doc:DocumentReference = fStore.collection("user").document(userID)
        val submitBtn = findViewById<Button>(R.id.submit_btn)
        submitBtn.setOnClickListener {
            val fName = findViewById<EditText>(R.id.first_name).text.toString()
            val sName = findViewById<EditText>(R.id.second_name).text.toString()
            val sEmail = findViewById<EditText>(R.id.email).text.toString()
            if (fName.isEmpty() || sName.equals("") || sEmail.equals("")) {
                Toast.makeText(this,"Empty Fields", Toast.LENGTH_SHORT).show()
            } else {
                val user = hashMapOf("FirstName" to fName,"LastName" to sName,"Email" to sEmail, "DefaultBin" to "", "Points" to 0)
                doc.set(user).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
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
}
