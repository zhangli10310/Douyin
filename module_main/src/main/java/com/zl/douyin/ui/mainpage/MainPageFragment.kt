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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.zl.core.Router
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.RVGestureDetector
import com.zl.douyin.R
import com.zl.douyin.ui.comment.CommentDialogFragment
import com.zl.douyin.ui.main.SharedViewModel
import com.zl.ijk.UriHeader
import com.zl.ijk.media.IRenderView
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.item_main_video.view.*
import tv.danmaku.ijk.media.player.IMediaPlayer
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
                showToast("不喜欢")
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (System.currentTimeMillis() - lastClickTime > 400) {
                    val holder = viewHolder as MainPageVideoAdapter.ViewHolder
                    if (holder.itemView.videoView.isPlaying) {
                        pauseCurrent(holder)
                    } else {
                        playCurrent(holder)
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

            it.itemView.commentImg.setOnClickListener { _ ->
                if (it.adapterPosition >= 0) {
                    showComment(list[it.adapterPosition].aweme_id ?: 0)
                }
            }

            it.itemView.headImg.setOnClickListener { _ ->
                shareViewModel.changeViewPagerPosition(2)
            }

            it.itemView.likeImg.setOnClickListener { _ ->

            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() { //加载更多，停止播放上一个视频并播放当前的

            var lastIndex = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> { //当屏幕停止滚动

                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val first = layoutManager.findFirstVisibleItemPosition()
                        val last = layoutManager.findLastVisibleItemPosition()
                        val index = (first + last) / 2
                        if (index >= 0 && index < list.size) {

                            if (lastIndex == index) {
                                return
                            }

                            list[index].author.let {
                                shareViewModel.currentSelectUser.postValue(it)
                            }

                            stopLastVideo()

                            play(index)

                            lastIndex = index
                        }

                        if ((last + 3) > list.size) {
                            loadMore()
                        }
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {//当屏幕滚动且用户使用的触碰或手指还在屏幕上

                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {//由于用户的操作，屏幕产生惯性滑动

                    }

                }
            }
        })

//        val viewPreloadSizeProvider = ViewPreloadSizeProvider<FeedItem>()
//        val preloader = RecyclerViewPreloader<FeedItem>(
//                Glide.with(this), MyPreloadModelProvider(this, list), viewPreloadSizeProvider, 2 /*maxPreload*/)
//        recyclerView.addOnScrollListener(preloader)

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
        if (lastHolder != null) {
            Log.d(TAG, "stopLastVideo: ")
            lastHolder!!.itemView.videoView.release(true)
        }
    }

    private var lastHolder: RecyclerView.ViewHolder? = null

    private fun play(index: Int) {
        val holder = recyclerView.findViewHolderForAdapterPosition(index)
        if (holder == null) {
            Log.i(TAG, "play: holder null")
            return
        }
        Log.i(TAG, "recycler child count: " + recyclerView.childCount)
        val uriList = mutableListOf<UriHeader>()
        for (url in list[index].video!!.play_addr!!.url_list!!) {
            uriList.add(UriHeader(Uri.parse(url)))
        }
        holder.itemView.videoView.setUriList(uriList)
//        holder.itemView.videoView.start()
        holder.itemView.pauseImg.visibility = View.GONE
        lastHolder = holder
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
                    recyclerView.post {
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
            lastHolder?.let { holder ->
                if (it == 1) {
                    playCurrent(holder as MainPageVideoAdapter.ViewHolder)
                } else {
                    pauseCurrent(holder as MainPageVideoAdapter.ViewHolder)
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

    fun playCurrent(holder: MainPageVideoAdapter.ViewHolder) {
        holder.itemView.videoView.start()
        holder.itemView.pauseImg.visibility = View.GONE
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