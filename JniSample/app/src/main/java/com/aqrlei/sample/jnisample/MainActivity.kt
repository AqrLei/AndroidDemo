package com.aqrlei.sample.jnisample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jniTest = JniTest()
        // Example of a call to a native method
        sample_text.text = jniTest.stringFromJNI()

        setJniStringButton.setOnClickListener {
            jniTest.setStringToJNI("Hello From Kotlin")
        }

    }


}
