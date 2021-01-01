package com.charlezz.pickle.util.bindingadapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter

@BindingAdapter("constraintGuide_begin")
fun setConstraintGuideBegin(view: Guideline, margin: Int?) {
    val constraintLayout: ConstraintLayout = view.parent as ConstraintLayout
    val constraintSet = ConstraintSet()

    margin?.let {
        constraintSet.clone(constraintLayout)
        constraintSet.setGuidelineBegin(view.id, it)
        constraintSet.applyTo(constraintLayout)
    }

}