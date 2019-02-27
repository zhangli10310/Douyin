package com.zl.douyin.ui.user

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zl.core.GlideApp
import com.zl.core.extend.inflate
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import com.zl.douyin.ui.mainpage.FeedItem
import kotlinx.android.synthetic.main.item_user_video.view.*


class VideoGridAdapter(var list: MutableList<FeedItem>, var onItemClick: ((RecyclerView.ViewHolder) -> Unit)? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_user_video, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list[position].video?.dynamic_cover?.url_list?.let { urls ->

            val list = mutableListOf<String>()
            urls.forEach {
                if (!it.endsWith("jpeg")) {
                    list.add("$it.webp")
                }
            }
            GlideUtils.loadWebp(list, holder.itemView.dynamicCoverImg)
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(holder)
        }
    }
}