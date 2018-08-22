package com.zl.core.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

object GlideUtils {


    fun load(list: List<Any>, view: ImageView) {
        if (list.isEmpty()) {
            return
        }
        var index = 0
        load(view, list[index], object : RequestListener<Drawable> {

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                index++
                if (index >= list.size) {
                    return false
                }
                load(view, list[index], this)
                return true
            }
        })
    }


    fun load(view: ImageView, any: Any, listener: RequestListener<Drawable>? = null) {
        Glide.with(view)
                .load(any)
                .listener(listener)
                .into(view)
    }

}