package com.aqrlei.sample.viewsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_layout.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_layout)
        Log.d("Thread", "UI - ${Thread.currentThread()}")


        testTv.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {

                Log.d("Thread", "work - ${Thread.currentThread()}:root${testTv}")
                testTv.text = "set by new Thread"
                Thread.sleep(1000)
                testTv.requestLayout()
            }
        }

    }


}
