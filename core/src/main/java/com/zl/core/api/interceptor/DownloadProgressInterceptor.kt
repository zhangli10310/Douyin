package com.zl.core.api.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/31 11:11.<br/>
 */
class DownloadProgressInterceptor(private var listener: ((bytesRead: Long, contentLength: Long, done: Boolean)->Unit)?) : Interceptor {

    private val TAG = "DownloadInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        Log.i(TAG, request.url().toString())
        val originalResponse = chain.proceed(request)

        val body = originalResponse.body() ?: return originalResponse
        return originalResponse.newBuilder()
                .body(DownloadProgressResponseBody(body, listener))
                .build()

    }

    inner class DownloadProgressResponseBody(private val responseBody: ResponseBody,
                                             private val progressListener: ((bytesRead: Long, contentLength: Long, done: Boolean)->Unit)?) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()))
            }
            return bufferedSource!!
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                internal var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                    progressListener?.invoke(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                    return bytesRead
                }
            }

        }
    }
}