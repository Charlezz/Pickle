package com.charlezz.pickle

class SingleConfig internal constructor() : Config(singleMode = true) {
    companion object{
        val default = SingleConfig()
    }

}