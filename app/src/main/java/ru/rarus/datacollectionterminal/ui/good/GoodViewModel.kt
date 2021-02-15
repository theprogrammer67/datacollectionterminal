package ru.rarus.datacollectionterminal.ui.good

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.db.ViewGood
import ru.rarus.datacollectionterminal.observeOnce

class GoodViewModel : ViewModel() {
    var activity: GoodActivity? = null
    var viewGood = MutableLiveData<ViewGood>()
    var model = GoodModel()

    init {
        viewGood.value = ViewGood()
    }

    fun getData(goodId: String?) {
        if (goodId == null) return

        model.getData(goodId).observeOnce(activity!!, {
            if (it != null) {
                it.saved = true
                viewGood.value = it
            }
        })
    }

}