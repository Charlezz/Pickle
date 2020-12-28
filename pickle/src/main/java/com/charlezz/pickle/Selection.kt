package com.charlezz.pickle

import javax.inject.Inject

class Selection<T> @Inject constructor(

) {

    private val selectedItems = HashMap<Long, T>()

    fun select(id: Long, item: T) {
        selectedItems[id] = item
    }

    fun deselect(id: Long) {
        selectedItems.remove(id)
    }

    fun toggle(id: Long, item: T) {
        if (isSelected(id)) {
            deselect(id)
        } else {
            select(id, item)
        }
    }

    fun isSelected(id: Long): Boolean {
        return selectedItems.containsKey(id)
    }

    fun toList(): List<T> {
        return selectedItems.values.toList()
    }


}