package com.zl.core.api.interceptor

import android.util.Log
import com.ss.android.common.applog.Verify
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
        val addBodyParamReq = addBodyParam(addQuery(request))
        val addHeaderReq = addHeader(addBodyParamReq)
        return chain.proceed(addHeaderReq)
    }

    private fun addHeader(request: Request): Request {

        val builder = request.newBuilder()
                .addHeader("source", "android")
                .header("User-Agent", "com.ss.android.ugc.aweme/321 (Linux; U; Android 6.0.1; zh_CN; ATH-AL00; Build/HONORATH-AL00; Cronet/58.0.2991.0)")

        return builder.build()
    }

    private fun addQuery(request: Request): Request {

        val currentTimeMillis = System.currentTimeMillis()
        val httpUrlBuilder = request.url().newBuilder()
        httpUrlBuilder
                .addQueryParameter("ts", "${currentTimeMillis / 1000}")
                .addQueryParameter("app_type", "normal")
                .addQueryParameter("os_api", SystemInfoUtils.getSystemVersionId().toString())
                .addQueryParameter("device_type", SystemInfoUtils.getSystemModel())
                .addQueryParameter("device_platform", "android")
                .addQueryParameter("ssmix", "a") //fixme
                .addQueryParameter("iid", "31628563673") //fixme
                .addQueryParameter("manifest_version_code", "${MainApp.instance.getVersionCode()}")
                .addQueryParameter("dpi", SystemInfoUtils.getSystemDpi().toString())
                .addQueryParameter("uuid", MainApp.instance.getUUID())
                .addQueryParameter("version_code", "${MainApp.instance.getVersionCode()}")
                .addQueryParameter("app_name", "aweme")
                .addQueryParameter("version_name", MainApp.instance.getVersion())
                .addQueryParameter("openudid", SystemInfoUtils.getDeviceCode(MainApp.instance))
                .addQueryParameter("device_id", "46610181403") //fixme
                .addQueryParameter("resolution", "${SystemInfoUtils.getScreenWidthAndHeight()[0]}*${SystemInfoUtils.getScreenWidthAndHeight()[1]}")
                .addQueryParameter("os_version", SystemInfoUtils.getSystemVersion())
                .addQueryParameter("language", SystemInfoUtils.getSystemLanguage())
                .addQueryParameter("device_brand", SystemInfoUtils.getDeviceBrand())
                .addQueryParameter("ac", SystemInfoUtils.getNetState(MainApp.instance))
                .addQueryParameter("update_version_code", "1810") //fixme
                .addQueryParameter("aid", "1128") //fixme
                .addQueryParameter("channel", "update") //fixme
                .addQueryParameter("_rticket", "$currentTimeMillis")

                .addQueryParameter("as", "a1qwert123")
                .addQueryParameter("cp", "cbfhckdckkde1")
//                .addQueryParameter("mas", "")

        val httpUrl = httpUrlBuilder.build()

//        Verify.getParam((currentTimeMillis/1000).toInt(), httpUrl.toString())
//
        Log.i(TAG, "addQuery: ${httpUrl.toString()}")
        val builder = request.newBuilder()
                .url(httpUrl)

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