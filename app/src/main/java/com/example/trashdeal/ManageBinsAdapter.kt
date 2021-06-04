package com.example.trashdeal

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*

class ManageBinsAdapter(private val context: Context,
                        private val dataSource: ArrayList<BinLocation>) : BaseAdapter() {
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
        rowView = LayoutInflater.from(context).inflate(R.layout.manage_bin_row, parent, false)
        val binName = rowView.findViewById<TextView>(R.id.binName)
        val binAddress = rowView.findViewById<TextView>(R.id.binAddress)
        val deleteBin = rowView.findViewById<Button>(R.id.deleteBin)
        binName.text = dataSource[position].binName
        binAddress.text = dataSource[position].binAddress
        deleteBin.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Bin")
            builder.setIcon(R.drawable.logout_icon)
            builder.setMessage("Are you sure you want to delete bin ${dataSource[position].binName}?")
            builder.setPositiveButton("YES") { dialog, which ->
                Toast.makeText(context, "Deleting Bin ${dataSource[position].binName}..", Toast.LENGTH_SHORT)
                Log.i("TAGG", "Deleting Bin ${dataSource[position].binName}..")
            }
            builder.setNegativeButton(
                "NO"
            ) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
        return rowView
    }
}