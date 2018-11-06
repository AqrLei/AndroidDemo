package open.aqrlei.com.webviewsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scheme.*

/**
 * @author aqrlei on 2018/9/17
 */
class SchemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheme)
        intent?.data?.run {
            val stringBuilder = StringBuilder("")
            stringBuilder.append("url:${this}\n")
            val scheme = this.scheme
            stringBuilder.append("scheme:$scheme\n")
            val host = this.host
            stringBuilder.append("host:$host\n")
            val path = this.path
            stringBuilder.append("path:$path\n")

            val port = this.port
            stringBuilder.append("port:$port\n")
            val query = this.query
            stringBuilder.append("query:$query\n")
            val queryValue = this.getQueryParameter("testId")
            stringBuilder.append("queryValue:$queryValue\n")
            schemeTestTv.text = stringBuilder

        }
    }
}