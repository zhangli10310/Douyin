package com.zl.douyin.ui.login

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meicai.lib.extend.addTextChangedListener
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_login.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/13 19:17.<br/>
 */
class LoginDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog != null && dialog.window != null) {
            dialog.window.attributes.windowAnimations = R.style.dialogAnim
        }
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
        setListener()
    }

    private fun init() {

    }

    private fun setListener() {

        phoneEdit.addTextChangedListener {
            if (it.isEmpty()) {
                if (otherLoginText.visibility != View.VISIBLE) {
                    otherLoginText.visibility = View.VISIBLE
                }
                if (loginWechatImg.visibility != View.VISIBLE) {
                    loginWechatImg.visibility = View.VISIBLE
                }
                if (agreementText.visibility == View.VISIBLE) {
                    agreementText.visibility = View.GONE
                }
                if (nextImg.visibility == View.VISIBLE) {
                    nextImg.visibility = View.GONE
                }
            } else {
                if (otherLoginText.visibility == View.VISIBLE) {
                    otherLoginText.visibility = View.INVISIBLE
                }
                if (loginWechatImg.visibility == View.VISIBLE) {
                    loginWechatImg.visibility = View.INVISIBLE
                }
                if (agreementText.visibility != View.VISIBLE) {
                    agreementText.visibility = View.VISIBLE
                }
                if (nextImg.visibility != View.VISIBLE) {
                    nextImg.visibility = View.VISIBLE
                }
            }
        }

        cancelText.setOnClickListener {
            dismiss()
        }
    }
}