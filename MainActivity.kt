package com.example.zemcd.paceconnection

import android.app.Activity
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var webView:WebView
    lateinit var progressBar:ProgressBar

    var close_warn = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar) as ProgressBar

        webView = findViewById(R.id.pace_web_webview) as WebView
        webView.setWebViewClient(object:WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                close_warn = 0
                view.loadUrl("${request.url}")
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        })

        webView.setWebChromeClient(object:WebChromeClient(){
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressBar.setProgress(progress)
            }
        })

        webView.settings.let {
            it.javaScriptEnabled = true
            @Suppress("DEPRECATION")
            it.setRenderPriority(WebSettings.RenderPriority.HIGH)
            it.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            it.setAppCacheEnabled(true)
            it.domStorageEnabled = true
            it.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            it.useWideViewPort = true
        }

        if (savedInstanceState==null){
            webView.loadUrl("http://paceconnection.us/")
        }

    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }else{
            when(close_warn){
                0 -> {
                    close_warn = 1
                    toast()
                }
                1 -> finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        close_warn = 0
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

}

fun Activity.toast(message:String = "Press back again to exit", length:Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, length).show()
}