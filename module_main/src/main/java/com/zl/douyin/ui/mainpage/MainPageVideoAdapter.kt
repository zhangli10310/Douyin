package com.zl.douyin.ui.mainpage

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            holder.itemView.videoView.setVideoSize(width ?: 0, height ?: 0)

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
}