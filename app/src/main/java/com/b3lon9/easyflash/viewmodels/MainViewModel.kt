package com.b3lon9.easyflash.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.b3lon9.easyflash.MainActivity
import com.b3lon9.easyflash.R
import com.b3lon9.easyflash.constant.Constant
import com.b3lon9.easyflash.constant.Constant.Level
import com.b3lon9.easyflash.constant.Constant.Theme
import com.b3lon9.easyflash.constant.Constant.Screen
import com.b3lon9.easyflash.converter.ResourceIdConverter
import com.b3lon9.easyflash.views.SettingDialog
import com.b3lon9.nlog.NLog

@SuppressLint("StaticFieldLeak")
class MainViewModel(private val context: Context, private val pref: SharedPreferences) :  ViewModel() {
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val audioManager:AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val editor: Editor = pref.edit()

    private lateinit var cameraId:String
    var isAPI33Higher:Boolean = false

    val isToggleChecked      = MutableLiveData(false)
    val isSwitchImmediate    = MutableLiveData(false)
    val isSwitchScreen       = MutableLiveData(false)
    val isSwitchLock         = MutableLiveData(false)

    var isKeepLightChecked:Boolean = pref.getBoolean(context.resources.getString(R.string.setting_keep), true)
    var themeColor:Int = pref.getInt("themeColor", Theme.GREEN.ordinal)
    var screenColor:Int = pref.getInt("screenColor", Screen.WHITE.ordinal)

    val toggleScreenSelector   = MutableLiveData<Drawable>()
    val toggleRipple           = MutableLiveData<Drawable>()
    val baseLineFlashLightOn   = MutableLiveData<Drawable>()
    val baseLineFlashLightOff  = MutableLiveData<Drawable>()
    val baseLineMenu           = MutableLiveData<Drawable>()
    val baseLineClose          = MutableLiveData<Drawable>()
    val baseLineLock           = MutableLiveData<Drawable>()
    val buttonRippleEffect     = MutableLiveData<Drawable>()
    val switchImmediate        = MutableLiveData<Drawable>()
    val switchScreen           = MutableLiveData<Drawable>()
    val switchLock             = MutableLiveData<Drawable>()
    val switchTextColor        = MutableLiveData<Int>()


    var firstY = -1
    var curLevel = MutableLiveData(pref.getInt(context.getString(R.string.torch_level), Level.FLASH_LEVEL1))
    var direct = Constant.Direct.NORMAL

    var flagChange = false
    var flagLevel1 = false
    var flagLevel2 = false
    var flagLevel3 = false
    var flagLevel4 = false
    var flagLevel5 = false

    private val settingDialog:SettingDialog by lazy {
        SettingDialog(context, this).apply {
            setDataListener(settingListener)
        }
    }

