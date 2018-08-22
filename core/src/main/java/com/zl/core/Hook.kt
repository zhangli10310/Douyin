package com.zl.core

import android.content.Context
import android.content.Intent
import android.util.Log
import java.lang.reflect.AccessibleObject.setAccessible
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.AccessibleObject.setAccessible
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/4/27 15:57.<br/>
 */
object Hook {

    fun hookAMS(handler: (Any, Method, Array<Any>?)->Unit) {

        try {
            val activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative")
            val gDefaultField = activityManagerNativeClazz.getDeclaredField("gDefault")
            gDefaultField.isAccessible = true
            val gDefault = gDefaultField.get(null) // 获取gDefault实例，由于是静态类型的属性，所以这里直接传递null参数

            // 下面通过反射执行gDefault.get();操作，最终返回IActivityManager，也就是ActivityManagerService的实例
            val singleTonClazz = Class.forName("android.util.Singleton")
            val mInstanceField = singleTonClazz.getDeclaredField("mInstance")
            mInstanceField.isAccessible = true
            val iActivityManager = mInstanceField.get(gDefault)

            val iActivityManagerClazz = Class.forName("android.app.IActivityManager")
            // 指定被代理对象的类加载器
            // 指定被代理对象所实现的接口，这里就是代理IActivityManager
            // 表示这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上
            val myProxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(iActivityManagerClazz)) { proxy, method, args ->
                handler.invoke(proxy, method, args)
                return@newProxyInstance method.invoke(iActivityManager, *(args ?: arrayOfNulls<Any>(0)))
            }
            mInstanceField.set(gDefault, myProxy)


//            val activityManagerClazz = Class.forName("android.app.ActivityManager")
//            val gDefault = activityManagerClazz.getDeclaredField("IActivityManagerSingleton")
//            val singleTonClazz = Class.forName("android.util.Singleton")
//            val mInstanceField = singleTonClazz.getDeclaredField("mInstance")
//            mInstanceField.isAccessible = true
//            val iActivityManager = mInstanceField.get(gDefault)
//
//            val iActivityManagerClazz = Class.forName("android.app.IActivityManager")
//            val myProxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(iActivityManagerClazz), AMSProxy(iActivityManager))
//            mInstanceField.set(gDefault, myProxy)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hookPMS(ctx: Context, handler: (Any, Method, Array<Any>?)->Unit) {
        val activityThreadClazz = Class.forName("android.app.ActivityThread")

// 1.1 获取当前ActivityThread实例
        val currentActivityThreadMethod = activityThreadClazz.getDeclaredMethod("currentActivityThread")
        val activityThreadObj = currentActivityThreadMethod.invoke(null)

// 1.2 获取sPackageManager静态属性的值
        val sPackageManagerField = activityThreadClazz.getDeclaredField("sPackageManager")
        sPackageManagerField.isAccessible = true
        val sPackageManagerObj = sPackageManagerField.get(activityThreadObj)

// 2. 创建一个代理，然后将sPackageManager传递进入代理
        val iPackageManagerClazz = Class.forName("android.content.pm.IPackageManager")
        val pmsProxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(iPackageManagerClazz)){ proxy, method, args ->
            handler.invoke(proxy, method, args)
            return@newProxyInstance method.invoke(sPackageManagerObj, *(args ?: arrayOfNulls<Any>(0)))
        }

// 3. 重新为当前的ActivityThread对象设置sPackageManager
        sPackageManagerField.set(activityThreadObj, pmsProxy)

// 4. 替换ApplicationPackageManager中的mPM对象
        val packageManager = ctx.applicationContext.packageManager
        val applicationPackageManagerClazz = Class.forName("android.app.ApplicationPackageManager")
        val mPMField = applicationPackageManagerClazz.getDeclaredField("mPM")
        mPMField.isAccessible = true
        mPMField.set(packageManager, pmsProxy)

    }

    fun hookWindowManagerGlobal() {

        try {
            val windowManagerGlobalClazz = Class.forName("android.view.WindowManagerGlobal")
            val getInstanceMethod = windowManagerGlobalClazz.getDeclaredMethod("getInstance")
            getInstanceMethod.isAccessible = true
            val windowManagerGlobalInstance = getInstanceMethod.invoke(windowManagerGlobalClazz)


            val mViewsField = windowManagerGlobalClazz.getDeclaredField("mViews")
            mViewsField.isAccessible = true
            val mViews = mViewsField.get(windowManagerGlobalInstance)
            mViewsField.set(windowManagerGlobalInstance, object : ArrayList<View>() {

                override fun add(element: View): Boolean {
//                    if (element is ViewGroup) {
//                        hook(element)
//                    }

                    return super.add(element)
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}