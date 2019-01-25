package com.zl.core.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.annotation.RequiresPermission
import android.hardware.usb.UsbDevice.getDeviceId
import android.app.Activity
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.icu.util.ULocale.getLanguage
import java.util.*
import android.util.DisplayMetrics
import com.zl.core.MainApp


/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/30 14:17.<br/>
 */
object SystemInfoUtils {

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val connManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        if (null != networkInfo) {
            // 获取当前的网络连接是否可用
            return networkInfo.isAvailable
        }
        return false
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetState(context: Context): String {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.activeNetworkInfo
        if (null != networkInfo) {
            if (networkInfo.typeName == "WIFI") {
                return "wifi"
            } else if (networkInfo.typeName == "MOBILE") {
                return networkInfo.extraInfo
            }
        }
        return "null"
    }

    @SuppressLint("HardwareIds")
    fun getDeviceCode(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    fun getSystemVersion(): String {
        return android.os.Build.VERSION.RELEASE
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    fun getSystemVersionId(): Int {
        return android.os.Build.VERSION.SDK_INT
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    fun getSystemModel(): String {
        return android.os.Build.MODEL
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    fun getDeviceBrand(): String {
        return android.os.Build.BRAND
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getIMEI(ctx: Context): String? {
        val tm = ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
        return tm.deviceId
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    fun getSystemLanguage(): String {
        return Locale.getDefault().getLanguage()
    }

    /**
     * 获取当前手机系统dpi
     */
    fun getSystemDpi(): Int {
        val metrics = MainApp.instance.resources.displayMetrics
        return metrics.densityDpi
    }

    /**
     * 获取当前手机系统dpi
     */
    fun getScreenWidthAndHeight(): IntArray {
        val metrics = MainApp.instance.resources.displayMetrics
        return intArrayOf(metrics.widthPixels, metrics.heightPixels)
    }
}