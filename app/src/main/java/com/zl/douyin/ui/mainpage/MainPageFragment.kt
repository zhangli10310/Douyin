package com.zl.douyin.ui.mainpage

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.zl.core.Router
import com.zl.core.base.ModeFragment
import com.zl.core.utils.DisplayUtils
import com.zl.core.view.RVGestureDetector
import com.zl.douyin.R
import com.zl.douyin.ui.comment.CommentDialogFragment
import com.zl.douyin.ui.main.SharedViewModel
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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

                            list[index].video?.play_addr?.url_list?.let {
                                if (it.isEmpty()) {
                                    return
                                }
                                play(index, it)
                            }

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

        searchImg.setOnClickListener {
            shareViewModel.gotoViewPagerPosition.postValue(0)
        }

        doublePointImg.setOnClickListener {
            Router.toFunc(activity!!, "http://www.baidu.com")
        }
    }

    private fun loadMore() {
        viewModel.loadMoreVideo()
    }

    private fun stopLastVideo() {
        if (lastHolder != null) {
            lastHolder!!.itemView.videoView.release(true)
            lastHolder!!.itemView.videoView.visibility = View.INVISIBLE
        }
    }

    private var lastHolder: RecyclerView.ViewHolder? = null

    private fun play(index: Int, urls: List<String>) {
        val holder = recyclerView.findViewHolderForAdapterPosition(index)
        if (holder == null) {
            Log.i(TAG, "play: holder null")
            return
        }
        var i = 0
//        holder.itemView.videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW)
        holder.itemView.videoView.setAspectRatio(IRenderView.AR_MATCH_WIDTH)
        holder.itemView.videoView.setOnCompletionListener {
            Log.i(TAG, "setOnCompletionListener: ")
            holder.itemView.videoView.start()
        }
        holder.itemView.videoView.setOnPreparedListener {
            Log.i(TAG, "setOnPreparedListener: ")
            holder.itemView.videoView.start()
        }

        holder.itemView.videoView.setOnInfoListener { _, arg2, _ ->

            //int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
            //int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
            //int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
            //int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
            //int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
            //int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
            //int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
            Log.d(TAG, arg2.toString())
            when (arg2) {
                IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:")
                    holder.itemView.videoView.postDelayed({
                        holder.itemView.videoView.visibility = View.VISIBLE
                    }, 180) //不延迟会有黑屏
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> { //缓冲
                    holder.itemView.bufferProgressBar.visibility = View.VISIBLE
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> { //缓冲结束
                    holder.itemView.bufferProgressBar.visibility = View.GONE
                }
            }
            true
        }
        holder.itemView.videoView.setOnErrorListener { _, _, _ ->
            i++
            if (i >= urls.size) {
                return@setOnErrorListener true
            }
            holder.itemView.videoView.setVideoURI(Uri.parse(urls[i]))
            holder.itemView.videoView.start()
            true
        }
        holder.itemView.videoView.setVideoURI(Uri.parse(urls[i]))
        holder.itemView.pauseImg.visibility = View.GONE
        lastHolder = holder
    }

    private fun showComment(aweId: Long) {
        val fragment = CommentDialogFragment.getInstance(aweId)
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
                        list[0].video?.play_addr?.url_list?.let {
                            play(0, it)
                        }
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
        IjkMediaPlayer.loadLibrariesOnce(null)
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