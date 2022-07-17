package com.ziyad.pencatatuang

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class totalViewModel: ViewModel() {
    var totalBulanIni = MutableLiveData<Int>()
    var totalAll = MutableLiveData<Int>()
    init {
        totalBulanIni.value=0
        totalAll.value=0
    }
    fun addBulanIni(x:Int){
        totalBulanIni.value= totalBulanIni.value?.plus(x)
    }
}