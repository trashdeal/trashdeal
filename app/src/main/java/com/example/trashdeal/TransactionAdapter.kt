package com.example.trashdeal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class TransactionAdapter(private val context: Context,
                         private val dataSource: ArrayList<Transaction>) : BaseAdapter() {

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
        // Get view for row item
        rowView = LayoutInflater.from(context).inflate(R.layout.user_transaction_row, parent, false)
        val date = rowView.findViewById<TextView>(R.id.date)
        val wasteWeight = rowView.findViewById<TextView>(R.id.wasteWeight)
        val wasteType = rowView.findViewById<TextView>(R.id.wasteType)
        val points = rowView.findViewById<TextView>(R.id.pointsEarned)
        val bin = rowView.findViewById<TextView>(R.id.binName)
        date.text = dataSource[position].date
        wasteWeight.text = dataSource[position].wasteWeight.toString()+"Kg"
        wasteType.text = dataSource[position].wasteType
        points.text = "+${dataSource[position].points.toString()}xp"
        bin.text = dataSource[position].bin
        return rowView
    }
}
