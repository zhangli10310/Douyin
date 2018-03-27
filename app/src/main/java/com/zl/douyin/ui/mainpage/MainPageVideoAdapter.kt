package com.zl.douyin.ui.mainpage

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zl.core.extend.inflate
import com.zl.douyin.R
import kotlinx.android.synthetic.main.item_main_video.view.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/14 17:19.<br/>
 */
class MainPageVideoAdapter(private var list: MutableList<VideoEntity>) : RecyclerView.Adapter<MainPageVideoAdapter.ViewHolder>() {

    private val TAG = MainPageVideoAdapter::class.java.simpleName

    private var viewClick: ((ViewHolder) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = list[position].url

        holder.itemView.videoView.setVideoURI(Uri.parse(url))
        holder.itemView.videoView.setOnPreparedListener {
            holder.itemView.videoView.start()
        }
        viewClick?.invoke(holder)
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