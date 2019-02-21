package com.zl.douyin.ui.mainpage

import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zl.core.extend.inflate
import com.zl.core.utils.CommonUtils
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import com.zl.ijk.UriHeader
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
        Log.i(TAG, "onBindViewHolder: $position")
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
            holder.itemView.videoView.setVideoSize(width ?: 0, height ?: 0)

//            val uriList = mutableListOf<UriHeader>()
//            play_addr?.url_list?.apply {
//                for (url in this) {
//                    uriList.add(UriHeader(Uri.parse(url)))
//                }
//            }
//            holder.itemView.videoView.setUriList(uriList)

        }?.origin_cover?.url_list?.let {
            GlideUtils.load(it, holder.itemView.videoView.previewImage)
        }

        if (!feedItem.region.isNullOrEmpty()) {
            holder.itemView.regionText.text = feedItem.region
        }

        holder.itemView.videoView.isLoop = true
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

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Log.i(TAG, "onViewRecycled: ")
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.i(TAG, "onViewAttachedToWindow: ")
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.i(TAG, "onViewDetachedFromWindow: ")
//        holder.itemView.videoView.release(true)
    }
}