package com.b3lon9.easyflash.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.b3lon9.easyflash.R

class MainViewModel(private val context: Context, private val pref: SharedPreferences) : ViewModel() {
    private lateinit var cameraManager:CameraManager
    private val editor:Editor = pref.edit()
    val isToggleChecked = MutableLiveData(false)
    val isSwitchImmediate = MutableLiveData(false)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        }
        isSwitchImmediate.value = pref.getBoolean(context.getString(R.string.switch_immediate), false)

        if (isSwitchImmediate.value == true) {

        }
    }

    fun onCheckedChanged(isChecked:Boolean) {
        isToggleChecked.value = isChecked
        try {
            if (isChecked) flashOn() else flashOff()
        } finally {
            editor.apply {
                putBoolean(context.resources.getString(R.string.switch_immediate), isChecked)
                apply()
            }
        }
    }

    private fun flashOn() {
        // API23(Marshmallow) check
        cameraManager.apply {
            val cameraId = cameraIdList.first()
            setTorchMode(cameraId, true)
        }
    }

    private fun flashOff() {
        // API23(Marshmallow) check
        cameraManager.apply {
            val cameraId = cameraIdList.first()
            setTorchMode(cameraId, false)
        }
    }
}