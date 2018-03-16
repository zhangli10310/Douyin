package com.zl.douyin.ui.main

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zl.core.base.BaseFragment

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/16 17:27.<br/>
 */
class MainPagerAdapter(private val list: MutableList<BaseFragment>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int) = list[position]

    override fun getCount() = list.size
}