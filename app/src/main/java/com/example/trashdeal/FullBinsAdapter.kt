package com.example.trashdeal

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import java.util.*

class FullBinsAdapter (private val context: Context,
                       private var dataSource: ArrayList<FullBin>) : BaseAdapter() {
    override fun getCount(): Int {
        return dataSource.size
    }
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var rowView = convertView
        rowView = LayoutInflater.from(context).inflate(R.layout.full_bins_row, parent, false)
        val binName = rowView.findViewById<TextView>(R.id.binName)
        val binAddress = rowView.findViewById<TextView>(R.id.binAddress)
        val binType = rowView.findViewById<TextView>(R.id.binType)
        val notifyBtn = rowView.findViewById<Button>(R.id.notifyBtn)
        binName.text = dataSource[position].binName
        binAddress.text = dataSource[position].binAddress
        binType.text = dataSource[position].binType
        notifyBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Notify Collector")
            builder.setIcon(R.drawable.edit_user_icon)
            builder.setMessage("Send notification to collector to collect the waste from bin ${dataSource[position].binType}")
            builder.setPositiveButton("YES") { dialog, which ->

            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() }
            builder.show()
        }
        return rowView
    }
}