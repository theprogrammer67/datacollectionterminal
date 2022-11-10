package ru.rarus.datacollectionterminal.ui.goodlist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.Good

class GoodListViewModel() : ViewModel() {
    var goods = MutableLiveData<List<Good>>()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            goods.postValue(App.database.getDao().getGoodsSync())
        }
    }
}