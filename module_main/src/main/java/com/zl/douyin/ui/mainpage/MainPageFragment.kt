package com.zl.douyin.ui.mainpage

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.launcher.ARouter
import com.zl.core.Router
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.RVGestureDetector
import com.zl.douyin.R
import com.zl.douyin.ui.comment.CommentDialogFragment
import com.zl.douyin.ui.main.SharedViewModel
import com.zl.ijk.UriHeader
import com.zl.ijk.ZVideoView
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

//    private var hasMore = true
//    private var isLoading = false

    private lateinit var shareViewModel: SharedViewModel
    private lateinit var viewModel: MainPageViewModel

    private var list: MutableList<FeedItem> = mutableListOf()
    private lateinit var mAdapter: VideoPagerAdapter

    override fun layoutId() = R.layout.fragment_main_page

    override fun initView(savedInstanceState: Bundle?) {

        mAdapter = VideoPagerAdapter(list)
        recyclerViewPager.adapter = mAdapter
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
                showToast("不喜欢")
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (System.currentTimeMillis() - lastClickTime > 400) {
                    if (view!!.videoView.isPlaying) {
                        pauseCurrent(view!!)
                    } else {
                        playCurrent(view!!)
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

        mAdapter.registerViewClick { v, pos ->
            v.setOnTouchListener { _, event ->
                return@setOnTouchListener gestureDetector.onTouchEvent(event, v)
            }

            v.commentImg.setOnClickListener { _ ->
                if (pos >= 0) {
                    showComment(list[pos].aweme_id ?: 0)
                }
            }

            v.headImg.setOnClickListener { _ ->
                shareViewModel.changeViewPagerPosition(2)
            }

            v.likeImg.setOnClickListener { _ ->

            }
        }

        recyclerViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            var lastIndex = 0

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                shareViewModel.currentSelectUser.postValue(list[position].author)
                if (position + 3 > list.size) {
                    loadMore()
                }
                if (position >= 0 && position < list.size) {

                    if (lastIndex == position) {
                        return
                    }

                    list[position].author.let {
                        shareViewModel.currentSelectUser.postValue(it)
                    }

                    stopLastVideo()

                    play(position)

                    lastIndex = position
                }
            }

        })

        searchImg.setOnClickListener {
            shareViewModel.gotoViewPagerPosition.postValue(0)
        }

        doublePointImg.setOnClickListener {
            ARouter.getInstance().build(Router.LIVE_ACTIVITY).navigation()
        }
    }

    private fun loadMore() {
        viewModel.loadMoreVideo()
    }


    private fun stopLastVideo() {
        if (lastView != null) {
            Log.d(TAG, "stopLastVideo: ")
            lastView!!.videoView.release(true)
        }

    }

    private var lastView: View? = null
    private fun play(index: Int) {
        val v = recyclerViewPager.findViewWithTag<View>(index)

        if (v == null) {
            Log.i(TAG, "play: holder null")
            return
        }
        Log.i(TAG, "recycler child count: " + recyclerViewPager.childCount)
        val uriList = mutableListOf<UriHeader>()
        for (url in list[index].video!!.play_addr!!.url_list!!) {
            uriList.add(UriHeader(Uri.parse(url)))
        }
        v.videoView.setUriList(uriList)
        v.pauseImg.visibility = View.GONE
        lastView = v
    }

    private fun showComment(aweId: Long) {
        val fragment = CommentDialogFragment.newInstance(aweId)
        fragment.show(fragmentManager!!, "tag")
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

    private fun anim(heartView: View, v: ViewGroup) {
        val set = AnimatorSet()
        val random = Random()
        val ran = DisplayUtils.dp2px(context!!, random.nextInt(40).toFloat())

        val firstSet = AnimatorSet()
        firstSet.play(ObjectAnimator.ofFloat(heartView, View.SCALE_X, 1f, 0.8f))
                .with(ObjectAnimator.ofFloat(heartView, View.SCALE_Y, 1f, 0.8f))
        firstSet.duration = 100

        set.play(ObjectAnimator.ofFloat(heartView, View.ALPHA, 1f, 0.3f))
                .with(ObjectAnimator.ofFloat(heartView, View.TRANSLATION_X, 0f, (ran - DisplayUtils.dp2px(context!!, 20f)) * 2))
                .with(ObjectAnimator.ofFloat(heartView, View.TRANSLATION_Y, 0f, -2 * ran))
                .with(ObjectAnimator.ofFloat(heartView, View.SCALE_X, 0.8f, 1.4f))
                .with(ObjectAnimator.ofFloat(heartView, View.SCALE_Y, 0.8f, 1.4f))

        set.interpolator = AccelerateInterpolator()
        set.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                v.removeView(heartView)
//                likeHeartRecycler.offer(heartView)
            }
        })
        set.duration = 600
        set.startDelay = 100

        firstSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                set.start()
            }
        })
        firstSet.start()
    }

    override fun observe() {
        viewModel = ViewModelProviders.of(this, MainPageViewModel.Factory(MainPageRepository.get())).get(MainPageViewModel::class.java)

        shareViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

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
                if (!list.isEmpty()) {
                    list[0].author.let {
                        shareViewModel.currentSelectUser.postValue(it)
                    }
                    recyclerViewPager.post {
                        play(0)
                    }
                }
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

        shareViewModel.onViewPagerChange.observe(this, Observer {
            lastView?.let { v ->
                if (it == 1) {
                    playCurrent(v)
                } else {
                    pauseCurrent(v)
                }
            }

        })
    }

    fun pauseCurrent(v: View) {
        v.videoView.pause()
        v.pauseImg.visibility = View.VISIBLE
        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(v.pauseImg, View.SCALE_X, 1.2f, 1.0f),
                ObjectAnimator.ofFloat(v.pauseImg, View.SCALE_Y, 1.2f, 1.0f)
        )
        set.start()
    }

    fun playCurrent(v: View) {
        v.videoView.start()
        v.pauseImg.visibility = View.GONE
    }

    override fun loadingProgressBarId() = R.id.loadingBar

    override fun afterView() {
        viewModel.loadRecommendVideo()
    }

    override fun onStart() {
        super.onStart()
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
    }

    override fun onStop() {
        super.onStop()
        stopLastVideo()
        IjkMediaPlayer.native_profileEnd()
    }

    fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        viewModel.loadRecommendVideo()
    }

}