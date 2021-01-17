package ru.rarus.datacollectionterminal.ui.goodlist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.Good

class GoodListViewModel() : ViewModel() {
    var goods = MutableLiveData<List<Good>>()

    fun getData(owner: LifecycleOwner) {
        App.database.getDao().getGoods()
            .observe(owner, { goods.value = it ?: ArrayList() })
    }

}