package com.qwary.internal

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.qwary.QwaryInterface
import com.qwary.internal.ui.QwaryWebViewFragment
import java.util.LinkedList
import java.util.Queue

class QwaryWebController : QwaryInterface, Application.ActivityLifecycleCallbacks,
    QwaryWebView.Callback, ConnectivityManager.NetworkCallback() {

    private val QW_TAG = javaClass.simpleName
    private val QW_SURVEY_TAG = "QwarySurveyDialog"
    private var currentFragmentActivity: FragmentActivity? = null
    private var sdkReadyQueue: Queue<String> = LinkedList()
    private lateinit var qwWebView: QwaryWebView
    private var shouldDismissOnResume: Boolean = false
    private var shouldResetOnResume = false
    private val QW_RESET_DELAY_MS = 5_000L
    private var isSdkReady: Boolean = false
    private var qwSettings: String = ""
    private var hasConfigured: Boolean = false

    //    var viewHeight: String = ""
    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val resetWebView = Runnable {
        shouldResetOnResume = currentFragmentActivity?.let { context ->
            if (!isSdkReady) {
                Log.d(QW_TAG, "resetWebView() called")
                qwWebView =
                    QwaryWebView(context.applicationContext, getInItScript(qwSettings), this)
            }
            false
        } ?: true
    }

    override fun configure(context: Context, qwSettings: String) {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(this)
        if (hasConfigured) return
        hasConfigured = true
        isSdkReady = false
        this.qwSettings = qwSettings
        sdkReadyQueue.clear()
        onUiParallel {
            qwWebView = QwaryWebView(context.applicationContext, getInItScript(qwSettings), this)
        }
        setupListenersForNetworkStatus(context)
    }

    override fun presentSurvey(fragmentActivity: FragmentActivity, isBanner: Boolean) {
//        Log.d(QW_TAG, "presentSurvey() called")
        if (!isSdkReady) return
        onUiParallel {
            currentFragmentActivity = fragmentActivity
            // Only show a survey if none presented (mimics iOS ViewController modal behavior)
            if (activeSurvey == null) {
                if (fragmentActivity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    // Satinder Change comment this line of code for banner
//                    if (isBanner) {
//                        val fm = currentFragmentActivity?.supportFragmentManager
//                        val fragment = QwaryBannerViewFragment.newInstance(this)
//                        fm?.beginTransaction()?.add(fragment, QW_SURVEY_TAG)?.commit()
//                    } else {
                    QwaryWebViewFragment
                        .newInstance(this)
                        .showNow(fragmentActivity.supportFragmentManager, QW_SURVEY_TAG)
//                    }
                }
            }
        }
    }

    override fun dismissActiveSurvey() {}

    override fun addEvent(eventName: String) {
        sdkReadyQueue.add(getEventTrackScript(eventName))
//        executeJavascript(getEventTrackScript(eventName))
    }

    override fun logout() {
        executeJavascript(getLogoutScript())
    }

    // javascript helper
    private fun executeJavascript(javascript: String, callback: ((String) -> Void)? = null) {
        onUiParallel {
            qwWebView.executeJavascript(javascript, callback)
        }
    }

    // android
    private fun onUiParallel(function: () -> Unit) {
        handler.post {
            runCatching {
                function()
            }.onFailure(::logError)
        }
    }

    private fun logError(exception: Throwable) {
        // create a javascript error and send it
        executeJavascript("{ message: '${exception.message}', stack: ${exception.stackTrace} }")
    }

    //   QwaryWebView Callbacks
    override fun qwMobileSdkReady(data: String) {
        Log.d(QW_TAG, "qwMobileSdkReady() called $data")
        isSdkReady = true
        while (!sdkReadyQueue.isEmpty()) {
            sdkReadyQueue.poll()?.let(::executeJavascript)
        }
    }

    override fun qwSurveyHeight(data: String) {
        Log.d(QW_TAG, "qwSurveyHeight() called $data")
    }

    override fun qwShow(data: String, isBanner: Boolean) {
        Log.d(QW_TAG, "qwShow() $data $isBanner")
//        viewHeight = data
        currentFragmentActivity?.let { presentSurvey(it, isBanner) }
    }

    override fun qwEventTracking(data: String) {
        Log.d(QW_TAG, "qwEventTracking() called $data")
    }

    override fun qwMobileLogout(data: String) {
        Log.d(QW_TAG, "qwMobileLogout() called $data")
    }

    override fun qwDismissSurvey(data: String) {
        Log.d(QW_TAG, "qwDismissSurvey() called $data")
        //dismiss bottom sheet
        currentFragmentActivity?.let {
            if (it.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                activeSurvey?.dismiss()
            } else {
                shouldDismissOnResume = true
            }
        }
    }

    //   ActivityLifecycle Callbacks
    override fun onActivityResumed(activity: Activity) {
        currentFragmentActivity = activity as? FragmentActivity
        onUiParallel {
            Log.d(QW_TAG, "shouldDismissOnResume $shouldDismissOnResume")
            Log.d(QW_TAG, "shouldResetOnResume $shouldResetOnResume")
            if (shouldDismissOnResume) {
                activeSurvey?.dismiss()
                shouldDismissOnResume = false
            }
            if (shouldResetOnResume) {
                resetWebView.run()
                shouldResetOnResume = false
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        currentFragmentActivity = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentFragmentActivity = activity as? FragmentActivity
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        currentFragmentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        currentFragmentActivity = null
    }

    private val activeSurvey: QwaryWebViewFragment?
        get() = currentFragmentActivity?.supportFragmentManager?.findFragmentByTag(QW_SURVEY_TAG) as? QwaryWebViewFragment

    private fun FragmentActivity.ifActive(): FragmentActivity? =
        takeIf { !isFinishing && !isDestroyed }

    fun onViewCreated(view: ViewGroup, dismissView: () -> Unit) {
        if (::qwWebView.isInitialized) {
//            Log.d(QW_TAG, "onViewCreated() isAttachedToWindow ${qwWebView.isAttachedToWindow}")
//            Log.d(QW_TAG, "onViewCreated() parent ${qwWebView.parent != null}")
//            handler.removeCallbacks(resetWebView)
//            isSdkReady = true // The SDK must have already been ready to get here
//            if (webView.parent != null) {
//                // The view is already attached, so avoid a crash
//                dismissView()
//                logError(IllegalStateException("Prevented displaying survey because provided view already has parent"))
//                return
//            }
            Log.d(QW_TAG, "onViewCreated() isInitialized ${::qwWebView.isInitialized}")
            view.addView(qwWebView)
        } else {
            // The process died with a survey present, so simply dismiss the view
//            Log.d(TAG, "onViewCreated() isInitialized false")
            dismissView()
        }
    }

    fun onViewDestroyed(view: ViewGroup) {
        Log.d(QW_TAG, "onViewDestroyed() called")
        if (!::qwWebView.isInitialized) {
            // We're dismissing the view because the process died, so there's nothing else to do
            return
        }
        view.removeView(qwWebView)
        // Reset the WebView to prevent network call from being stuck in a pending state
//        handler.postDelayed(resetWebView, RESET_DELAY_MS)
//        isSdkReady = false
    }

    /**
     *  Network status
     */
    private var isNetworkAvailable = false

    /**
     * Register a [ConnectivityManager.NetworkCallback] to reset the WebView when the network
     * becomes available
     */
    private fun setupListenersForNetworkStatus(context: Context) {
        val networkRequest: NetworkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
//        Log.d(QW_TAG, "onAvailable() called")
        isNetworkAvailable = true
        // Reset web view in order to re-fetch the configuration. Survey will not be shown until the
        // configuration is fetched.
//        onUiParallel {
//            resetWebView.run()
//        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isNetworkAvailable = false
    }

}