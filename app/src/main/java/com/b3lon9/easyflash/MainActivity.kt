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
import com.b3lon9.easyflash.constant.Constant
import com.b3lon9.easyflash.databinding.ActivityMainBinding
import com.b3lon9.easyflash.viewmodels.MainViewModel
import com.b3lon9.nlog.NLog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.abs
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity(), View.OnTouchListener {
    private lateinit var binding:ActivityMainBinding
    private val context:Context = this
    private var firstPoint:Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = MainViewModel(context, getPreferences(Context.MODE_PRIVATE))    //ViewModelProvider(this, ViewModelFactory(this))[(MainViewModel::class.java)]

        admob()

        firebaseRealtimeDatabase()
        binding.toggleButton.setOnTouchListener(this)
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
                NLog.d("...error : ${error.toException().message}")
            }
        })
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                firstPoint = (event.y).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                abs(firstPoint - (event.y).toInt()).let {
                    val level = when(it) {
                        in 0.. 100 -> Constant.Level.FLASH_LEVEL1
                        in 101.. 200 -> Constant.Level.FLASH_LEVEL2
                        in 201.. 300 -> Constant.Level.FLASH_LEVEL3
                        in 301.. 400 -> Constant.Level.FLASH_LEVEL4
                        else -> Constant.Level.FLASH_LEVEL5
                    }

                    flashLevel(level, firstPoint < event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (abs(firstPoint - (event.y).toInt()) <= 10) {
                    binding.toggleButton.isChecked = !binding.vm!!.isToggleChecked.value!!
                }
            }
        }
        return true
    }
    private fun flashLevel(level:Int, up:Boolean) {

    }
}