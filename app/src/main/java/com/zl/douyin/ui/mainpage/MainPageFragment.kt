package com.zl.douyin.ui.mainpage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.util.Log
import android.view.*
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.LikeHeartView
import com.zl.core.view.RVGestureDetector
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_main_page.*
import java.util.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/14 15:33.<br/>
 */
class MainPageFragment : ModeFragment() {

    private val TAG = MainPageFragment::class.java.simpleName

    private lateinit var viewModel: MainPageViewModel

    private var list: MutableList<VideoEntity> = mutableListOf()
    private lateinit var mAdapter: MainPageVideoAdapter

    override fun layoutId() = R.layout.fragment_main_page

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.layoutManager = LinearLayoutManager(activity)
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        mAdapter = MainPageVideoAdapter(list)
        recyclerView.adapter = mAdapter
    }

    override fun setListener() {

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRecommendVideo()
        }

        val gestureDetector = RVGestureDetector(activity, object : RVGestureDetector.RVOnGestureListener() {

            var lastClickTime = 0L

            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
                Log.i(TAG, "不喜欢?")
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                if (System.currentTimeMillis() - lastClickTime > 500) {
                    Log.i(TAG, "暂停")
                }
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                showLikeHeart(e.x, e.y)
                lastClickTime = System.currentTimeMillis()
                return true
            }

        })

        mAdapter.registerViewClick {
            it.itemView.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event, it)
            }
        }
    }

    private val likeHeartRecycler: Queue<LikeHeartView> = ArrayDeque()
    private fun showLikeHeart(x: Float, y: Float) {
        val v = view
        if (v is ConstraintLayout) {
            Log.i(TAG, "showLikeHeart: ${likeHeartRecycler.size}")
            var heartView = likeHeartRecycler.poll()
            val params: ConstraintLayout.LayoutParams
            if (heartView == null) {
                heartView = LikeHeartView(activity)
                params = ConstraintLayout.LayoutParams(DisplayUtils.dp2px(activity, 100f).toInt(), DisplayUtils.dp2px(activity, 140f).toInt())
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                heartView.layoutParams = params
            } else {
                heartView.reset()
                params = heartView.layoutParams as ConstraintLayout.LayoutParams
            }
            params.leftMargin = x.toInt() - DisplayUtils.dp2px(activity, 50f).toInt()
            params.topMargin = y.toInt() - DisplayUtils.dp2px(activity, 70f).toInt()
            v.addView(heartView)

            heartView.anim({
                v.removeView(it)
                likeHeartRecycler.offer(it)
            })
        }
    }

    override fun observe() {
        viewModel = ViewModelProviders.of(this, MainPageViewModel.Factory(MainPageRepository.get())).get(MainPageViewModel::class.java)

        viewModel.isLoadingInner.observe(this, Observer {
            if (it != null && it) { //正在加载,不显示弹框
                loadingInner()
            } else {
                cancelLoadingInner()
            }
        })

        viewModel.isLoading.observe(this, Observer {
            if (it != null && it) { //正在加载
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.errorMsg.observe(this, Observer {
            if (it != null) {
                showToastSafe("${it.code}-${it.msg}")
            }
        })

        viewModel.videoList.observe(this, Observer {
            if (it != null) {
                Log.i(TAG, "${it.size} new video")
                list.clear()
                list.addAll(it)
                mAdapter.notifyDataSetChanged()
            }
        })

        viewModel.moreVideoList.observe(this, Observer {
            if (it != null) {
                list.addAll(it)
                mAdapter.notifyDataSetChanged()
            }
        })

        viewModel.loadingRecommendStatus.observe(this, Observer {
            if (it != null) { //正在加载
                when (it) {
                    1 -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        })
    }

    override fun loadingProgressBarId() = R.id.loadingBar

    override fun afterView() {
        viewModel.loadRecommendVideo()
    }

    fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        viewModel.loadRecommendVideo()
    }

}