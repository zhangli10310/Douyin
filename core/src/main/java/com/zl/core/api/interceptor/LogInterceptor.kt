package com.zl.core.api.interceptor

import android.util.Log
import com.zl.core.utils.DateUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2017/11/28 11:49.<br/>
 */
class LogInterceptor : Interceptor {

    companion object {
        private val TAG = LogInterceptor::class.java.simpleName
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        log(request)

        val response = chain.proceed(request)
        log(response)

        return response
    }

    private fun log(request: Request) {
        Log.i(TAG, "req.time:${DateUtils.timeMillisToDateString(System.currentTimeMillis(), "HH:mm:ss")} ")
        Log.i(TAG, "${request.method()}:${request.url()}")

        val headers = request.headers()
        for (name in headers.names()) {
            Log.i(TAG, "req.header->$name:${headers[name]}")
        }

        val body = request.body()
        if (body != null) {
            val buffer = Buffer()
            body.writeTo(buffer)

            var charset = Charset.forName("UTF-8")
            val contentType = body.contentType()
            if (contentType != null) {
                charset = contentType.charset(charset)
                Log.i(TAG, "req.contentType: $contentType")
                Log.i(TAG, "req.charset: $charset")
            }

            Log.i(TAG, "req.body:${buffer.readString(charset)} ")

        }
    }

    private fun log(response: Response) {
        Log.i(TAG, "resp.time:${DateUtils.timeMillisToDateString(System.currentTimeMillis(), "HH:mm:ss")} ")
        val headers = response.headers()
        for (name in headers.names()) {
            Log.i(TAG, "resp.header->$name:${headers[name]}")
        }

        val body = response.body()
        if (body != null) {
            val contentLength = body.contentLength()

            val source = body.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()

            var charset = Charset.forName("UTF-8")
            val contentType = body.contentType()
            if (contentType != null) {
                charset = contentType.charset(charset)
            }

            if (contentLength != 0L) {
                Log.i(TAG, "resp.body: ${buffer.clone().readString(charset)}")
            }
        }
    }

}