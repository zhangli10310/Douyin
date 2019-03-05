package com.zl.douyin.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.zl.core.Router
import com.zl.core.base.BaseFragment
import com.zl.core.base.ModeActivity
import com.zl.douyin.R
import com.zl.douyin.ui.littlefilm.LittleFilmFragment
import com.zl.douyin.ui.user.UserFragment
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = Router.MAIN_ACTIVITY)
class MainActivity : ModeActivity(), MainFragment.OnFragmentChangeListener {

    private val TAG = MainActivity::class.java.simpleName


    private lateinit var shareViewModel: SharedViewModel

    private val mainFragment: MainFragment = MainFragment()
    private val littleFilmFragment: LittleFilmFragment = LittleFilmFragment()
    private val userFragment: UserFragment = UserFragment()
    private lateinit var mAdapter: MainPagerAdapter

    override fun initView(savedInstanceState: Bundle?) {

        mAdapter = MainPagerAdapter(arrayListOf(littleFilmFragment, mainFragment, userFragment), supportFragmentManager)
        viewPager.adapter = mAdapter
        viewPager.currentItem = 1

        viewPager.setPageTransformer(false) { page, position ->

            if (!(page === littleFilmFragment.view)) {
                return@setPageTransformer
            }
            val width = page.width
            when {
                position < -1 -> {
                    page.alpha = 0f
                }
                position < 0 -> {
                    page.alpha = 1f
                    page.translationX = -position * width
                    page.scaleX = (1 + position / 10)
                    page.scaleY = (1 + position / 10)
                }
                position < 1 -> {
                    page.alpha = 1f
                    page.translationX = 0f
                }
                else -> {
                    page.alpha = 0f
                }
            }
        }

        checkViewPagerScroll()
    }

    override fun setListener() {

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

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
