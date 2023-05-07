package com.b3lon9.easyflash.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.b3lon9.easyflash.R
import com.b3lon9.nlog.NLog

class MainViewModel(private val context: Context, private val pref: SharedPreferences) :  ViewModel() {
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val editor: Editor = pref.edit()

    val isToggleChecked      = MutableLiveData(false)
    val isSwitchImmediate    = MutableLiveData(false)
    val isSwitchScreen       = MutableLiveData(false)
    val isSwitchLock         = MutableLiveData(false)

    init {
        isSwitchImmediate.value = pref.getBoolean(context.getString(R.string.switch_immediate), false)
        isSwitchScreen.value = pref.getBoolean(context.getString(R.string.switch_screen), false)
        isSwitchLock.value = pref.getBoolean(context.getString(R.string.switch_lock), false)
    }

    fun resume() {
        if (isSwitchImmediate.value == true) {
            isToggleChecked.value = true
            flashOn()
        }
    }

    fun pause() {
        if (isToggleChecked.value == true) {
            isToggleChecked.value = false
            flashOff()
        }
    }

    fun onCheckedChanged(isChecked: Boolean) {
        isToggleChecked.value = isChecked
        try {
            if (isChecked) flashOn() else flashOff()
        } finally {

        }
    }

    fun onSwitchToggleChanged(view:View, isChecked: Boolean) {
        lateinit var key:String

        when(view.id) {
            R.id.switch_immediate -> {
                key = context.resources.getString(R.string.switch_immediate)
                isSwitchImmediate.value = isChecked
            }
            R.id.switch_screen -> {
                key = context.resources.getString(R.string.switch_screen)
                isSwitchScreen.value = isChecked
            }
            R.id.switch_lock -> {
                key = context.resources.getString(R.string.switch_lock)
                isSwitchLock.value = isChecked
            }
        }

        editor.apply {
            putBoolean(key, isChecked)
            apply()
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