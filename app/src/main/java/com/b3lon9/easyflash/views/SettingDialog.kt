package com.b3lon9.easyflash.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.b3lon9.easyflash.R
import com.b3lon9.easyflash.databinding.SettingDialogBinding
import com.b3lon9.easyflash.models.SettingModel

class SettingDialog(private val context:Context) : Dialog(context) {
    private lateinit var binding:SettingDialogBinding
    private lateinit var listener:SettingDataListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_dialog, null, false)
        setContentView(binding.root)
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

    interface SettingDataListener {
        fun onThemeColor(data:SettingModel)
    }
}