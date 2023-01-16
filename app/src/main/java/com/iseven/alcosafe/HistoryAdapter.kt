package com.iseven.alcosafe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class HistoryAdapter(var context: Context, var listDrink: List<Drink>): BaseAdapter(){

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    //constructor(context: Context, listDrink: List<Drink>)

    override fun getCount(): Int {
        return listDrink.size
    }

    override fun getItem(position: Int): Drink {
        return listDrink[position]
    }

    override fun getItemId(position: Int): Long {
        return listDrink[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = inflater.inflate(R.layout.adapter_item, null)

        var item:Drink = getItem(position)
        var name = item.tag
        var timeMS = item.time

        var textView: TextView = convertView.findViewById(R.id.item_hour)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMS
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        if ((hour < 10) && (minute < 10)){
            textView.text = "0$hour:0$minute"
        }
        else if ((hour < 10) && (minute >= 10)){
            textView.text = "0$hour:$minute"
        }
        else if ((hour >= 10) && (minute < 10)){
            textView.text = "$hour:0$minute"
        }
        else{
            textView.text = "$hour:$minute"
        }

        var itemImageView: ImageView = convertView.findViewById(R.id.item_icon)
        var resID = context.resources.getIdentifier(name, "drawable", context.packageName)
        itemImageView.setImageResource(resID)

        return convertView
    }
}