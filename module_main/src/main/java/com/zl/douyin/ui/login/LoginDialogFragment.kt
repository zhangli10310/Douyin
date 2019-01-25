package com.zl.douyin.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_login.*
import android.text.Spanned
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.zl.core.MainApp
import com.zl.core.db.user.User
import com.zl.core.extend.addTextChangedListener
import kotlin.concurrent.thread


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
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window.attributes.windowAnimations = R.style.dialogAnim
        }
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
        setListener()
    }

    private fun init() {

        val spannableInfo = SpannableString("继续表示你同意抖音用户协议")
        spannableInfo.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(activity, "必须同意", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(activity!!, com.zl.core.R.color.link)
            }
        }, 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        agreementText.text = spannableInfo
        agreementText.movementMethod = LinkMovementMethod.getInstance()
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

        nextImg.setOnClickListener {
            thread {
                MainApp.instance.user = User(0)
                activity!!.runOnUiThread {
                    dismiss()
                }
            }
        }
    }
}