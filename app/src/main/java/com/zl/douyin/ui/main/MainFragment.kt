package com.zl.douyin.ui.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.zl.core.MainApp
import com.zl.core.base.BaseFragment
import com.zl.core.base.ModeFragment
import com.zl.douyin.R
import com.zl.douyin.ui.login.LoginDialogFragment
import com.zl.douyin.ui.mainpage.MainPageFragment
import kotlinx.android.synthetic.main.fragment_main.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/16 17:17.<br/>
 */
class MainFragment : ModeFragment() {

    var selectItem = 0

    private var currentFragment: BaseFragment? = null
    private var mainPageFragment: BaseFragment? = null

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
            val colorUnselected = ContextCompat.getColor(activity, com.zl.core.R.color.txt_unselected)
            when (selectItem) {
                1 -> {
                    mainPageText.textSize = 18f
                    mainPageText.setTextColor(colorUnselected)
                    mainPageImg.visibility = View.GONE
                }
                2 -> {
                    followText.textSize = 18f
                    followText.setTextColor(colorUnselected)
                    followImg.visibility = View.GONE
                }
                3 -> {
                    messageText.textSize = 18f
                    messageText.setTextColor(colorUnselected)
                    messageImg.visibility = View.GONE
                }
                4 -> {
                    mineText.textSize = 18f
                    mineText.setTextColor(colorUnselected)
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
            (activity as OnFragmentChangeListener).onFragmentChange(selectItem)
            return false
        }

    }

    override fun observe() {

    }

    override fun afterView() {
        clickMain()
    }

    override fun layoutId() = R.layout.fragment_main

    private fun clickMain() {
        if (changeSelectItem(1)) {
            (mainPageFragment as MainPageFragment).refresh()
        } else {
            if (mainPageFragment == null) {
                mainPageFragment = MainPageFragment()
            }
            showFragment(mainPageFragment!!)
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

    private fun showFragment(fragment: BaseFragment) {
        if (fragment != currentFragment) {
            val transaction = childFragmentManager.beginTransaction()

            val fragmentByTag = childFragmentManager.findFragmentByTag(fragment::class.java.simpleName)
            if (fragmentByTag != null) {
                transaction.show(fragmentByTag)
            } else {
                transaction.add(R.id.frameLayout, fragment, fragment::class.java.simpleName)
            }
            if (currentFragment != null) {
                transaction.hide(currentFragment)
            }

            transaction.commit()
            currentFragment = fragment
        }
    }

    private fun showLoginFragment() {
        if (loginFragment == null) {
            loginFragment = LoginDialogFragment()
        }
        loginFragment!!.show(childFragmentManager, "login")
    }

    interface OnFragmentChangeListener {
        fun onFragmentChange(item: Int)
    }
}