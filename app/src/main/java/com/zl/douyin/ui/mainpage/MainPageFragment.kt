package com.zl.douyin.ui.mainpage

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import com.zl.core.base.ModeFragment
import com.zl.douyin.R
import kotlinx.android.synthetic.main.fragment_main_page.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/14 15:33.<br/>
 */
class MainPageFragment : ModeFragment() {

    private val TAG = MainPageFragment::class.java.simpleName

    private var list: MutableList<VideoEntity> = mutableListOf()
    private lateinit var mAdapter: MainPageVideoAdapter

    override fun layoutId() = R.layout.fragment_main_page

    override fun initView(savedInstanceState: Bundle?) {

        recyclerView.layoutManager = LinearLayoutManager(activity)
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        mAdapter = MainPageVideoAdapter(list)
        recyclerView.adapter = mAdapter
    }

    override fun setListener() {

    }

    override fun observe() {

    }

    override fun afterView() {
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://api.amemv.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://aweme.snssdk.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        list.add(VideoEntity("https://api.amemv.com/aweme/v1/play/?video_id=cb04291f17fd423daa6a81f42fe41d76&line=1&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0"))
        mAdapter.notifyDataSetChanged()
    }

    fun refresh() {
        swipeRefreshLayout.isRefreshing = true
    }

}