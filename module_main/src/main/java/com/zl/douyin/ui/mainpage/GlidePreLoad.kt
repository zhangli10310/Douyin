package com.zl.douyin.ui.mainpage

import android.content.Context
import androidx.fragment.app.Fragment
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.zl.core.GlideApp
import com.zl.core.utils.GlideUtils


class MyPreloadModelProvider(private val context: Fragment, private val list: MutableList<FeedItem>): ListPreloader.PreloadModelProvider<FeedItem> {

    override fun getPreloadRequestBuilder(item: FeedItem): RequestBuilder<*>? {
        item.video?.origin_cover?.url_list?.let {
            return GlideApp.with(context).load(it[0])
        }
        return null
    }

    override fun getPreloadItems(position: Int): MutableList<FeedItem> {
        return list
    }



}