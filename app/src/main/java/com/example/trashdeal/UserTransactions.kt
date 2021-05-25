package com.example.trashdeal

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserTransactions : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_transactions)
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
}