package com.aqrlei.sample.jnisample

/**
 * @author aqrlei on 2019/4/2
 */

class JniTest {
    companion object {
        init {
            System.loadLibrary("jni-test")
        }
    }
    external fun stringFromJNI(): String

    external fun setStringToJNI(value:String)
}