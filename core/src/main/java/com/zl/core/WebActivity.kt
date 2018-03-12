package com.zl.core

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import com.zl.core.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/2/2 17:09.<br/>
 */
class WebActivity : BaseActivity() {

    companion object {
        private val TAG = WebActivity::class.java.simpleName
        private val JS = "js"
        var URL = "WebActivity_url"
        var TITLE = "WebActivity_Title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        initWebView()

        webView.loadUrl(intent.getStringExtra(URL))
    }

    private fun initWebView() {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    if (View.INVISIBLE == progressBar.getVisibility()) {
                        progressBar.visibility = View.VISIBLE
                    }
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.i(TAG, "[" + consoleMessage.messageLevel() + "] " + consoleMessage.message() + "(" + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + ")")
                return super.onConsoleMessage(consoleMessage)
            }
        }

        //启用支持js
        val setting = webView.settings
        //设置属性，执行javascript脚本
        setting.javaScriptEnabled = true
        // 允许访问文件  支持localStorage
        setting.domStorageEnabled = true
        //设置支持缩放
        setting.builtInZoomControls = false
        webView.addJavascriptInterface(this, JS)
        webView.webViewClient = object : WebViewClient() {

            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.i(TAG, "load url:" + url)
                view.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.proceed() // Ignore SSL certificate errors
            }
        }
    }

    fun closeClick(view: View){
        finish()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    @JavascriptInterface
    fun setTitle(title: String) {
        Log.i(TAG, "setTitle:" + title)
        runOnUiThread {
            titleText.text = title
        }
    }
}