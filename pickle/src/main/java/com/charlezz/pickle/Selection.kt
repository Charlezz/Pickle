package com.charlezz.pickle

import android.os.Parcelable
import com.charlezz.pickle.data.entity.Media
import kotlinx.parcelize.Parcelize

@Parcelize
class Selection(private val selectedItems: LinkedHashMap<Long, Media> = LinkedHashMap()) :Parcelable {

    private fun select(id: Long, item: Media) {
        selectedItems[id] = item
    }

    private fun deselect(id: Long) {
        selectedItems.remove(id)
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


}