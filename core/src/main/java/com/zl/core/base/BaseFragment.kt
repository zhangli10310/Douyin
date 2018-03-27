package com.zl.core.base

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar


/**
 *
 *<p></p>
 *
 * Created by zhangli<br/>
 */
open class BaseFragment : Fragment() {

    fun showToast(text: CharSequence) {
        getBaseActivity().showToastSafe(text)
    }

    fun getBaseActivity(): BaseActivity {
        return activity as BaseActivity
    }

    fun showToastSafe(text: CharSequence, gravity: Int = Gravity.BOTTOM, xOffset: Int = 0, yOffset: Int = 0) {
        getBaseActivity().showToastSafe(text, gravity, xOffset, yOffset)
    }

    fun showLoading() {
        getBaseActivity().showLoading()
    }

    fun hideLoading() {
        getBaseActivity().hideLoading()
    }

    /**
     * activity界面内的ProgressBarId，不阻塞页面操作的加载提示 [loadingInner()]
     */
    open protected fun loadingProgressBarId() = 0

    /**
     * activity界面内的ProgressBarId，不阻塞页面操作的加载提示 [loadingProgressBarId()]
     */
    open fun loadingInner() {
        if (loadingProgressBarId() == 0) {
            return
        }
        val progressBar = view?.findViewById<View>(loadingProgressBarId())
        progressBar?.visibility = View.VISIBLE

    }

    open fun cancelLoadingInner() {
        if (loadingProgressBarId() == 0) {
            return
        }
        val progressBar = view?.findViewById<ProgressBar>(loadingProgressBarId())
        progressBar?.visibility = View.GONE
    }

    override fun startActivity(intent: Intent) {
        try {
            super.startActivity(intent)
        } catch (e: Exception) {
            getBaseActivity().showToastSafe("启动失败")
        }
    }

}