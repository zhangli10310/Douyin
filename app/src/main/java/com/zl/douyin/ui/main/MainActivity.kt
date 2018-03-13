package com.zl.douyin.ui.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.zl.core.MainApp
import com.zl.core.base.ModeActivity
import com.zl.douyin.R
import com.zl.douyin.ui.login.LoginDialogFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ModeActivity() {

    private var selectItem = 0

    private var loginFragment: LoginDialogFragment? = null

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setListener() {

        mainPageText.setOnClickListener {
            clickMain()
        }

        followText.setOnClickListener {
            clickFollow()
        }

        addContentImg.setOnClickListener {
            clickAddContent()
        }

        messageText.setOnClickListener {
            clickMessage()
        }

        mineText.setOnClickListener {
            clickMine()
        }
    }

    private fun changeSelectItem(i: Int): Boolean {
        if (i == selectItem) {
            return true
        } else {
            when (selectItem) {
                1 -> {
                    mainPageText.textSize = 18f
                    mainPageText.setTextColor(ContextCompat.getColor(this, com.zl.core.R.color.txt_unselected))
                    mainPageImg.visibility = View.GONE
                }
                2 -> {
                    followText.textSize = 18f
                    followText.setTextColor(ContextCompat.getColor(this, com.zl.core.R.color.txt_unselected))
                    followImg.visibility = View.GONE
                }
                3 -> {
                    messageText.textSize = 18f
                    messageText.setTextColor(ContextCompat.getColor(this, com.zl.core.R.color.txt_unselected))
                    messageImg.visibility = View.GONE
                }
                4 -> {
                    mineText.textSize = 18f
                    mineText.setTextColor(ContextCompat.getColor(this, com.zl.core.R.color.txt_unselected))
                    mineImg.visibility = View.GONE
                }
            }
            when (i) {
                1 -> {
                    mainPageText.textSize = 20f
                    mainPageText.setTextColor(Color.WHITE)
                    mainPageImg.visibility = View.VISIBLE
                }
                2 -> {
                    followText.textSize = 20f
                    followText.setTextColor(Color.WHITE)
                    followImg.visibility = View.VISIBLE
                }
                3 -> {
                    messageText.textSize = 20f
                    messageText.setTextColor(Color.WHITE)
                    messageImg.visibility = View.VISIBLE
                }
                4 -> {
                    mineText.textSize = 20f
                    mineText.setTextColor(Color.WHITE)
                    mineImg.visibility = View.VISIBLE
                }
            }
            selectItem = i
            return false
        }

    }

    override fun observe() {

    }

    override fun afterView() {
        clickMain()
    }

    override fun layoutId() = R.layout.activity_main

    private fun clickMain() {
        if (changeSelectItem(1)) {

        }
    }

    private fun clickFollow() {

        if (MainApp.instance.user == null) { //未登陆
            showLoginFragment()
        } else {
            changeSelectItem(2)
        }
    }

    private fun clickAddContent() {

        if (MainApp.instance.user == null) { //未登陆
            showLoginFragment()
        }

    }

    private fun clickMessage() {

        if (MainApp.instance.user == null) { //未登陆
            showLoginFragment()
        } else {
            changeSelectItem(3)
        }

    }

    private fun clickMine() {

        if (MainApp.instance.user == null) { //未登陆
            showLoginFragment()
        } else {
            changeSelectItem(4)
        }

    }

    private fun showLoginFragment() {
        if (loginFragment == null) {
            loginFragment = LoginDialogFragment()
        }
        loginFragment!!.show(supportFragmentManager, "login")
    }

}
