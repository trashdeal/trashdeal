package com.example.trashdeal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.trashdeal.databinding.ActivityUserTransactionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserTransactions : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityUserTransactionsBinding
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "My Trash History"
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
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val gridview = findViewById<ListView>(R.id.listView)
        val doc: CollectionReference = fStore.collection("user").document(auth.currentUser.uid).collection(
            "transactions"
        )
        doc.get().addOnSuccessListener {
            val transactions : ArrayList<Transaction> = ArrayList()
            for(document in it){
                val transaction = Transaction(
                    document.data["Date"].toString(),
                    document.data["Bin"].toString(),
                    document.data["WasteWeight"] as Double,
                    document.data["WasteType"].toString(),
                    document.data["PointsEarned"].toString().toDouble().toInt()
                )
                transactions.add(transaction)
            }
            Log.d("TAG", "Transactions List: $transactions")
            val adapter = TransactionAdapter(this, transactions)
            gridview.adapter = adapter
        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}