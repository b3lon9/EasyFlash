package com.b3lon9.easyflash.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.b3lon9.easyflash.MainActivity
import com.b3lon9.easyflash.R
import com.b3lon9.easyflash.constant.Constant

class MainViewModel(private val context: Context, private val pref: SharedPreferences) :  ViewModel() {
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val audioManager:AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val editor: Editor = pref.edit()

    val isToggleChecked      = MutableLiveData(false)
    val isSwitchImmediate    = MutableLiveData(false)
    val isSwitchScreen       = MutableLiveData(false)
    val isSwitchLock         = MutableLiveData(false)

    var firstY = -1
    var curLevel = Constant.Level.FLASH_LEVEL1
    var direct = Constant.Direct.NORMAL

    var flagChange = false
    var flagLevel1 = false
    var flagLevel2 = false
    var flagLevel3 = false
    var flagLevel4 = false
    var flagLevel5 = false

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

    fun clearFlag() {
        flagChange = false
        flagLevel1 = false
        flagLevel2 = false
        flagLevel3 = false
        flagLevel4 = false
        flagLevel5 = false
    }

    fun onCheckedChanged(isChecked:Boolean) {
        beepOn()
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
        try {
            cameraManager.apply {
                val cameraId = cameraIdList.first()
                setTorchMode(cameraId, true)
            }
        } catch (e:Exception) {
            Toast.makeText(context, context.resources.getString(R.string.torch_toast), Toast.LENGTH_SHORT).show()
            return
        }

        if (isSwitchScreen.value == true) {
            (context as MainActivity).apply{
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                // max bright
                val layoutParams = window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                window.attributes = layoutParams
            }
        }
    }

    private fun flashOff() {
        // API23(Marshmallow) check
        try {
            cameraManager.apply {
                val cameraId = cameraIdList.first()
                setTorchMode(cameraId, false)
            }
        } catch (e:Exception) {
            Toast.makeText(context, context.resources.getString(R.string.torch_toast), Toast.LENGTH_SHORT).show()
            return
        }

        if (isSwitchScreen.value == true) {
            (context as MainActivity).apply{
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                // max bright
                val layoutParams = window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = layoutParams
            }
        }
    }

    private fun beepOn() {
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
    }
}