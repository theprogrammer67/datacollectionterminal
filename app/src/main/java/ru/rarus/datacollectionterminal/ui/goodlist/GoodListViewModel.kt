package ru.rarus.datacollectionterminal.ui.goodlist

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.rarus.datacollectionterminal.App
import ru.rarus.datacollectionterminal.R
import ru.rarus.datacollectionterminal.db.entities.Good
import java.util.ArrayList

class GoodListViewModel() : ViewModel() {
    var list = MutableLiveData<List<Good>>()
    val selectedItems: ArrayList<String> = ArrayList()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            list.postValue(App.database.getDao().getGoodsSync())
        }
    }

    fun deleteSelectedItems() {
        if (selectedItems.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                selectedItems.forEach {
                    try {
                        App.database.getDao().deleteGoodSync(it)
                    } catch (e: SQLiteConstraintException) {
                        withContext(Dispatchers.Main) {
                            App.showMessage(App.context.getString(R.string.error_delete_good))
                        }
                    }
                }
                list.postValue(App.database.getDao().getGoodsSync())
                selectedItems.clear()
            }
        }
    }
}