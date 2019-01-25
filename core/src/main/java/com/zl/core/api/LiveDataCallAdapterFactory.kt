package com.zl.core.api

import androidx.lifecycle.LiveData
import android.util.Log
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 *<p></p>
 *
 * Created by zhangli.<br/>
 */
class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    companion object {
        private val TAG = LiveDataCallAdapterFactory::class.java.simpleName
    }

    override fun get(returnType: Type, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType) != LiveData::class.java) {
            return null
        }

        //returnType是原始写的返回类型，下面可以获取returnType中的泛型
        val observableType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)

//        val rawObservableType = CallAdapter.Factory.getRawType(observableType)
//        if (rawObservableType != ApiResponse::class.java) {
//            throw IllegalArgumentException("type must be a resource")
//        }

        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }

        //再次调用下面的方法能继续获取泛型
//        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Any>(observableType)
    }

    class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<R>> {

        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<R>): LiveData<R> {
            return object : LiveData<R>() {
                internal var started = AtomicBoolean(false)
                override fun onActive() {
                    super.onActive()
                    if (started.compareAndSet(false, true)) {

//                        postValue(call.execute().body())
                        call.enqueue(object : Callback<R> {
                            override fun onResponse(call: Call<R>, response: Response<R>) {
                                val body = response.body()
                                postValue(body)
                            }

                            override fun onFailure(call: Call<R>, throwable: Throwable) {
//                                postValue(throwable)
                                Log.i(TAG, "onFailure: ${throwable.message}")
                            }
                        })
                    }
                }
            }
        }
    }

}