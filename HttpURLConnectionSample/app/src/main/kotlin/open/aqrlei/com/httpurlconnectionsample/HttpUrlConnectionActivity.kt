package open.aqrlei.com.httpurlconnectionsample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.telephony.PhoneNumberUtils
import kotlinx.android.synthetic.main.activity_http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author  aqrLei on 2018/7/30
 */
fun queryActivities(context: Context, intent: Intent): Boolean {
    return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY) != null
}

class HttpUrlConnectionActivity : AppCompatActivity() {
    companion object {
        fun open(context: Context) {
            val intent = Intent(context, HttpUrlConnectionActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http)
        initView()

    }

    private fun initView() {
        searchTelInfoV.onActionViewExpanded()
        searchTelInfoV.setIconifiedByDefault(false)
        searchTelInfoV.queryHint ="输入手机号码,提交后搜索"
        searchTelInfoV.isSubmitButtonEnabled= true
        searchTelInfoV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (PhoneNumberUtils.isGlobalPhoneNumber(query)) {
                    val job = GlobalScope.async {
                        connectionResponse(query!!)
                    }
                    GlobalScope.launch(UI) {
                        contentTv.text = job.await()
                    }
                }
                return true
            }
        })
    }

    private fun connectionResponse(tel:String): String {
        val connection = getHttpUrlConnection(tel)
        val inputStream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream, "gbk"))
        return reader.readText()
    }

    private fun getHttpUrlConnection(tel:String): HttpURLConnection {
        val mUrl = URL("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=$tel")
        val mHttpURLConnection = mUrl.openConnection() as HttpURLConnection
        mHttpURLConnection.apply {
            connectTimeout = 15000    //设置链接超时时间
            readTimeout = 15000   //设置读取超时时间
            requestMethod = "GET"     //设置请求参数
            setRequestProperty("Connection", "Keep-Alive")  //添加Header
            doInput = true //接收输入流
            doOutput = true  //传递参数时需要开启
        }
        return mHttpURLConnection
    }


}