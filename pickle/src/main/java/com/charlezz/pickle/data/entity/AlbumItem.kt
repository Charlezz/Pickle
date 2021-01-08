package com.charlezz.pickle.data.entity

import android.content.ContentUris
import android.net.Uri
import androidx.databinding.BaseObservable
import com.charlezz.pickle.BR
import com.charlezz.pickle.R
import com.charlezz.pickle.util.recyclerview.DataBindingAware

class AlbumItem constructor(
    val album: Album,
    val listener: OnItemClickListener
) : BaseObservable(), DataBindingAware {
    fun getRecentMediaUri(): Uri {
        return ContentUris.withAppendedId(album.contentUri, album.recentMediaId)
    }

    override fun getLayoutResId(): Int = R.layout.view_pickle_album

    override fun getBindingResId(): Int = BR.item

    interface OnItemClickListener {
        fun onFolderClick(item: AlbumItem)
    }

}