package com.qwary

import android.content.Context
import androidx.annotation.Keep
import androidx.fragment.app.FragmentActivity
import com.qwary.internal.QwaryWebController

@Keep
internal interface QwaryInterface {
    fun configure(context: Context, qwSettings: String, fragmentActivity: FragmentActivity? = null)
    fun presentSurvey(fragmentActivity: FragmentActivity, isBanner: Boolean)
    fun addEvent(eventName: String)
    fun logout()
    fun dismissActiveSurvey()
}

@Keep
object Qwary : QwaryInterface by QwaryWebController()