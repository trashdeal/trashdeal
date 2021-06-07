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
import com.example.trashdeal.databinding.ActivityMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mikhaellopez.circularimageview.CircularImageView


class MyProfile : AppCompatActivity() {
    lateinit var binding: ActivityMyProfileBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "My Profile"
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
        val circularImageView = findViewById<CircularImageView>(R.id.circularImageView)
        val fName = findViewById<EditText>(R.id.editText2)
        val lName = findViewById<EditText>(R.id.editText)
        val email = findViewById<EditText>(R.id.editText3)
        val doc: DocumentReference = fStore.collection("user").document(auth.currentUser.uid)
        doc.get().addOnSuccessListener {
            fName.setText(it.data?.get("FirstName").toString())
            lName.setText(it.data?.get("LastName").toString())
            email.setText(it.data?.get("Email").toString())
        }
        val saveBtn = findViewById<Button>(R.id.save)
        saveBtn.setOnClickListener{
            if (fName.text.toString().isEmpty() || lName.text.toString().isEmpty() || email.text.toString().isEmpty()) {
                Toast.makeText(this,"Empty Fields", Toast.LENGTH_SHORT).show()
            } else {
                val user = hashMapOf("FirstName" to fName.text.toString(),"LastName" to lName.text.toString(),"Email" to email.text.toString())
                doc.set(user, SetOptions.merge()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Saved Changes", Toast.LENGTH_SHORT).show()
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
        val cancelBtn = findViewById<Button>(R.id.cancel)
        cancelBtn.setOnClickListener{
            startActivity(Intent(applicationContext, MainActivity::class.java))
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