package com.iseven.alcosafe

import android.content.ClipData
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class HistoryAdapter(var context: Context, var listDrink: List<Drink>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.item_icon)
        val textView = itemView.findViewById<TextView>(R.id.item_hour)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false))
    }

    override fun getItemCount(): Int {
        return listDrink.size
    }

    fun getItem(position: Int): Drink {
        return listDrink[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item:Drink = getItem(position)
        var name = item.tag
        var timeMS = item.time

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMS
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        if ((hour < 10) && (minute < 10)){
            holder.textView.text = "0$hour:0$minute"
        }
        else if ((hour < 10) && (minute >= 10)){
            holder.textView.text = "0$hour:$minute"
        }
        else if ((hour >= 10) && (minute < 10)){
            holder.textView.text = "$hour:0$minute"
        }
        else{
            holder.textView.text = "$hour:$minute"
        }

        var resID = context.resources.getIdentifier(name, "drawable", context.packageName)
        holder.imageView.setImageResource(resID)
        holder.itemView.setOnLongClickListener {
            deleteDrink(item)
            true
        }
    }
}