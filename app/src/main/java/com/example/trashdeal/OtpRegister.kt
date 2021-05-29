package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class OtpRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    lateinit var storedVerificationId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_register)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val phoneNo = intent.getStringExtra("Phone")
        storedVerificationId = intent.getStringExtra("otp").toString()
        val mobnoDisp = findViewById<TextView>(R.id.mobno)
        mobnoDisp.text = "${mobnoDisp.text} $phoneNo"
        val otpInp = findViewById<EditText>(R.id.otp)
        val verifyBtn = findViewById<Button>(R.id.verifyBtn)
        verifyBtn.setOnClickListener {
            var otp:String = otpInp.text.toString()
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(storedVerificationId, otp)
            verifyAuth(credential)
        }

        val resendBtn = findViewById<Button>(R.id.resendBtn)
        resendBtn.setOnClickListener{

        }

    }

    private fun verifyAuth(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}