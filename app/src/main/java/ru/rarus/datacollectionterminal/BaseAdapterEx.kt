package ru.rarus.datacollectionterminal

import android.view.View
import android.widget.BaseAdapter

abstract class BaseAdapterEx : BaseAdapter() {

    override fun getItemId(position: Int): Long = position.toLong()

    fun setItemBgColor(position: Int, itemView: View) {
        if (position % 2 == 0) // Раскрасим фон
            itemView.setBackgroundResource(R.color.evenBgColor)
        else
            itemView.setBackgroundResource(R.color.oddBgColor)
    }
}