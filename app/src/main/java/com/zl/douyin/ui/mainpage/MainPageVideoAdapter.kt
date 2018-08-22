package com.zl.douyin.ui.mainpage

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.zl.core.extend.inflate
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import com.zl.ijk.media.IMediaController
import com.zl.ijk.media.IRenderView
import kotlinx.android.synthetic.main.item_main_video.view.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer

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

//        val url = list[position].url
//
//        Log.w(TAG, "onBindViewHolder: ${holder.itemView.videoView}")
//        holder.itemView.videoView.setOnPreparedListener {
//            Log.e(TAG, "onBindViewHolder: OnPrepared")
//        }
//        holder.itemView.videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT)
//        holder.itemView.videoView.setOnInfoListener { iMediaPlayer, i, j ->
//            Log.e(TAG, "video: ${iMediaPlayer.videoHeight}, ${iMediaPlayer.videoWidth}, $i, $j")
//            Log.e(TAG, "videoView: ${holder.itemView.videoView.height}, ${holder.itemView.videoView.width}")
////            holder.itemView.videoView.toggleAspectRatio()
//            return@setOnInfoListener true
//        }
//        holder.itemView.videoView.setVideoURI(Uri.parse(url))
//        holder.itemView.videoView.start()

        feedItem.music?.let {
            holder.itemView.musicText.text = it.title + "-" + it.owner_nickname
        }

        holder.itemView.descText.text = feedItem.desc

        feedItem.author?.let {
            holder.itemView.authorText.text = "@${it.nickname}"
        }

        feedItem.statistics?.let {
            holder.itemView.commentCountText.text = convertCount(it.comment_count)
            holder.itemView.shareCountText.text = convertCount(it.share_count)
            holder.itemView.likeCountText.text = convertCount(it.digg_count)
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

        }?.origin_cover?.url_list?.let {
            GlideUtils.load(it, holder.itemView.previewImg)
        }
    }

    private fun convertCount(number: Long?): String {
        return when (number) {
            null -> {
                "0"
            }
            in 0..9999 -> {
                number.toString()
            }
            in 10000..99999 -> {
                (number / 10000).toString() + "." + (number / 1000 % 10).toString() + "w"
            }
            else -> {
                (number / 10000).toString() + "w"
            }
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