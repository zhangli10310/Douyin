package com.zl.douyin.ui.user

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.GridSpacingItemDecoration
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_user.*
import com.bumptech.glide.Glide


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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE ->
                        //当屏幕停止滚动，加载图片
                        try {
                            Glide.with(activity).resumeRequests()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    RecyclerView.SCROLL_STATE_DRAGGING ->
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        try {
                            Glide.with(activity).pauseRequests()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    RecyclerView.SCROLL_STATE_SETTLING ->
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        try {
                            Glide.with(activity).pauseRequests()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                }
            }
        })
    }

    override fun observe() {

    }

    override fun afterView() {

    }

    override fun layoutId() = R.layout.fragment_user
}