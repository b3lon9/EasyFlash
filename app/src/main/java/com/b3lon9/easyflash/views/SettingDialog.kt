package com.b3lon9.easyflash.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import com.b3lon9.easyflash.R
import com.b3lon9.easyflash.constant.Constant.Theme
import com.b3lon9.easyflash.constant.Constant.Screen
import com.b3lon9.easyflash.databinding.SettingDialogBinding
import com.b3lon9.easyflash.viewmodels.MainViewModel
import com.b3lon9.nlog.NLog

class SettingDialog(private val context:Context, private val vm:MainViewModel) : Dialog(context) {
    private lateinit var binding:SettingDialogBinding
    private lateinit var listener:SettingDataListener

    private lateinit var themeColor:Theme
    private lateinit var screenColor:Screen

    interface SettingDataListener {
        fun onThemeColor(themeColor:Theme)
        fun onScreenColor(screenColor:Screen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_dialog, null, false)
        binding.dialog = this

        setContentView(binding.root)

        window?.setBackgroundDrawable(context.getDrawable(android.R.color.transparent))

        // setOnDismissListener(this)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return super.onCreatePanelView(featureId)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        binding.themeRadioGroup.check(when(Theme.values()[vm.themeColor]) {
            Theme.BEIGE -> R.id.theme_radio_beige
            Theme.NAVY -> R.id.theme_radio_navy
            Theme.PINK -> R.id.theme_radio_pink
            else -> R.id.theme_radio_green
        })

        binding.screenRadioGroup.check(when(Screen.values()[vm.screenColor]) {
            Screen.WHITEYELLOW -> R.id.screen_radio_white_yellow
            Screen.YELLOW -> R.id.screen_radio_yellow
            else -> R.id.screen_radio_white
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }



    fun setDataListener(listener:SettingDataListener) {
        this.listener = listener
    }

    /*fun onThemeCheckedChanged(@IdRes radioButtonId:Int) {
            android:onCheckedChanged="@{(radio, id) -> dialog.onThemeCheckedChanged(id)}"
    }*/

    fun onScreenCheckedChanged(@IdRes radioButtonId:Int) {
        this.screenColor = when(radioButtonId) {
            R.id.screen_radio_white_yellow -> Screen.WHITEYELLOW
            R.id.screen_radio_yellow -> Screen.YELLOW
            else -> Screen.WHITE
        }
    }

    fun onApply() {
        this.themeColor = when(binding.themeRadioGroup.checkedRadioButtonId) {
            R.id.theme_radio_beige -> Theme.BEIGE
            R.id.theme_radio_navy -> Theme.NAVY
            R.id.theme_radio_pink -> Theme.PINK
            else -> Theme.GREEN
        }

        listener.onThemeColor(themeColor)
        // listener.onScreenColor(screenColor)
        dismiss()
    }
    /*override fun onDismiss(dialog: DialogInterface?) {
        NLog.d("onDismiss... themeColor : $themeColor")
    }*/
}