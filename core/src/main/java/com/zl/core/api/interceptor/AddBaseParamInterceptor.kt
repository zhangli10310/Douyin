package com.zl.core.api.interceptor

import android.util.Log
import com.zl.core.MainApp
import com.zl.core.utils.SystemInfoUtils
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import java.nio.charset.Charset

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/30 14:40.<br/>
 */
class AddBaseParamInterceptor : Interceptor {

    companion object {
        private val TAG = AddBaseParamInterceptor::class.java.simpleName
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        //顺序 先addBodyParam再addHeader
        val addBodyParamReq = addBodyParam(request)
        val addHeaderReq = addHeader(addBodyParamReq)
        return chain.proceed(addHeaderReq)
    }

    private fun addHeader(request: Request): Request {

        val builder = request.newBuilder()
                .addHeader("source", "android")

        return builder.build()
    }

    private fun addBodyParam(request: Request): Request {

        val body = request.body()

        if (body != null) {
            val buffer = Buffer()
            body.writeTo(buffer)

            var charset = Charset.forName("UTF-8")
            val contentType = body.contentType()
            if (contentType != null) {
                charset = contentType.charset(charset)
                if (contentType.type() == "application" && contentType.subtype() == "json") {
                    val json = buffer.readString(charset)

                    val requestBody = RequestBody.create(body.contentType(), addBaseParam(json))
                    return request.newBuilder()
                            .post(requestBody)
                            .build()
                }
            }
        }
        return request
    }

    private fun addBaseParam(json: String): String {
        try {
            val jsonObject = JSONObject(json)
            addBaseParam(jsonObject)
            return jsonObject.toString()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
        return json
    }

    private fun addBaseParam(json: JSONObject) {
        json.put("deviceCode", SystemInfoUtils.getDeviceCode(MainApp.instance))
        json.put("versionCode", MainApp.instance.getVersionCode())
    }
}