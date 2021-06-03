package com.example.trashdeal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import java.util.*

class BinLocationAdapter(private val context: Context,
                         private val dataSource: ArrayList<BinLocation> ) : BaseAdapter() {
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
        rowView = LayoutInflater.from(context).inflate(R.layout.bin_row, parent, false)
        val binName = rowView.findViewById<TextView>(R.id.binName)
        val binAddress = rowView.findViewById<TextView>(R.id.binAddress)
        val binDistance = rowView.findViewById<TextView>(R.id.binDistance)
        val viewBtn = rowView.findViewById<Button>(R.id.goToBinBtn)
        binName.text = dataSource[position].binName
        binAddress.text = dataSource[position].binAddress
        binDistance.text = dataSource[position].binDistance+"km away"
        viewBtn.setOnClickListener{
//            Log.i("TAG", "Tapped on ${dataSource[position].binID}")
//            startActivity(Intent(context, ConnectBin::class.java).apply {
//                putExtra("userBin", dataSource[position].binID)
//            })
        }
        return rowView
    }
}
