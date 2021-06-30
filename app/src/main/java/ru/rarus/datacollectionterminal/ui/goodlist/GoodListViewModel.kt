package ru.rarus.datacollectionterminal.ui.goodlist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.Good

class GoodListViewModel() : ViewModel() {
    var goods = MutableLiveData<List<Good>>()

    fun getData(owner: LifecycleOwner) {
        val data = MutableLiveData<List<Good>>()
        GlobalScope.launch(Dispatchers.IO) {
            data.postValue(App.database.getDao().getGoodsSync())
        }
        data.observe(owner, { goods.value = it })
    }
}