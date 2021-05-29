package com.example.trashdeal

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import java.util.concurrent.TimeUnit

class MobnoRegister : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobno_register)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val phoneNo = findViewById<EditText>(R.id.phone)
        val nextBtn = findViewById<Button>(R.id.getOtpBtn)
        val textView1 = findViewById<TextView>(R.id.textView1)
        @Suppress("DEPRECATION") val color: Int = getResources().getColor(R.color.colorPrimaryDark)
        val text1 = "We will send you an OTP verification"
        val s1 = SpannableString(text1)
        val ssgreen = ForegroundColorSpan(color)
        s1.setSpan(ssgreen, 0, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView1.text=s1
        val ccp = findViewById<CountryCodePicker>(R.id.ccp).selectedCountryCode.toString()
        var mobNo = ""
        nextBtn.setOnClickListener {
            mobNo = "+$ccp ${phoneNo.text.toString()}"
            Log.d("TAG", "Mobile No: $mobNo")
            if(mobNo.isEmpty() ||phoneNo.text.length != 10){
                Toast.makeText(applicationContext, "Enter Valid Mobile Number", Toast.LENGTH_LONG).show()
            }else{
                sendVerification(mobNo)
            }
        }
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                val intent = Intent(applicationContext, OtpRegister::class.java).apply {
                    putExtra("Phone", mobNo)
                    putExtra("otp",storedVerificationId)
                }
                startActivity(intent)
                finish()
            }
        }
    }
    private fun sendVerification(mobNo: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(mobNo) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}