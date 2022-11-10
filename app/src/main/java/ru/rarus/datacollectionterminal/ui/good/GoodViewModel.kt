package ru.rarus.datacollectionterminal.ui.good

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.db.ViewGood

class GoodViewModel : ViewModel() {
    var viewGood = MutableLiveData<ViewGood>()

    init {
        viewGood.value = ViewGood()
    }

    fun getData(goodId: String?) {
        if (goodId == null) return

        viewModelScope.launch(Dispatchers.IO) {
            viewGood.postValue(App.database.getDao().getViewGoodSync(goodId))
        }
    }

}