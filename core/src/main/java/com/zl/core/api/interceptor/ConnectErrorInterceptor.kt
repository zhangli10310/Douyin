package com.zl.core.api.interceptor

import com.zl.core.MainApp
import com.zl.core.utils.SystemInfoUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/30 11:53.<br/>
 */
class ConnectErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!SystemInfoUtils.isNetworkAvailable(MainApp.instance)) {
            throw RuntimeException("无网络连接")
        }

        try {
            return chain.proceed(request)
        } catch (e: Exception) {
            if (e is SocketTimeoutException) {
                throw RuntimeException("连接超时，请检查网络环境")
            } else if (e is UnknownHostException) {
                var message: String? = e.message
                if (message != null) {
                    val split = message.split("\"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (split.size >= 2) {
                        message = split[1]
                    }
                }
                throw RuntimeException("未知的域名:" + message!!)
            }
            throw RuntimeException("网络异常，请检查网络," + e)
        }

    }
}