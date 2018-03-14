package com.zl.douyin.ui.mainpage

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import com.zl.core.base.ModeFragment
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_main_page.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/14 15:33.<br/>
 */
class MainPageFragment : ModeFragment() {

    private lateinit var mAdapter: MainPageVideoAdapter

    override fun layoutId() = R.layout.fragment_main_page

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.layoutManager = LinearLayoutManager(activity)
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        mAdapter = MainPageVideoAdapter()
        recyclerView.adapter = mAdapter
    }

    override fun setListener() {

    }

    override fun observe() {

    }

    override fun afterView() {

    }

    fun refresh() {

    }
}