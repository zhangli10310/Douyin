package com.zl.core.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import android.support.annotation.RequiresPermission

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/30 14:17.<br/>
 */
object SystemInfoUtils {

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val connManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        if (null != networkInfo) {
            // 获取当前的网络连接是否可用
            return networkInfo.isAvailable
        }
        return false
    }

    @SuppressLint("HardwareIds")
    fun getDeviceCode(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}