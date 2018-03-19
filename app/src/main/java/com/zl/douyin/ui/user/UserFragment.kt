package com.zl.douyin.ui.user

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.GridSpacingItemDecoration
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_user.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/16 18:02.<br/>
 */
class UserFragment : ModeFragment() {

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount = 3, space = DisplayUtils.dp2px(activity, 1f).toInt(), includeEdge = false))
        recyclerView.adapter = UserVideoAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false


    }

    override fun setListener() {
        backImg.setOnClickListener {
            activity.onBackPressed()
        }
    }

    override fun observe() {

    }

    override fun afterView() {

    }

    override fun layoutId() = R.layout.fragment_user
}