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
class AddInternationalBaseParamInterceptor : Interceptor {

    companion object {
        private val TAG = AddInternationalBaseParamInterceptor::class.java.simpleName
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

        return builder.build()
    }

    private fun addQuery(request: Request): Request {

        val currentTimeMillis = System.currentTimeMillis()
        val httpUrlBuilder = request.url().newBuilder()
        httpUrlBuilder

                .addQueryParameter("ad_user_agent", "Dalvik/2.1.0 (Linux; U; Android 6.0.1; ATH-AL00 Build/HONORATH-AL00)")
                .addQueryParameter("retry_type", "no_retry")
                .addQueryParameter("app_language", MainApp.instance.getCarrierRegionCode())
                .addQueryParameter("language", "en")
                .addQueryParameter("region", "en")
                .addQueryParameter("sys_region", "US")
                .addQueryParameter("carrier_region", MainApp.instance.getCarrierRegionCode())
                .addQueryParameter("carrier_region_v2", "")
                .addQueryParameter("build_number", "3.1.2")
                .addQueryParameter("time_zone_name", "Asia/Taipei")
                .addQueryParameter("time_zone_offset", "28800")
                .addQueryParameter("mcc_mnc", "")
                .addQueryParameter("is_my_cn", "1")
                .addQueryParameter("fp", "c2T_PzQMLzsqFlqrPlU1LSFeFSUI")
                .addQueryParameter("iid", "6623657744081815297") //fixme
                .addQueryParameter("device_id", "6623657680592504321") //fixme
                .addQueryParameter("ac", SystemInfoUtils.getNetState(MainApp.instance))
                .addQueryParameter("channel", "googleplay")
                .addQueryParameter("aid", "1180") //fixme
                .addQueryParameter("version_code", "${MainApp.instance.getVersionCode()}")
                .addQueryParameter("version_name", MainApp.instance.getVersion())
                .addQueryParameter("ts", "${currentTimeMillis / 1000}")
                .addQueryParameter("app_type", "normal")
                .addQueryParameter("os_api", SystemInfoUtils.getSystemVersionId().toString())
                .addQueryParameter("device_type", SystemInfoUtils.getSystemModel())
                .addQueryParameter("device_platform", "android")
                .addQueryParameter("ssmix", "a") //fixme
                .addQueryParameter("app_name", "trill")
                .addQueryParameter("os_version", SystemInfoUtils.getSystemVersion())
                .addQueryParameter("manifest_version_code", "${MainApp.instance.getVersionCode()}")
                .addQueryParameter("dpi", SystemInfoUtils.getSystemDpi().toString())
                .addQueryParameter("openudid", SystemInfoUtils.getDeviceCode(MainApp.instance))
                .addQueryParameter("resolution", "${SystemInfoUtils.getScreenWidthAndHeight()[0]}*${SystemInfoUtils.getScreenWidthAndHeight()[1]}")
                .addQueryParameter("device_brand", SystemInfoUtils.getDeviceBrand())
                .addQueryParameter("update_version_code", "3120") //fixme
                .addQueryParameter("_rticket", "$currentTimeMillis")

                .addQueryParameter("as", "a1b5ff9e3440eb5a5b4355")
                .addQueryParameter("cp", "fd06b15248bbeeabe1QyYc")
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