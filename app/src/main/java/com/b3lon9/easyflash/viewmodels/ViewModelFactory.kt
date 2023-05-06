package com.b3lon9.easyflash.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val param:Any) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val context:Context = param as Context
            MainViewModel(context) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}