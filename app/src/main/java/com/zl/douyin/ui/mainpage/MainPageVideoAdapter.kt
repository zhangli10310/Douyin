package com.zl.douyin.ui.mainpage

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.zl.core.extend.inflate
import com.zl.core.utils.CommonUtils
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import kotlinx.android.synthetic.main.item_main_video.view.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/14 17:19.<br/>
 */
class MainPageVideoAdapter(private var list: MutableList<FeedItem>) : RecyclerView.Adapter<MainPageVideoAdapter.ViewHolder>() {

    private val TAG = MainPageVideoAdapter::class.java.simpleName

    private var viewClick: ((ViewHolder) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewClick?.invoke(holder)

        val feedItem = list[position]

        feedItem.music?.let {
            holder.itemView.musicText.text = it.title + "-" + it.owner_nickname
        }

        holder.itemView.descText.text = feedItem.desc

        feedItem.author?.let {
            holder.itemView.authorText.text = "@${it.nickname}"
        }

        feedItem.statistics?.let {
            holder.itemView.commentCountText.text = CommonUtils.formatCount(it.comment_count)
            holder.itemView.shareCountText.text = CommonUtils.formatCount(it.share_count)
            holder.itemView.likeCountText.text = CommonUtils.formatCount(it.digg_count)
        }

        if (feedItem.user_digged == 1) {
            holder.itemView.likeImg.setImageResource(com.zl.core.R.mipmap.ic_like)
        }

        feedItem.author?.avatar_thumb?.url_list?.let {
            GlideUtils.load(it, holder.itemView.headImg)
        }

        feedItem.music?.cover_thumb?.url_list?.let {
            GlideUtils.load(it, holder.itemView.musicRoundImg)
        }

        feedItem.video?.apply {

            Log.i(TAG, "data:width=$width, height=$height")

            //计算适配屏幕
//            if (videoFrameWidth == null || videoFrameWidth!! == 0) {
//                videoFrameWidth = holder.itemView.displayFrameLayout.width
//            }
//            if (videoFrameHeight == null || videoFrameHeight!! == 0) {
//                videoFrameHeight = holder.itemView.displayFrameLayout.height
//            }
//            var w: Int = videoFrameWidth!!
//            var h: Int = videoFrameHeight!!
//            if (width != null && height != null && width!! > 0 && height!! > 0) {
//                if (width!! > height!!) {
//                    h = (w.toFloat() * height!! / width!!).toInt()
//                } else if (videoFrameWidth!!.toFloat() / videoFrameHeight!! > width!!.toFloat() / height!!) {
//                    h = (w.toFloat() * height!! / width!!).toInt()
//                } else {
//                    w = (videoFrameHeight!!.toFloat() * width!!.toFloat() / height!!).toInt()
//                }
//            }
//            if (w != 0 && h != 0) {
//                Log.i(TAG, "video: $width, $height")
//                Log.i(TAG, "layout: $videoFrameWidth, $videoFrameHeight")
//                Log.i(TAG, "param: $w, $h")
//                val layoutParams = holder.itemView.displayFrameLayout.layoutParams
//                layoutParams.height = h
//                layoutParams.width = w
//                holder.itemView.displayFrameLayout.layoutParams = layoutParams
//            }

        }?.origin_cover?.url_list?.let {
            GlideUtils.load(it, holder.itemView.previewImg)
        }

        if (!feedItem.region.isNullOrEmpty()) {
            holder.itemView.regionText.text = feedItem.region
        }
    }

    fun registerViewClick(viewClick: ((ViewHolder) -> Unit)) {
        this.viewClick = viewClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_main_video, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}