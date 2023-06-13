package com.b3lon9.easyflash.views

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
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

class SettingDialog(private val context:Context, private val vm:MainViewModel) : Dialog(context), DialogInterface.OnDismissListener {
    private lateinit var binding:SettingDialogBinding
    private lateinit var listener:SettingDataListener

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

        setOnDismissListener(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }



    fun setDataListener(listener:SettingDataListener) {
        this.listener = listener
    }

    lateinit var themeColor:Theme
    lateinit var screenColor:Screen


    fun onThemeCheckedChanged(@IdRes radioButtonId:Int) {
        this.themeColor = when(radioButtonId) {
            R.id.theme_radio_beige -> Theme.BEIGE
            R.id.theme_radio_navy -> Theme.NAVY
            R.id.theme_radio_pink -> Theme.PINK
            else -> Theme.GREEN
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        NLog.d("onDismiss... themeColor : $themeColor")
        listener.onThemeColor(themeColor)
        /*if (vm.themeColor.value != themeColor) {
            listener.onThemeColor(themeColor)
        }*/
    }
}