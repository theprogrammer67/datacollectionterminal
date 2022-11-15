package ru.rarus.datacollectionterminal

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun enableButton(button: Button, enable: Boolean) {
    button.alpha = if (enable) 1.0f else 0.5f
    button.isClickable = enable;
}
