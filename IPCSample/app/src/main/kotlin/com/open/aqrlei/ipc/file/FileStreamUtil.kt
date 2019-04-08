package com.open.aqrlei.ipc.file

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*

/**
 * @author aqrlei on 2019/4/3
 */

object FileStreamUtil {

    fun getCacheFile(context: Context, uniqueName: String = "ipc_test.text"): File? {
        val cachePath =
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable())
                    context.externalCacheDir?.path
                else
                    context.cacheDir.path

        return File("$cachePath${File.separator}$uniqueName")
    }

    fun writeChar(file: File, content: String = "Hello IPC File"): Boolean {
        return try {
            BufferedWriter(FileWriter(file, true)).use { bWriter ->
                bWriter.write((content))
                bWriter.flush()
                true
            }
        } catch (e: Exception) {
            Log.d("IOTest", e.message ?: "UnKnowError")
            false
        }
    }

    fun getObjectFile(context: Context) = getCacheFile(context, "object_test")
    fun writeObject(file: File?, user: User): Boolean {
        return try {
            ObjectOutputStream(FileOutputStream(file)).use {
                it.writeObject(user)
                it.flush()
                true
            }

        } catch (e: Exception) {
            false
        }
    }

    fun readObject(file: File?, action: (User?) -> Unit) {
        if (file == null) {
            return
        }
        try {
            ObjectInputStream(FileInputStream(file)).use {
                val user = it.readObject() as? User
                action(user)
            }
        } catch (e: Exception) {
            action(null)
        }
    }

    fun readChar(file: File, action: (String) -> Unit) {
        try {
            BufferedReader(FileReader(file)).use { bReader ->
                val str = StringBuffer()
                var buffer = -1
                while (buffer.let {
                            buffer = bReader.read()
                            buffer
                        } != -1) {
                    str.append(buffer.toChar().toString())
                }
                action(str.toString())
            }
        } catch (e: Exception) {
            action(e.message ?: "UnKnowError")
        }
    }
}