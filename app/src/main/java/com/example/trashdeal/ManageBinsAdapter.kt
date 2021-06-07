package com.example.trashdeal

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ManageBinsAdapter(private val context: Context,
                        private var dataSource: ArrayList<BinLocation>) : BaseAdapter(), Filterable{
    private val dataSourceFilter = dataSource
    private lateinit var fStore: FirebaseFirestore
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
        fStore = FirebaseFirestore.getInstance()
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
            builder.setIcon(R.drawable.deleteicon)
            builder.setMessage("Are you sure you want to delete bin ${dataSource[position].binName}?")
            builder.setPositiveButton("YES") { dialog, which ->
                Toast.makeText(context, "Deleting Bin ${dataSource[position].binName}..", Toast.LENGTH_SHORT)
                fStore.collection("binLocation").document(dataSource[position].binID).delete()
                FirebaseDatabase.getInstance().getReference(binName.text.toString()).removeValue()
                Log.i("TAGG", "Deleting Bin ${dataSource[position].binName}..")
                context.startActivity(Intent(context, ManageBins::class.java))
            }
            builder.setNegativeButton(
                "NO"
            ) { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
        return rowView
    }

    override fun getFilter(): Filter {
        return customFilter
    }
    private val customFilter =object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<BinLocation>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(dataSourceFilter)
            } else {
                for (item in dataSourceFilter) {
                    if (item.binName.toLowerCase()
                            .startsWith(constraint.toString().toLowerCase()) ||
                        item.binAddress.toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            dataSource = results?.values as ArrayList<BinLocation>
            notifyDataSetChanged()
        }
    }
}