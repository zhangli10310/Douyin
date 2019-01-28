package com.zl.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 *
 *<p>跳转路由</p>
 *
 * Created by zhangli on 2018/2/8 17:12.<br/>
 */
object Router {

    private val TAG = Router::class.java.simpleName

    const val MAIN_ACTIVITY = "/main/main"
    const val LIVE_ACTIVITY = "/live/room"

    fun toFunc(context: Context, url: String) {
        when {
            url.startsWith("http") -> {
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra(WebActivity.URL, url)
                context.startActivity(intent)
            }
            url.startsWith("action") -> {
                val intent = Intent(url)
                context.startActivity(intent)
            }
            else -> {
                try {
                    val clazz = Class.forName(url)
                    if (Activity::class.java.isAssignableFrom(clazz)) {
                        context.startActivity(Intent(context, clazz))
                    } else {
                        Log.i(TAG, "toFunc: error")
                    }
                } catch (e: Exception) {
                    Log.i(TAG, "toFunc: $e")
                }
            }
        }
    }
}