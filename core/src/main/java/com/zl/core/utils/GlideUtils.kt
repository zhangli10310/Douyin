package com.zl.core.utils

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zl.core.GlideApp


object GlideUtils {

    private val TAG = "GlideUtils"


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
                Handler(Looper.getMainLooper()).post {
                    load(view, list[index], this)
                }
                return true
            }
        })
    }


    fun load(view: ImageView, any: Any, listener: RequestListener<Drawable>? = null) {
        Log.i(TAG, "load: $any")
        Glide.with(view)
                .load(any)
                .listener(listener)
                .into(view)
    }


    fun loadWebp(list: List<Any>, view: ImageView) {
        if (list.isEmpty()) {
            return
        }
        var index = 0
        loadWebp(view, list[index], object : RequestListener<Drawable> {


            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                index++
                if (index >= list.size) {
                    return false
                }
                Handler(Looper.getMainLooper()).post {
                    load(view, list[index], this)
                }
                return true
            }
        })
    }


    fun loadWebp(view: ImageView, any: Any, listener: RequestListener<Drawable>? = null) {
        Log.i(TAG, "loadWebp: $any")
        val circleCrop = CenterCrop()
        GlideApp.with(view)
                .load(any)
                .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(circleCrop))
                .listener(listener)
                .into(view)
    }

}