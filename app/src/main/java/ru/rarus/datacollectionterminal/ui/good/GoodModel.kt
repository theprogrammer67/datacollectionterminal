package ru.rarus.datacollectionterminal.ui.good

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.ViewGood

class GoodModel {
    fun getData(goodId: String): LiveData<ViewGood> {
        val data = MutableLiveData<ViewGood>()
        GlobalScope.launch {
            val viewGood = App.database.getDao().getViewGood(goodId)
            data.postValue(viewGood)
        }

        return data
    }

}
