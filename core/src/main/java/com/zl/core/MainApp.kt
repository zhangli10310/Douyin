package com.zl.core

import android.annotation.SuppressLint
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import com.squareup.leakcanary.LeakCanary
import com.ss.android.common.applog.GlobalContext
import com.zl.core.db.AppDatabase
import com.zl.core.db.user.User
import com.zl.core.utils.SPUtils
import com.zl.core.utils.SystemInfoUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer
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

        BlockCanary.install(this, object : BlockCanaryContext(){

            override fun provideBlockThreshold() = 500
        }).start()

        LeakCanary.install(this)

        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this) // 尽可能早，推荐在Application中初始化

        IjkMediaPlayer.loadLibrariesOnce(null)
    }

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