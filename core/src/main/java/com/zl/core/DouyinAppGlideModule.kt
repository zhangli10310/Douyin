package com.zl.core

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * Created by zhangli on 2018/8/28,22:28<br/>
 */
@GlideModule
class DouyinAppGlideModule:AppGlideModule() {


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(object :X509TrustManager{
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

        }), SecureRandom())
        val client = OkHttpClient().newBuilder()
                .sslSocketFactory(sslContext.socketFactory)
                .hostnameVerifier { _, _ ->
                    true
                }
                .build()

        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))

    }
}