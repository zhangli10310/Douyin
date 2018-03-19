package com.zl.douyin.ui.user

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zl.core.extend.inflate
import com.zl.douyin.R

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/19 16:12.<br/>
 */
class UserVideoAdapter : RecyclerView.Adapter<UserVideoAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_user_video, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = 30

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}