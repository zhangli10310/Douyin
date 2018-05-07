package com.ss.android.common.applog

import android.util.Log

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/27 15:20.<br/>
 */
object Verify {

    private val TAG = Verify::class.java.simpleName

    // Used to load the 'native-lib' library on application startup.
    init {
        try {
            System.loadLibrary("userinfo")
            UserInfo.setAppId(2)
            UserInfo.initUser("a3668f0afac72ca3f6c1697d29e0e1bb1fef4ab0285319b95ac39fa42c38d05f")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getParam(time: Int, url: String): String {
        return ""
    }
}