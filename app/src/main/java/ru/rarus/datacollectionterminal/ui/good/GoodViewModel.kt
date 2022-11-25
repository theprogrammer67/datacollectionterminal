package ru.rarus.datacollectionterminal.ui.good

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.db.dao.GoodDao
import ru.rarus.datacollectionterminal.db.entities.ViewGood
import ru.rarus.datacollectionterminal.db.models.GoodModel
import java.util.ArrayList

class GoodViewModel : ViewModel() {
    var viewGood = MutableLiveData<ViewGood>()
    val selectedItems: ArrayList<String> = ArrayList()
    private var goodId: String? = null

    init {
        viewGood.value = ViewGood()
    }

    fun getData(goodId: String?) {
        if (goodId == null) return

        this.goodId = goodId
        viewModelScope.launch(Dispatchers.IO) {
            viewGood.postValue(GoodModel.getViewGood(goodId))
        }
    }

    fun deleteSelectedItems() {
        if (goodId == null) return

        if (selectedItems.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    GoodModel.deleteUnit(selectedItems)
                } catch (e: SQLiteConstraintException) {
                    withContext(Dispatchers.Main) {
                        App.showMessage(App.context.getString(R.string.error_delete_good))
                    }
                }
                viewGood.postValue(GoodModel.getViewGood(goodId!!))
                selectedItems.clear()
            }
        }
    }

}