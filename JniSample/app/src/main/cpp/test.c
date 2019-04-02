//
// Created by AqrLei on 2019/4/2.
//
#include "com_aqrlei_sample_jnisample_JniTest.h"
#include "com_aqrlei_sample_jnisample_JniTest_Companion.h"
#include <stdlib.h>

JNIEXPORT jstring JNICALL Java_com_aqrlei_sample_jnisample_JniTest_stringFromJNI(JNIEnv *env, jobject this) {
    return (*env)->NewStringUTF(env, "Hello from c");
}

JNIEXPORT void JNICALL
Java_com_aqrlei_sample_jnisample_JniTest_setStringToJNI(JNIEnv *env, jobject this, jstring value) {
    printf("invoke set from c");
    char *str = (char *) (*env)->GetStringUTFChars(env, value, NULL);
    printf("%s/n", str);
    (*env)->ReleaseStringUTFChars(env, value, str);
}