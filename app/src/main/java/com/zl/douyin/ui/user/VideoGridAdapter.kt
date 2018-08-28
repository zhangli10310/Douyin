package com.zl.douyin.ui.user

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.zl.core.extend.inflate
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import com.zl.douyin.ui.mainpage.FeedItem
import kotlinx.android.synthetic.main.item_user_video.view.*


class VideoGridAdapter(var list: MutableList<FeedItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_user_video, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list[position].video?.dynamic_cover?.url_list?.let {
            it.forEach {
                "$it.webp"
            }
            GlideUtils.load(it, holder.itemView.dynamicCoverImg)
        }
    }
}