package com.charlezz.pickle.util.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpaceDecoration constructor(
    val spanCount: Int = 0,
    val mSpacing: Int = 0,
    val mIncludeEdge: Boolean = false
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (mIncludeEdge) {
            outRect.left = mSpacing - column * mSpacing / spanCount
            outRect.right = (column + 1) * mSpacing / spanCount
            if (position < spanCount) {
                outRect.top = mSpacing
            }
            outRect.bottom = mSpacing
        } else {
            outRect.left = column * mSpacing / spanCount
            outRect.right = mSpacing - (column + 1) * mSpacing / spanCount
            outRect.bottom = mSpacing
        }
    }

}