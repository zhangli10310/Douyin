package com.zl.core.view

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.AccessibilityDelegateCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v7.app.AppCompatDialog
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import com.zl.core.R
import com.zl.core.utils.DisplayUtils

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/25 14:42.<br/>
 */
class BaseBottomSheetDialog : AppCompatDialog {

    private var mBehavior: BottomSheetBehavior<FrameLayout>? = null

    internal var mCancelable = true
    private var mCanceledOnTouchOutside = true
    private var mCanceledOnTouchOutsideSet: Boolean = false

    public var heightDP = 360f

    constructor(context: Context) : this(context, 0)

    constructor(context: Context, theme: Int) : super(context, BaseBottomSheetDialogUtils.getThemeResId(context, theme)) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        mCancelable = cancelable
    }


    override fun setContentView(layoutResID: Int) {
        super.setContentView(wrapInBottomSheet(layoutResID, null, null))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        if (window != null) {
//            if (Build.VERSION.SDK_INT >= 21) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            }
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        if (mCancelable != cancelable) {
            mCancelable = cancelable
            mBehavior?.isHideable = cancelable
        }
    }

    override fun onStart() {
        super.onStart()
        mBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        if (cancel && !mCancelable) {
            mCancelable = true
        }
        mCanceledOnTouchOutside = cancel
        mCanceledOnTouchOutsideSet = true
    }

    private fun wrapInBottomSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {
        var view = view
        val coordinator = View.inflate(context, R.layout.design_base_bottom_sheet_dialog, null) as CoordinatorLayout
        if (layoutResId != 0 && view == null) {
            view = layoutInflater.inflate(layoutResId, coordinator, false)
        }
        val bottomSheet = coordinator.findViewById<View>(android.support.design.R.id.design_bottom_sheet) as FrameLayout
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior!!.setBottomSheetCallback(mBottomSheetCallback)
        mBehavior!!.isHideable = mCancelable
        if (params == null) {
            val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.dp2px(context, heightDP).toInt())
            bottomSheet.addView(view, p)
        } else {
            bottomSheet.addView(view, params)
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        coordinator.findViewById<View>(android.support.design.R.id.touch_outside).setOnClickListener {
            if (mCancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
                cancel()
            }
        }
        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(bottomSheet, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View,
                                                           info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                if (mCancelable) {
                    info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                    info.isDismissable = true
                } else {
                    info.isDismissable = false
                }
            }

            override fun performAccessibilityAction(host: View, action: Int, args: Bundle): Boolean {
                if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && mCancelable) {
                    cancel()
                    return true
                }
                return super.performAccessibilityAction(host, action, args)
            }
        })
        bottomSheet.setOnTouchListener { view, event ->
            // Consume the event and prevent it from falling through
            true
        }
        return coordinator
    }

    internal fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!mCanceledOnTouchOutsideSet) {
            val a = context.obtainStyledAttributes(
                    intArrayOf(android.R.attr.windowCloseOnTouchOutside))
            mCanceledOnTouchOutside = a.getBoolean(0, true)
            a.recycle()
            mCanceledOnTouchOutsideSet = true
        }
        return mCanceledOnTouchOutside
    }

    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View,
                                    @BottomSheetBehavior.State newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

}

object BaseBottomSheetDialogUtils {
    fun getThemeResId(context: Context, themeId: Int): Int {
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
            val outValue = TypedValue()
            return if (context.theme.resolveAttribute(R.attr.bottomSheetDialogTheme, outValue, true)) {
                outValue.resourceId
            } else {
                // bottomSheetDialogTheme is not provided; we default to our light theme
                R.style.Theme_Design_Light_BottomSheetDialog
            }
        }
        return themeId
    }
}