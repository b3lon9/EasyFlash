package com.b3lon9.easyflash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.b3lon9.easyflash.constant.Constant
import com.b3lon9.easyflash.databinding.ActivityMainBinding
import com.b3lon9.easyflash.viewmodels.MainViewModel
import com.b3lon9.easyflash.viewmodels.ViewModelFactory
import com.b3lon9.nlog.NLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var vm:MainViewModel

    private val context:Context = this


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        val factory = ViewModelFactory(context, getPreferences(Context.MODE_PRIVATE))
        vm = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding.vm = vm

        admob()

        firebaseRealtimeDatabase()

        binding.toggleView.setOnTouchListener(touchListener)
        vm.isToggleChecked.observe(this, Observer{
            binding.toggleView.isSelected = it
        })
    }

    override fun onResume() {
        super.onResume()
        vm.resume()
    }

    override fun onPause() {
        super.onPause()
        vm.pause()
    }

    private fun admob() {
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
    }

    private fun firebaseRealtimeDatabase() {
        val database = Firebase.database
        val myRef = database.getReference("version")

        myRef.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value as String

                if (BuildConfig.VERSION_NAME < value) {
                    val url = "https://play.google.com/store/apps/details?id=$packageName"

                    // google store update
                    try {
                        val builder = AlertDialog.Builder(context)
                            .setTitle("업데이트 알림")
                            .setMessage("최신 버전으로 업데이트 하시겠습니까?")
                            .setPositiveButton("확인") { _, _ ->
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }

                        builder.let {
                            val dialog = it.create()
                            dialog.show()
                        }

                    } finally {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                NLog.d("error msg : ${error.toException().message}")
            }
        })
    }

    var firstY = -1
    var curLevel = 1
    var direct = Constant.Direct.NORMAL

    var flagChange = false
    var flagLevel1 = false
    var flagLevel2 = false
    var flagLevel3 = false
    var flagLevel4 = false
    var flagLevel5 = false

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { view, event ->
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                firstY = (event.y).toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                (firstY - (event.y).toInt()).let {
                    if (it > 0) {
                        // up scroll
                        if (direct == Constant.Direct.UP) {
                            when(abs(it)) {
                                in 0..9 -> {}
                                in 10..100 -> {
                                    if (!flagLevel1) {
                                        flagLevel1 = true
                                        flagChange = true
                                    }
                                }
                                in 101..200 -> {
                                    if (!flagLevel2) {
                                        flagLevel2 = true
                                        flagChange = true
                                    }
                                }
                                in 201..300 -> {
                                    if (!flagLevel3) {
                                        flagLevel3 = true
                                        flagChange = true
                                    }
                                }
                                in 301..400 -> {
                                    if (!flagLevel4) {
                                        flagLevel4 = true
                                        flagChange = true
                                    }
                                }
                                else -> {
                                    if (!flagLevel5) {
                                        flagLevel5 = true
                                        flagChange = true
                                    }
                                }
                            }

                            if (flagChange && curLevel in 1..4) {
                                curLevel += 1
                                flagChange = false
                                binding.brightImg.setImageLevel(curLevel)
                            }

                        } else {
                            // before Direct.DOWN
                            clearFlag()
                        }

                        direct = Constant.Direct.UP
                    } else {
                        // down scroll
                        if (direct == Constant.Direct.DOWN) {
                            when(abs(it)) {
                                in 0..9 -> {}
                                in 10..100 -> {
                                    if (!flagLevel1) {
                                        flagLevel1 = true
                                        flagChange = true
                                    }
                                }
                                in 101..200 -> {
                                    if (!flagLevel2) {
                                        flagLevel2 = true
                                        flagChange = true
                                    }
                                }
                                in 201..300 -> {
                                    if (!flagLevel3) {
                                        flagLevel3 = true
                                        flagChange = true
                                    }
                                }
                                in 301..400 -> {
                                    if (!flagLevel4) {
                                        flagLevel4 = true
                                        flagChange = true
                                    }
                                }
                                else -> {
                                    if (!flagLevel5) {
                                        flagLevel5 = true
                                        flagChange = true
                                    }
                                }
                            }

                            if (flagChange && curLevel in 2 .. 5) {
                                curLevel -= 1
                                flagChange = false
                                binding.brightImg.setImageLevel(curLevel)
                            }
                        } else {
                            // before Direct.UP
                            clearFlag()
                        }

                        direct = Constant.Direct.DOWN
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (abs(firstY - (event.y).toInt()) < 10) vm.onCheckedChanged(!view!!.isSelected)
                direct = Constant.Direct.NORMAL
                clearFlag()
            }
        } // when end
        true
    }

    private fun clearFlag() {
        flagChange = false
        flagLevel1 = false
        flagLevel2 = false
        flagLevel3 = false
        flagLevel4 = false
        flagLevel5 = false
    }
}