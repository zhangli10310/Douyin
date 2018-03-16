package com.zl.douyin.ui.main

import android.os.Bundle
import android.support.v4.view.ViewPager
import com.zl.core.MainApp
import com.zl.core.base.ModeActivity
import com.zl.douyin.R
import com.zl.douyin.ui.user.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ModeActivity(), MainFragment.OnFragmentChangeListener {

    private val mainFragment: MainFragment = MainFragment()
    private lateinit var mAdapter: MainPagerAdapter

    override fun initView(savedInstanceState: Bundle?) {

        //fixme MainFragment()
        mAdapter = MainPagerAdapter(arrayListOf(MainFragment(), mainFragment, UserFragment()), supportFragmentManager)
        viewPager.adapter = mAdapter
        viewPager.currentItem = 1

        checkViewPagerScroll()
    }

    override fun setListener() {

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                checkViewPagerScroll()
            }

        })
    }

    private fun checkViewPagerScroll() {
        when {
            mainFragment.selectItem != 1 -> {
                viewPager.forbidToLeft = true
                viewPager.forbidToRight = true
            }
            MainApp.instance.user == null -> {
                viewPager.forbidToLeft = true
                viewPager.forbidToRight = false
            }
            else -> {
                viewPager.forbidToLeft = false
                viewPager.forbidToRight = false
            }
        }
    }

    override fun observe() {

    }

    override fun afterView() {

    }

    override fun layoutId() = R.layout.activity_main

    override fun onFragmentChange(item: Int) {
        checkViewPagerScroll()
    }
}
