package ru.rarus.datacollectionterminal.ui.good

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.rarus.datacollectionterminal.db.ViewGood

class GoodViewModel: ViewModel() {
    var activity: GoodActivity? = null
    var document = MutableLiveData<ViewGood>()

}