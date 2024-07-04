package com.qwary.internal

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.qwary.R

@SuppressLint("ViewConstructor", "SetJavaScriptEnabled")
class QwaryWebView(context: Context, appId: String, callback: Callback) :
    WebView(context.applicationContext) {

    private val QW_TAG = javaClass.simpleName
    val qwSettings: String
    private val qwJavascriptInterface = object {

        @JavascriptInterface
        fun qwMobileSdkReady(data: String) {
            callback.qwMobileSdkReady(data)
        }

        @JavascriptInterface
        fun qwSurveyHeight(data: String) {
            callback.qwSurveyHeight(data)
        }

        @JavascriptInterface
        fun qwShow(data: String, isBanner: Boolean) {
            callback.qwShow(data, isBanner)
        }

        @JavascriptInterface
        fun qwEventTracking(data: String) {
            callback.qwEventTracking(data)
        }

        @JavascriptInterface
        fun qwMobileLogout(data: String) {
            callback.qwMobileLogout(data)
        }

        @JavascriptInterface
        fun qwDismissSurvey(data: String) {
            callback.qwDismissSurvey(data)
        }

    }

    companion object {
        private const val QW_SDK_HOOK = "qwSdkHook"
    }

    init {
        val displayMetrics = this.resources.displayMetrics
        // on below line we are getting metrics
        // for display using window manager.
        // on below line we are getting height
        // and width using display metrics.
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels / 3
        Log.d(QW_TAG, "null() called width $width")
        Log.d(QW_TAG, "null() called height $height")
        layoutParams = LinearLayout.LayoutParams(
            width, height
        )
//        layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
//        )
        this.qwSettings = appId
        clearCache(true)
//        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
//        measure(100, 100)
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        addJavascriptInterface(qwJavascriptInterface, QW_SDK_HOOK)
        webViewClient = QwaryWebClient
        loadUrl("file:///android_asset/render.html")
        // Satinder Change for remove background
        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
    }

    interface Callback {
        fun qwMobileSdkReady(data: String)
        fun qwSurveyHeight(data: String)
        fun qwShow(data: String, isBanner: Boolean)
        fun qwEventTracking(data: String)
        fun qwMobileLogout(data: String)
        fun qwDismissSurvey(data: String)
    }

    fun executeJavascript(javascript: String, callback: ((String) -> Void)? = null) {
        Log.d(QW_TAG, "executeJavascript: $javascript")
        evaluateJavascript(javascript) {
//            Log.d(QW_TAG, "executeJavascript() called callback $it")
            callback?.invoke(it)
        }
    }

}