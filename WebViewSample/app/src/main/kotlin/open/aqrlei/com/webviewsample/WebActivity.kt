package open.aqrlei.com.webviewsample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import kotlinx.android.synthetic.main.activity_web.*

/**
 * @author  aqrLei on 2018/7/20
 */

fun queryActivities(context: Context, intent: Intent): Boolean {
    return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY) != null
}

class WebActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        fun open(context: Context) {
            val intent = Intent(context, WebActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loadTv -> {
                callByLoadUrl()

            }
            R.id.evaluateTv -> {
                callByEvaluateJavascript()
            }
        }
    }

    private fun callByLoadUrl() {
        webView.loadUrl("javascript:callJsByLoad()")//此方法会使界面刷新
    }

    private fun callByEvaluateJavascript() {
        //不会使页面刷新，但是Android 4.4之后才能用
        webView.evaluateJavascript("javascript:callJsByEvaluate") {
            contentTv.text = "evaluate: $it"
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initWebView()
        loadTv.setOnClickListener(this)
        evaluateTv.setOnClickListener(this)

    }

    private fun initWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
            }
            addJavascriptInterface(JsInteract(), "Android")
            loadUrl("file:///android_asset/JsTest.html")
            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    contentTv.text = "loadUrl: ${message ?: ""}"
                    result?.confirm()
                    return true
                }

                override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    return super.onJsConfirm(view, url, message, result)
                }

                override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                    return super.onJsPrompt(view, url, message, defaultValue, result)
                }
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url?.startsWith("aqrlei") == true) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                    contentTv.text = url
                    return true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
//        webView.pauseTimers()
//        webView.resumeTimers()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        (webView.parent as ViewGroup).removeView(webView)
        webView.destroy()
    }

    inner class JsInteract {

        @JavascriptInterface
        fun callFromJs(msg: String) {
            runOnUiThread {
                contentTv.text = msg
            }
        }
    }
}