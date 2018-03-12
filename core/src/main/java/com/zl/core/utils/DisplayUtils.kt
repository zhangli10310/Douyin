package com.zl.core.utils

import android.content.Context

/**
 * Created by zhangli <br/>
 */
object DisplayUtils {
    /**
     * convert px to its equivalent dp
     *
     * 将px转换为与之相等的dp
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }


    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    fun dp2px(context: Context, dipValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f)
    }


    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    fun px2sp(context: Context, pxValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f)
    }

    /**
     * convert sp to its equivalent px
     *
     * 将sp转换为px
     */
    fun sp2px(context: Context, spValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f)
    }

    fun dp2sp(context: Context, dpValue: Float): Float {
        val px = dp2px(context, dpValue)
        return px2sp(context, px)
    }
}