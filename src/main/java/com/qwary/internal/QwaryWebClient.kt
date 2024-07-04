package com.qwary.internal

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout


object QwaryWebClient : WebViewClient() {

    private val QW_TAG = javaClass.simpleName

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        (view as? QwaryWebView)?.let { it.executeJavascript(it.qwSettings) }
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        return if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            view.context.startActivity(intent)
            true
        } else {
            false
        }
    }

//    override fun onLoadResource(view: WebView?, url: String?) {
//        super.onLoadResource(view, url)
//        Log.d(QW_TAG, "onLoadResource() called with: url = $url")
//        (view as? QwaryWebView)?.let {
//            val contentHeight = (it.contentHeight * it.scale).toInt()
//            Log.d(QW_TAG, "onPageFinished() called contentHeight ${it.contentHeight} ${it.scale}")
//            // Set WebView height
//            it.layoutParams = RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
//            )
//        }
//    }

    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?) = true
}