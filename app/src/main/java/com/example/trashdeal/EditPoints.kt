package com.example.trashdeal

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditPoints : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_points)
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        val listview = findViewById<ListView>(R.id.listView)
        val doc: CollectionReference = fStore.collection("trash_value")
        doc.get().addOnSuccessListener {
            val pointsList : ArrayList<Trash_Value> = ArrayList()
            for(document in it){
                val wasteWeight = document.data["waste_weight"].toString().toInt()
                val wasteType = document.data["waste_type"].toString()
                val wastePoints = document.data["waste_points"].toString().toInt()
                val trashDetails = Trash_Value(wasteWeight,wasteType,wastePoints)
                pointsList.add(trashDetails)
            }
            val adapter = EditPointsAdapter(this, pointsList)
            listview.adapter = adapter
        }
    }
}