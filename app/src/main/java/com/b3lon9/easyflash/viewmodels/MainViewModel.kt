package com.b3lon9.easyflash.viewmodels

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(context: Context) : ViewModel() {
    private lateinit var cameraManager:CameraManager
    val isToggleChecked = MutableLiveData(false)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        }
    }


    private val _myLiveData = MutableLiveData<String>()

    val myLiveData: LiveData<String>
        get() = _myLiveData

    fun updateMyLiveData(newValue: String) {
        _myLiveData.value = newValue
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onCheckedChanged(isChecked:Boolean) {
        isToggleChecked.value = isChecked
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isChecked) {
                    val cameraId = cameraManager.cameraIdList.get(0)
                    cameraManager.setTorchMode(cameraId, true)
                } else {
                    val cameraId = cameraManager.cameraIdList.get(0)
                    cameraManager.setTorchMode(cameraId, false)
                }
            }

        } finally {

        }
    }
}