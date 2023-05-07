package com.b3lon9.easyflash

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.b3lon9.easyflash.databinding.ActivityMainBinding
import com.b3lon9.easyflash.viewmodels.MainViewModel
import com.b3lon9.nlog.NLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = MainViewModel(this, getPreferences(Context.MODE_PRIVATE))    //ViewModelProvider(this, ViewModelFactory(this))[(MainViewModel::class.java)]

        admob()
    }

    override fun onResume() {
        super.onResume()
        binding.vm?.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.vm?.pause()
    }

    private fun admob() {
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
    }
}