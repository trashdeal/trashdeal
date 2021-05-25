package com.example.trashdeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

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
        val ccp = intent.getStringExtra("ccp")
        storedVerificationId = intent.getStringExtra("otp").toString()
        val mobnoDisp = findViewById<TextView>(R.id.mobno)
        mobnoDisp.text = "+$ccp $phoneNo"
        val otpInp = findViewById<EditText>(R.id.otp)
        val verifyBtn = findViewById<Button>(R.id.verifyBtn)
        verifyBtn.setOnClickListener {
            var otp:String = otpInp.text.toString()
                if(otp.isNotEmpty() && otp.length == 6) {
                    val credential: PhoneAuthCredential =
                        PhoneAuthProvider.getCredential(storedVerificationId, otp)
                    verifyAuth(credential)
                } else{
                    Toast.makeText(applicationContext, "(Invalid OTP)", Toast.LENGTH_LONG).show()
                }
        }

    }

    private fun verifyAuth(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
                    doc.get().addOnSuccessListener {
                        if(it.exists()){
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                        }else {
                            startActivity(Intent(applicationContext, RegisterUser::class.java))
                            finish()
                        }
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}