package com.charlezz.pickle.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpaceDecoration constructor(
    var spanCount: Int = 0,
    var space: Int = 0,
    private val includeEdge: Boolean = false
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (spanCount == 0) {
            return
        }
        val position = parent.getChildAdapterPosition(view)
        if (position >= 0) {
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = space - column * space / spanCount
                outRect.right = (column + 1) * space / spanCount
                if (position < spanCount) {
                    outRect.top = space
                }
                outRect.bottom = space
            } else {
                outRect.left = column * space / spanCount
                outRect.right = space - (column + 1) * space / spanCount
                outRect.bottom = space
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }

    }

}