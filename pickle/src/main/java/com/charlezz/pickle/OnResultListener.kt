package com.charlezz.pickle

import com.charlezz.pickle.data.entity.PickleError

interface OnResultListener {
    fun onSuccess(result: PickleResult)
    fun onError(pickleError: PickleError)
}