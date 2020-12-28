package com.charlezz.pickle.data.repository

import androidx.paging.PositionalDataSource
import com.charlezz.pickle.data.entity.Media

class PickleDataSource:PositionalDataSource<Media>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Media>) {
    }


    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Media>) {
    }

}