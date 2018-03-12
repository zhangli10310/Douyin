package com.zl.core.base

import android.content.Intent
import android.support.v4.app.Fragment


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

    override fun startActivity(intent: Intent) {
        try {
            super.startActivity(intent)
        } catch (e: Exception) {
            getBaseActivity().showToastSafe("启动失败")
        }
    }

}