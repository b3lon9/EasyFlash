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

        vm.curLevel.observe(this) {
            binding.brightImg.setImageLevel(it)
            vm.flashLevel(it)
        }

        binding.closeBtn.setOnClickListener{
            finish()
        }

        if (BuildConfig.DEBUG) {
            binding.banner.visibility = View.GONE
        }
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

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { view, event ->
        val offsetLevel = 1
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                vm.firstY = (event.y).toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                (vm.firstY - (event.y).toInt()).let {
                    if (it > 0) {
                        // up scroll
                        if (vm.direct == Constant.Direct.UP) {
                            when(abs(it)) {
                                in 0..9 -> {}
                                in 10..100 -> {
                                    if (!vm.flagLevel1) {
                                        vm.flagLevel1 = true
                                        vm.flagChange = true
                                    }
                                }
                                in 101..200 -> {
                                    if (!vm.flagLevel2) {
                                        vm.flagLevel2 = true
                                        vm.flagChange = true
                                    }
                                }
                                in 201..300 -> {
                                    if (!vm.flagLevel3) {
                                        vm.flagLevel3 = true
                                        vm.flagChange = true
                                    }
                                }
                                in 301..400 -> {
                                    if (!vm.flagLevel4) {
                                        vm.flagLevel4 = true
                                        vm.flagChange = true
                                    }
                                }
                                else -> {
                                    if (!vm.flagLevel5) {
                                        vm.flagLevel5 = true
                                        vm.flagChange = true
                                    }
                                }
                            }

                            if (vm.flagChange && vm.curLevel.value in 1..4) {
                                vm.curLevel.value = vm.curLevel.value!!.plus(offsetLevel)
                                vm.flagChange = false
                                // binding.brightImg.setImageLevel(vm.curLevel)

                                //vm.flashLevel(vm.curLevel)
                            }

                        } else {
                            // before Direct.DOWN
                            vm.clearFlag()
                        }

                        vm.direct = Constant.Direct.UP
                    } else {
                        // down scroll
                        if (vm.direct == Constant.Direct.DOWN) {
                            when(abs(it)) {
                                in 0..9 -> {}
                                in 10..100 -> {
                                    if (!vm.flagLevel1) {
                                        vm.flagLevel1 = true
                                        vm.flagChange = true
                                    }
                                }
                                in 101..200 -> {
                                    if (!vm.flagLevel2) {
                                        vm.flagLevel2 = true
                                        vm.flagChange = true
                                    }
                                }
                                in 201..300 -> {
                                    if (!vm.flagLevel3) {
                                        vm.flagLevel3 = true
                                        vm.flagChange = true
                                    }
                                }
                                in 301..400 -> {
                                    if (!vm.flagLevel4) {
                                        vm.flagLevel4 = true
                                        vm.flagChange = true
                                    }
                                }
                                else -> {
                                    if (!vm.flagLevel5) {
                                        vm.flagLevel5 = true
                                        vm.flagChange = true
                                    }
                                }
                            }

                            if (vm.flagChange && vm.curLevel.value in 2 .. 5) {
                                vm.curLevel.value = vm.curLevel.value!!.minus(offsetLevel)
                                vm.flagChange = false
                            }
                        } else {
                            // before Direct.UP
                            vm.clearFlag()
                        }

                        vm.direct = Constant.Direct.DOWN
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (abs(vm.firstY - (event.y).toInt()) < 10) vm.onCheckedChanged(!view!!.isSelected)
                vm.direct = Constant.Direct.NORMAL
                vm.clearFlag()
            }
        } // when end
        true
    }
}