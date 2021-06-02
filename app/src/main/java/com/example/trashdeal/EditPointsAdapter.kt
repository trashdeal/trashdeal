package com.example.trashdeal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var rowView = convertView
        rowView = LayoutInflater.from(context).inflate(R.layout.edit_points_row, parent, false)
        rowView.findViewById<TextView>(R.id.weightText).text = dataSource[position].waste_weight.toString()
        rowView.findViewById<TextView>(R.id.typeText).text = dataSource[position].waste_type
        rowView.findViewById<TextView>(R.id.pointsText).text = dataSource[position].waste_points.toString()
        val editBtn = rowView.findViewById<Button>(R.id.editPointsBtn)
        editBtn.setOnClickListener{
            Toast.makeText(context, "Work in Progress: ${dataSource[position].waste_points}", Toast.LENGTH_SHORT).show()
        }
        return rowView
    }
}