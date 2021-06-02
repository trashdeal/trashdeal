package com.example.trashdeal

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class EditPointsAdapter(private val context: Context,
                        private val dataSource: ArrayList<Trash_Value>) : BaseAdapter() {
    //1
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    private lateinit var fStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        auth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        var rowView = convertView
        rowView = LayoutInflater.from(context).inflate(R.layout.edit_points_row, parent, false)
        rowView.findViewById<TextView>(R.id.weightText).text = dataSource[position].waste_weight
        rowView.findViewById<TextView>(R.id.typeText).text = dataSource[position].waste_type
        rowView.findViewById<TextView>(R.id.pointsText).text = dataSource[position].waste_points.toString()
        val editBtn = rowView.findViewById<Button>(R.id.editPointsBtn)
        editBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Edit Points")
            builder.setIcon(R.drawable.logout_icon)
            builder.setMessage("Enter the updated points for ${dataSource[position].waste_type}")
            val input = EditText(context)
            input.inputType =
                InputType.TYPE_CLASS_NUMBER
            builder.setView(input)
            builder.setPositiveButton("YES") { dialog, which ->
                val updatedPoints = input.text.toString().toInt()
                dataSource[position].waste_points = updatedPoints
                val doc: DocumentReference = fStore.collection("trash_value").document(dataSource[position].id)
                doc.set(hashMapOf("waste_points" to updatedPoints), SetOptions.merge())
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() }
            builder.show()
        }
        return rowView
    }
}