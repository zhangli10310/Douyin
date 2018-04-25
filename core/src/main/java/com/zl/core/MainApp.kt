package com.zl.core

import android.app.Application
import android.util.Log
import com.zl.core.db.AppDatabase
import com.zl.core.db.user.User

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/12 14:09.<br/>
 */
class MainApp: Application() {

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
        instance = this

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

}