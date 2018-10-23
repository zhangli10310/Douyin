package com.zl.douyin.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.zl.core.base.BaseFragment
import com.zl.core.base.ModeActivity
import com.zl.douyin.R
import com.zl.douyin.ui.user.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ModeActivity(), MainFragment.OnFragmentChangeListener {


    private lateinit var shareViewModel:SharedViewModel

    private val mainFragment: MainFragment = MainFragment()
    private lateinit var mAdapter: MainPagerAdapter

    override fun initView(savedInstanceState: Bundle?) {

        //fixme BaseFragment()
        mAdapter = MainPagerAdapter(arrayListOf(BaseFragment(), mainFragment, UserFragment()), supportFragmentManager)
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
                shareViewModel.onViewPagerPositionChange(position)
                if (position == 2) {
                    shareViewModel.queryUser.postValue(true)
                }
            }

        })
    }

    private fun checkViewPagerScroll() {
        when {
            mainFragment.selectItem != 1 -> {
                viewPager.forbidToLeft = true
                viewPager.forbidToRight = true
            }
            else -> {
                viewPager.forbidToLeft = false
                viewPager.forbidToRight = false
            }
        }
    }

    override fun observe() {
        shareViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        shareViewModel.gotoViewPagerPosition.observe(this, Observer {
            if (it != null) {
                viewPager.currentItem = it
            }
        })
    }

    override fun afterView() {

    }

    override fun layoutId() = R.layout.activity_main

    override fun onFragmentChange(item: Int) {
        checkViewPagerScroll()
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 2 || viewPager.currentItem == 0) {
            viewPager.currentItem = 1
        } else {
            super.onBackPressed()
        }
    }
}
