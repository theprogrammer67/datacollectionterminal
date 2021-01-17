package ru.rarus.datacollectionterminal

import android.graphics.Color
import android.view.View
import android.widget.BaseAdapter

abstract class BaseAdapterEx : BaseAdapter() {
    var evenBgColor: Int = Color.parseColor("#C0DAFF")
    var oddBgColor: Int = Color.parseColor("#DAE8FC")

    override fun getItemId(position: Int): Long = position.toLong()

    fun setItemBgColor(position: Int, itemView: View) {
        if (position % 2 == 0) // Раскрасим фон
            itemView.setBackgroundColor(evenBgColor)
        else
            itemView.setBackgroundColor(oddBgColor)
    }
}