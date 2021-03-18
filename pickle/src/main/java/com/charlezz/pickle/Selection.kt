package com.charlezz.pickle

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.charlezz.pickle.data.entity.Media
import kotlinx.parcelize.Parcelize

@Parcelize
class Selection(private val selectedItems: LinkedHashMap<Long, Media> = LinkedHashMap()) :
    Parcelable {

    private val count = MutableLiveData(selectedItems.size)

    fun select(id: Long, item: Media) {
        selectedItems[id] = item
        count.value = selectedItems.size
    }

    private fun deselect(id: Long) {
        selectedItems.remove(id)
        count.value = selectedItems.size
    }

    fun toggle(id: Long, item: Media) {
        if (isSelected(id)) {
            deselect(id)
        } else {
            select(id, item)
        }
    }

    fun isSelected(id: Long): Boolean {
        return selectedItems.containsKey(id)
    }

    fun toList(): List<Media> {
        return selectedItems.values.toList()
    }

    fun isEmpty() = selectedItems.isEmpty()

    fun getCount(): LiveData<Int> = count

}