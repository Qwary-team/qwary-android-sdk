package com.qwary.internal.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.qwary.R
import com.qwary.databinding.QwaryFragmentWebviewBinding
import com.qwary.internal.QwaryWebController

class QwaryBannerViewFragment : Fragment() {

    private val QW_TAG = javaClass.simpleName
    private lateinit var qwBinding: QwaryFragmentWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.qwary_theme)
        val view = inflater.cloneInContext(contextThemeWrapper)
        val layoutInflater = view?.inflate(R.layout.qwary_fragment_webview, container, false)
        qwBinding = layoutInflater?.let { QwaryFragmentWebviewBinding.bind(it) }!!
        return qwBinding.root
    }

//    override fun getTheme() = R.style.qwary_bottom_sheet_dialog_theme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val height= parseInt(webController.viewHeight)
//        Log.d(QW_TAG, "onViewCreated() called viewHeight = ${height}")
//        qwBinding.qwSurveyWebView.layoutParams= LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT, height
//        )
        Log.d(QW_TAG, "onViewCreated() called with")
        webController.onViewCreated(qwBinding.qwSurveyWebView) {
//            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webController.onViewDestroyed(qwBinding.qwSurveyWebView)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return BottomSheetDialog(requireContext(), theme).apply {
//            // Ideally we'd like set to true, but can crash on screen rotation while dismissing.
//            // Likely a material components bug, needs further investigation.
//            dismissWithAnimation = false
//            setOnShowListener {
//                (window?.findViewById<View>(R.id.qw_design_bottom_sheet) as? FrameLayout)?.let {
//                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
//                }
//            }
//        }
//    }

    companion object {
        private lateinit var webController: QwaryWebController
        fun newInstance(webController: QwaryWebController): QwaryBannerViewFragment {
            this.webController = webController
            return QwaryBannerViewFragment()
        }
    }

}