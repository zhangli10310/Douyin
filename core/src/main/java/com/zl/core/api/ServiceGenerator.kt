package com.zl.core.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.zl.core.BuildConfig
import com.zl.core.MainApp
import com.zl.core.api.interceptor.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.ArrayList
import javax.net.ssl.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2017/11/28 10:46.<br/>
 */
object ServiceGenerator {

    private val TAG = ServiceGenerator::class.java.simpleName

    fun <T> createRxService(clazz: Class<T>, baseUrl: String = MainApp.instance.getBaseUrl()): T {

        Log.i(TAG, "createService: url=$baseUrl")

        val build = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return build.create(clazz)
    }

    fun <T> createLiveDataService(clazz: Class<T>, baseUrl: String = MainApp.instance.getBaseUrl()): T {

        Log.i(TAG, "createService: url=$baseUrl")

        val build = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()

        return build.create(clazz)
    }

    fun <T> createDownloadService(clazz: Class<T>, listener: ((bytesRead: Long, contentLength: Long, done: Boolean) -> Unit)? = null): T {

        val client = OkHttpClient.Builder()
                .addInterceptor(DownloadProgressInterceptor(listener))
                .addInterceptor(ConnectErrorInterceptor())
                .build()

        val build = Retrofit.Builder()
                .baseUrl(MainApp.instance.getBaseUrl())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return build.create(clazz)
    }

    private fun getClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (MainApp.instance.isInternational()) {
            builder.addInterceptor(AddInternationalBaseParamInterceptor())
        } else {
            builder.addInterceptor(AddBaseParamInterceptor())
        }

        builder.addInterceptor(LogInterceptor())
                .addInterceptor(ConnectErrorInterceptor())

        try {
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

            }
            sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            builder.sslSocketFactory(sslContext.socketFactory)

        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
        builder.hostnameVerifier({ _, _ -> true })
        return builder.build()
    }

    private class NotNullListTypeAdapterFactory : TypeAdapterFactory {

        override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {

            val adapter = gson.getDelegateAdapter(this, type)
            val rawType = type.rawType

            return object : TypeAdapter<T>() {

                @Throws
                override fun read(inn: JsonReader): T {
                    if (rawType == object : TypeToken<List<*>>() {

                            }.rawType) {
                        if (inn.peek() == JsonToken.NULL) {
                            inn.nextNull()
                            return ArrayList<Any>() as T
                        }
                    }
                    return adapter.read(inn)
                }

                override fun write(outt: JsonWriter?, value: T) {
                    adapter.write(outt, value)
                }

            }
        }
    }
}