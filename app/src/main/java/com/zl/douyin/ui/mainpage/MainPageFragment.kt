package com.zl.douyin.ui.mainpage

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.RVGestureDetector
import com.zl.douyin.R
import com.zl.douyin.ui.comment.CommentDialogFragment
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.item_main_video.view.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer
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

        val gestureDetector = RVGestureDetector(activity!!, object : RVGestureDetector.RVOnGestureListener() {

            var lastClickTime = 0L
            var firstDoubleTap = true

            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
                Log.i(TAG, "不喜欢?")
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (System.currentTimeMillis() - lastClickTime > 400) {
                    val holder = viewHolder as MainPageVideoAdapter.ViewHolder
                    if (holder.itemView.videoView.isPlaying) {
                        pauseCurrent(holder)
                    } else {
                        holder.itemView.videoView.start()
                        holder.itemView.pauseImg.visibility = View.GONE
                    }
                } else {
                    showLikeHeart(e.x, e.y)
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if (System.currentTimeMillis() - lastClickTime > 400) {
                    firstDoubleTap = true
                }
                return super.onDoubleTap(e)
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                if (firstDoubleTap) {
                    firstDoubleTap = false
                    return true
                }
                showLikeHeart(e.x, e.y)
                lastClickTime = System.currentTimeMillis()
                return true
            }

        })

        mAdapter.registerViewClick {
            it.itemView.setOnTouchListener { _, event ->
                return@setOnTouchListener gestureDetector.onTouchEvent(event, it)
            }

            it.itemView.musicIconImg.setOnClickListener {
                showComment()
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> { //当屏幕停止滚动

                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {//当屏幕滚动且用户使用的触碰或手指还在屏幕上

                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {//由于用户的操作，屏幕产生惯性滑动

                    }

                }
            }
        })
    }

    private fun showComment() {
        val fragment = CommentDialogFragment()
        fragment.show(fragmentManager, "tag")
    }

    //    private val likeHeartRecycler: Queue<View> = ArrayDeque()
    private fun showLikeHeart(x: Float, y: Float) {
        val v = view
        if (v is ConstraintLayout) {

            val width = DisplayUtils.dp2px(activity!!, 120f).toInt()
            val height = DisplayUtils.dp2px(activity!!, 120f).toInt()

            val heartView = View(activity)
            heartView.setBackgroundResource(com.zl.core.R.mipmap.ic_heart)
            val params = ConstraintLayout.LayoutParams(width, height)
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.leftMargin = x.toInt() - width / 2
            params.topMargin = y.toInt() - height
            heartView.layoutParams = params
            heartView.rotation = (System.currentTimeMillis() % 40 - 20).toFloat()
            v.addView(heartView)

            anim(heartView, v)
        }
    }

    private fun anim(heartView: View, v: ConstraintLayout) {
        val set = AnimatorSet()
        val random = Random()
        val ran = DisplayUtils.dp2px(context!!, random.nextInt(60).toFloat())
        set.playTogether(ObjectAnimator.ofFloat(heartView, View.ALPHA, 1f, 0.3f),
                ObjectAnimator.ofFloat(heartView, View.TRANSLATION_X, 0f, (ran - DisplayUtils.dp2px(context!!, 30f)) * 2),
                ObjectAnimator.ofFloat(heartView, View.TRANSLATION_Y, 0f, -ran),
                ObjectAnimator.ofFloat(heartView, View.SCALE_X, 1f, 1.4f),
                ObjectAnimator.ofFloat(heartView, View.SCALE_Y, 1f, 1.4f)
        )
        set.interpolator = AccelerateInterpolator()
        set.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                v.removeView(heartView)
//                likeHeartRecycler.offer(heartView)
            }
        })
        set.startDelay = 200L
        set.start()
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

    fun pauseCurrent(viewHolder: MainPageVideoAdapter.ViewHolder) {
        viewHolder.itemView.videoView.pause()
        viewHolder.itemView.pauseImg.visibility = View.VISIBLE
        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(viewHolder.itemView.pauseImg, View.SCALE_X, 1.2f, 1.0f),
                ObjectAnimator.ofFloat(viewHolder.itemView.pauseImg, View.SCALE_Y, 1.2f, 1.0f)
        )
        set.start()
    }

    override fun loadingProgressBarId() = R.id.loadingBar

    override fun afterView() {
        viewModel.loadRecommendVideo()
    }

    override fun onStart() {
        super.onStart()
        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
    }

    override fun onStop() {
        super.onStop()
        IjkMediaPlayer.native_profileEnd()
    }

    fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        viewModel.loadRecommendVideo()
    }

}