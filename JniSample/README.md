# JniSample

 - Kotlin代码  
 ````
  class JniTest {
    companion object {
        init {
            System.loadLibrary("jni-test")
        }
    }
    external fun stringFromJNI(): String

    external fun setStringToJNI(value:String)
}
 
 ````
 - 使用 **kotlinc** 将*.kt*源代码编译成*.class*文件, 使用**javah** 根据.class文件 生成 *.h*文件  
    [LINK_1](./app/src/main/cpp/com_aqrlei_sample_jnisample_JniTest.h)  
    [LINK_2](./app/src/main/cpp/com_aqrlei_sample_jnisample_JniTest_Companion.h)
 
 - 根据 .h 文件 编写C/C++代码  
    [LINK](./app/src/main/cpp/test.c)
    
 - 配置**CMakeLists.txt** 文件
  
   [LINK](./app/src/main/cpp/CMakeLists.txt)   