    init {
        isSwitchImmediate.value = pref.getBoolean(context.getString(R.string.switch_immediate), false)
        isSwitchScreen.value = pref.getBoolean(context.getString(R.string.switch_screen), false)
        isSwitchLock.value = pref.getBoolean(context.getString(R.string.switch_lock), false)

        toggleScreenSelector.postValue(ContextCompat.getDrawable(context, pref.getInt(::toggleScreenSelector.name, R.drawable.toggle_screen_selector)))
        toggleRipple.postValue(ContextCompat.getDrawable(context, pref.getInt(::toggleRipple.name, R.drawable.toggle_ripple)))
        baseLineFlashLightOn.postValue(ContextCompat.getDrawable(context, pref.getInt(::baseLineFlashLightOn.name, R.drawable.baseline_flashlight_on_24)))
        baseLineFlashLightOff.postValue(ContextCompat.getDrawable(context, pref.getInt(::baseLineFlashLightOff.name, R.drawable.baseline_flashlight_off_24)))

        baseLineMenu.postValue(ContextCompat.getDrawable(context, pref.getInt(::baseLineMenu.name, R.drawable.menu_selector)))
        baseLineClose.postValue(ContextCompat.getDrawable(context, pref.getInt(::baseLineClose.name, R.drawable.close_selector)))
        baseLineLock.postValue(ContextCompat.getDrawable(context, pref.getInt(::baseLineLock.name, R.drawable.baseline_lock_24)))
        buttonRippleEffect.postValue(ContextCompat.getDrawable(context, pref.getInt(::buttonRippleEffect.name, R.drawable.button_ripple_effect)))

        switchImmediate.postValue(ContextCompat.getDrawable(context, pref.getInt(::switchImmediate.name, R.color.main_level1)))
        switchScreen.postValue(ContextCompat.getDrawable(context, pref.getInt(::switchScreen.name, R.color.main_level2)))
        switchLock.postValue(ContextCompat.getDrawable(context, pref.getInt(::switchLock.name, R.color.main_level3)))
        switchTextColor.value = pref.getInt(::switchTextColor.name, R.color.main_level4)


        try {
            val cameraIds = cameraManager.cameraIdList

            for (id in cameraIds) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    break
                }
            }
        } finally {

        }

        isAPI33Higher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    override fun onCleared() {
        super.onCleared()
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

    fun onSettingDialog() {
        settingDialog.show()
    }

    fun onCheckedChanged(num:Int) {
        NLog.d("... num : $num")
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
                // val cameraId = cameraIdList.first()
                setTorchMode(cameraId, true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) curLevel?.value?.let {
                    turnOnTorchWithStrengthLevel(cameraId,
                        it
                    )
                }
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
                // val cameraId = cameraIdList.first()
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

    fun flashLevel(level:Int) {
        try {
            if (isToggleChecked.value == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cameraManager.apply {
                    turnOnTorchWithStrengthLevel(cameraId, level)
                }
            }
        } finally {
            val key = context.resources.getString(R.string.torch_level)
            editor.apply {
                putInt(key, level)
                apply()
            }
        }
    }

    fun lockGuide() {
        Toast.makeText(context, context.resources.getString(R.string.toggle_text_guide_lock), Toast.LENGTH_SHORT).show()
    }

    private fun beepOn() {
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK)
    }

    /* setting contents listener */
    private val settingListener = object :SettingDialog.SettingDataListener {
        override fun onThemeColor(themeColor: Theme) {
            // init theme color
            this@MainViewModel.themeColor = themeColor.ordinal
            editor.apply {
                putInt("themeColor", themeColor.ordinal)
                apply()
            }

            // base Layout : toggleView
            when(themeColor) {
                Theme.BEIGE -> R.drawable.toggle_screen_selector_beige
                Theme.NAVY -> R.drawable.toggle_screen_selector_navy
                Theme.PINK -> R.drawable.toggle_screen_selector_pink
                else -> R.drawable.toggle_screen_selector
            }.let { id ->
                toggleScreenSelector.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::toggleScreenSelector.name, id)
                    apply()
                }
            }

            // base Layout : toggleView Ripple(default)
            when(themeColor) {
                Theme.BEIGE -> R.drawable.toggle_ripple_beige
                Theme.NAVY -> R.drawable.toggle_ripple_navy
                Theme.PINK -> R.drawable.toggle_ripple_pink
                else -> R.drawable.toggle_ripple
            }.let { id ->
                toggleRipple.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::toggleRipple.name, id)
                    apply()
                }
            }

            // flashLight ON Image
            when(themeColor) {
                Theme.BEIGE -> R.drawable.baseline_flashlight_on_24_beige
                Theme.NAVY -> R.drawable.baseline_flashlight_on_24_navy
                Theme.PINK -> R.drawable.baseline_flashlight_on_24_pink
                else -> R.drawable.baseline_flashlight_on_24
            }.let { id ->
                baseLineFlashLightOn.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::baseLineFlashLightOn.name, id)
                    apply()
                }
            }

            // flashLight OFF Image
            when(themeColor) {
                Theme.BEIGE -> R.drawable.baseline_flashlight_off_24_beige
                Theme.NAVY -> R.drawable.baseline_flashlight_off_24_navy
                Theme.PINK -> R.drawable.baseline_flashlight_off_24_pink
                else -> R.drawable.baseline_flashlight_off_24
            }.let { id ->
                baseLineFlashLightOff.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::baseLineFlashLightOff.name, id)
                    apply()
                }
            }

            // menu
            when(themeColor) {
                Theme.BEIGE -> R.drawable.menu_selector_beige
                Theme.NAVY -> R.drawable.menu_selector_navy
                Theme.PINK -> R.drawable.menu_selector_pink
                else -> R.drawable.menu_selector
            }.let { id ->
                baseLineMenu.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::baseLineMenu.name, id)
                    apply()
                }
            }

            // close
            when(themeColor) {
                Theme.BEIGE -> R.drawable.close_selector_beige
                Theme.NAVY -> R.drawable.close_selector_navy
                Theme.PINK -> R.drawable.close_selector_pink
                else -> R.drawable.close_selector
            }.let { id ->
                baseLineClose.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::baseLineClose.name, id)
                    apply()
                }
            }

            // lock
            when(themeColor) {
                Theme.BEIGE -> R.drawable.baseline_lock_24_beige
                Theme.NAVY -> R.drawable.baseline_lock_24_navy
                Theme.PINK -> R.drawable.baseline_lock_24_pink
                else -> R.drawable.baseline_lock_24
            }.let { id ->
                baseLineLock.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::baseLineLock.name, id)
                    apply()
                }
            }

            // button
            when(themeColor) {
                Theme.BEIGE -> R.drawable.button_ripple_effect_beige
                Theme.NAVY -> R.drawable.button_ripple_effect_navy
                Theme.PINK -> R.drawable.button_ripple_effect_pink
                else -> R.drawable.button_ripple_effect
            }.let { id ->
                buttonRippleEffect.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::buttonRippleEffect.name, id)
                    apply()
                }
            }


            // bottom menu
            when(themeColor) {
                Theme.BEIGE -> R.color.main_level1_beige
                Theme.NAVY -> R.color.main_level1_navy
                Theme.PINK -> R.color.main_level1_pink
                else -> R.color.main_level1
            }.let { id ->
                switchImmediate.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::switchImmediate.name, id)
                    apply()
                }
            }

            when(themeColor) {
                Theme.BEIGE -> R.color.main_level2_beige
                Theme.NAVY -> R.color.main_level2_navy
                Theme.PINK -> R.color.main_level2_pink
                else -> R.color.main_level2
            }.let { id ->
                switchScreen.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::switchScreen.name, id)
                    apply()
                }
            }

            when(themeColor) {
                Theme.BEIGE -> R.color.main_level3_beige
                Theme.NAVY -> R.color.main_level3_navy
                Theme.PINK -> R.color.main_level3_pink
                else -> R.color.main_level3
            }.let { id ->
                switchLock.postValue(ContextCompat.getDrawable(context, id))
                editor.apply {
                    putInt(::switchLock.name, id)
                    apply()
                }
            }


            // text Color
            when(themeColor) {
                Theme.BEIGE -> R.color.main_level4_beige
                Theme.NAVY -> R.color.main_level4_navy
                Theme.PINK -> R.color.main_level4_pink
                else -> R.color.main_level4
            }.let { id ->
                switchTextColor.value = id
                editor.apply {
                    putInt(::switchTextColor.name, id)
                    apply()
                }
            }
        }

        override fun onScreenColor(screenColor: Screen) {
            editor.apply {
                putInt("screenColor", screenColor.ordinal)
                apply()
            }
        }

        override fun onKeepLight(keep: Boolean) {
            isKeepLightChecked = keep

            editor.apply {
                putBoolean(context.resources.getString(R.string.setting_keep), keep)
                apply()
            }
        }
    }
}