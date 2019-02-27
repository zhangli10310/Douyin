package com.zl.douyin.ui.mainpage

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.zl.core.extend.inflate
import com.zl.core.utils.CommonUtils
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import kotlinx.android.synthetic.main.item_main_video.view.*
import java.util.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2019/2/26 15:03.<br/>
 */
class VideoPagerAdapter(private var list: MutableList<FeedItem>) : PagerAdapter() {

    private val TAG = VideoPagerAdapter::class.java.simpleName

    private var mCurrentView: View? = null

    private val viewCache: LinkedList<View> = LinkedList()
    private var viewClick: ((View, Int) -> Unit)? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val convertView = if (viewCache.size == 0) {

            container.inflate(R.layout.item_main_video, container, false)
        } else {
            viewCache.removeFirst()
        }
        bind(convertView, position)
        container.addView(convertView)
        return convertView
    }

    private fun bind(view: View, position: Int) {
        Log.i(TAG, "bind: $position")
        view.tag = position
        viewClick?.invoke(view, position)

        val feedItem = list[position]

        feedItem.music?.let {
            view.musicText.text = it.title + "-" + it.owner_nickname
        }

        view.descText.text = feedItem.desc

        feedItem.author?.let {
            view.authorText.text = "@${it.nickname}"
        }

        feedItem.statistics?.let {
            view.commentCountText.text = CommonUtils.formatCount(it.comment_count)
            view.shareCountText.text = CommonUtils.formatCount(it.share_count)
            view.likeCountText.text = CommonUtils.formatCount(it.digg_count)
        }

        if (feedItem.user_digged == 1) {
            view.likeImg.setImageResource(com.zl.core.R.mipmap.ic_like)
        }

        feedItem.author?.avatar_thumb?.url_list?.let {
            GlideUtils.load(it, view.headImg)
        }

        feedItem.music?.cover_thumb?.url_list?.let {
            GlideUtils.load(it, view.musicRoundImg)
        }

        feedItem.video?.apply {

            Log.i(TAG, "data:width=$width, height=$height")
            view.videoView.setVideoSize(width ?: 0, height ?: 0)

//            val uriList = mutableListOf<UriHeader>()
//            play_addr?.url_list?.apply {
//                for (url in this) {
//                    uriList.add(UriHeader(Uri.parse(url)))
//                }
//            }
//            view.videoView.setUriList(uriList)

        }?.origin_cover?.url_list?.let {
            GlideUtils.load(it, view.videoView.previewImage)
        }

        if (!feedItem.region.isNullOrEmpty()) {
            view.regionText.text = feedItem.region
        }

        view.videoView.isLoop = true

    }

    override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
        container.removeView(o as View)
        o.tag = null
//        viewCache.add(o)
    }


    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

//    override fun setPrimaryItem(container: ViewGroup, position: Int, o: Any) {
//        Log.i(TAG, "setPrimaryItem: "+position)
//        mCurrentView = o as View
//    }
//
//    fun getPrimaryItem(): View? {
//        return mCurrentView
//    }

    override fun getCount() = list.size

    fun registerViewClick(viewClick: ((View, Int) -> Unit)) {
        this.viewClick = viewClick
    }
}