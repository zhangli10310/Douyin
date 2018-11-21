package com.zl.douyin.ui.comment

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zl.core.extend.inflate
import com.zl.core.utils.CommonUtils
import com.zl.core.utils.DateUtils
import com.zl.core.utils.GlideUtils
import com.zl.douyin.R
import com.zl.douyin.ui.mainpage.MainPageVideoAdapter
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentAdapter(private var list: MutableList<CommentItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_comment, parent, false)
        return MainPageVideoAdapter.ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val commentItem = list[position]


        holder.itemView.commentText.text = commentItem.text

        commentItem.user?.let {
            it.avatar_thumb?.url_list?.let {
                GlideUtils.load(it, holder.itemView.headImg)
            }
            holder.itemView.nameText.text = "@${it.nickname}"
        }

        commentItem.create_time?.let {
            holder.itemView.timeText.text = DateUtils.timeToCurrent(it * 1000L)
        }

        commentItem.digg_count?.let {
            holder.itemView.likeCountText.text = CommonUtils.formatCount(it.toLong())
        }

        val reply = commentItem.reply_comment
        if (reply == null || reply.isEmpty()) {
            holder.itemView.replyLayout.visibility = View.GONE
        } else {
            holder.itemView.replyLayout.visibility = View.VISIBLE

            holder.itemView.replyNameText.text = reply[0].user?.nickname
            holder.itemView.replyContentText.text = reply[0].text
        }
    }
}