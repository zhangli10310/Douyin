package com.zl.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.multidex.MultiDexApplication
import android.util.Log
import com.ss.android.common.applog.GlobalContext
import com.zl.core.db.AppDatabase
import com.zl.core.db.user.User
import com.zl.core.utils.SPUtils
import com.zl.core.utils.SystemInfoUtils
import java.util.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/12 14:09.<br/>
 */
class MainApp: MultiDexApplication() {

    var currentUserId: Int = 0
    var user: User? = null
        set(value) {
            field = value
            if (value != null) {
                AppDatabase.getInstance().userDao().insertUser(value)
            }
        }

    companion object {
        lateinit var instance: MainApp
            private set
    }

    override fun onCreate() {
        super.onCreate()

//        Hook.hookAMS { proxy, method, args ->
//            Log.i("hookAMS", "hook: ${method.name}")
//        }

        Hook.hookPMS(this) { proxy, method, args ->
            Log.i("hookPMS", method.name)
            if ("getPackageInfo" == method.name) {

            }
        }

        instance = this
        GlobalContext.setContext(this)

//        Thread.setDefaultUncaughtExceptionHandler { t, e ->
//            Log.i("error_by_", "${t.name}, ${e.message}")
//        }
    }

//    fun gotoLogin() {
//        val intent = Intent(Router.ACTION_LOGIN)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//        try {
//            startActivity(intent)
//        } catch (e: Exception) {
//
//        }
//    }

    fun getVersion(): String {
        return try {
            val info = packageManager.getPackageInfo(packageName, 0)
            info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            "0.0"
        }
    }

    fun getVersionCode(): Int {
        return try {
            val info = packageManager.getPackageInfo(packageName, 0)
            info.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }

    @SuppressLint("MissingPermission")
    fun getUUID(): String {
        var uuid:String? = null
        try {
            uuid = SystemInfoUtils.getIMEI(this)
        } catch (e: Exception){
            uuid = SPUtils.getString("uuid")
        }
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            SPUtils.saveString("uuid", uuid)
        }
        return uuid
    }

    fun getBaseUrl(): String {

        return if (isInternational()) {
            BuildConfig.INTERNATIONAL_URL
        } else {
            BuildConfig.API_URL
        }
    }

    fun isInternational(): Boolean { // 是否是国际版
        return false
    }

    fun getRegionCode(): String {
        return "JP"
    }

    fun getCarrierRegionCode(): String {
        return "ja"
    }

}