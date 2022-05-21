package com.SeeChenAndLeYe.Pi

import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.net.toUri

class about_Overview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_overview)

        val buttonBack: ImageButton = findViewById(R.id.imageButton_backButton)
        buttonBack.setOnClickListener {
            finish()
        }

        loadWebView()

        val overview_Webview: WebView = findViewById(R.id.webView)


        val buttonRefresh: ImageButton = findViewById(R.id.imageButton_Refresh)
        buttonRefresh.setOnClickListener {
            loadWebView()
        }
    }

    private fun loadWebView(){
        val overview_Webview: WebView = findViewById(R.id.webView)
        val webClient = object: WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100){
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                } else {
                    findViewById<ProgressBar>(R.id.progressBar).progress = newProgress
                }
            }
        }

        overview_Webview.webChromeClient = webClient
        var webSetting = overview_Webview!!.settings
        webSetting.javaScriptEnabled = true
        webSetting.setAppCacheEnabled(true)
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = false
        webSetting.displayZoomControls = false
        webSetting.blockNetworkImage = false
        webSetting.loadsImagesAutomatically = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSetting.safeBrowsingEnabled = true
        }

        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.domStorageEnabled = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.useWideViewPort = true
        webSetting.loadWithOverviewMode = true
        webSetting.allowFileAccess = true
        webSetting.setGeolocationEnabled(true)
        overview_Webview?.fitsSystemWindows = true
        overview_Webview?.setLayerType(View.LAYER_TYPE_HARDWARE,null)

        var websiteUri: String = ("https://seechen.github.io/androidProject/aboutApp/aboutApp_" + systemLanguage + ".html")
        overview_Webview.loadUrl(websiteUri)


    }

}