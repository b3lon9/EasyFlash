package com.b3lon9.easyflash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val context:Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = MainViewModel(context, getPreferences(Context.MODE_PRIVATE))    //ViewModelProvider(this, ViewModelFactory(this))[(MainViewModel::class.java)]

        admob()

        firebaseRealtimeDatabase()
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
                            .setPositiveButton("확인") { d, i ->
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
}