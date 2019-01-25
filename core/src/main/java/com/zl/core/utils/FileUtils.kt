package com.zl.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.annotation.RequiresPermission
import android.util.Log
import com.zl.core.MainApp
import okhttp3.ResponseBody
import java.io.*
import java.math.BigDecimal
import java.util.HashMap

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/1/31 11:44.<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
object FileUtils {

    private val TAG = FileUtils::class.java.simpleName

    public val FOLDER = "douyin"

    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun getFile(dirName: String, fileName: String): File? {

        val popPath = File.separator + FOLDER + File.separator + dirName + File.separator

        val dir: File?
        dir = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File(Environment.getExternalStorageDirectory().absolutePath + popPath)
        } else {
            File(Environment.getRootDirectory().absolutePath + popPath)
        }

        if (!dir.exists()) {
            val mkdirs = dir.mkdirs()
            if (!mkdirs) {
                return null
            }
        }
        val file = File(dir, fileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }
        return file
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun writeResponseBodyToDisk(dirName: String, fileName: String, body: ResponseBody): Boolean {

        val futureStudioIconFile = getFile(dirName, fileName) ?: return false

        Log.i(TAG, futureStudioIconFile.absolutePath)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)

            val fileSize = body.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = body.byteStream()
            outputStream = FileOutputStream(futureStudioIconFile)

            while (true) {
                val read = inputStream!!.read(fileReader)

                if (read == -1) {
                    break
                }

                outputStream.write(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()

                Log.d(TAG, "file write: $fileSizeDownloaded of $fileSize")
            }

            outputStream.flush()

            return true
        } catch (e: IOException) {
            return false
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close()
                }

                if (outputStream != null) {
                    outputStream.close()
                }
            } catch (e: Exception) {
                Log.i(TAG, e.message)
            }

        }

    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun installApk(context: Context, dir: String, fileName: String) {
        val file = getFile(dir, fileName)
        if (file != null) {
            val intent = Intent(Intent.ACTION_VIEW)
            //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
            try {
                val command = arrayOf("chmod", "777", file.toString())
                val builder = ProcessBuilder(*command)
                builder.start()
            } catch (ignored: IOException) {
            }

            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            //            return size + "Byte";
            return "0K"
        }

        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    // 获取文件大小
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    @Throws(Exception::class)
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            if (fileList != null) {
                for (aFileList in fileList) {
                    // 如果下面还有文件
                    if (aFileList.isDirectory) {
                        size = size + getFolderSize(aFileList)
                    } else {
                        size = size + aFileList.length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (aChildren in children) {
                    val success = deleteDir(File(dir, aChildren))
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return dir.delete()
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteDir(context.externalCacheDir)
            deleteDir(File(Environment.getExternalStorageDirectory()
                    .absolutePath + File.separator + FOLDER))
//            deleteDatabases(context)
        }
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getTotalCacheSize(context: Context): String {
        var cacheSize = getFolderSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
            cacheSize += getFolderSize(File(Environment.getExternalStorageDirectory()
                    .absolutePath + File.separator + FOLDER))
        }
        return getFormatSize(cacheSize.toDouble())
    }

    fun getCacheDir(uniqueName: String): File {
        val cachePath: String

        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            //如果SD卡存在通过getExternalCacheDir()获取路径，
            cachePath = MainApp.instance.externalCacheDir.path
        } else {
            //如果SD卡不存在通过getCacheDir()获取路径，
            cachePath = MainApp.instance.cacheDir.path
        }
        //放在路径 /.../data/<application package>/cache/uniqueName
        return File(cachePath + File.separator + uniqueName)
    }

    private val MIME_MAP = object : HashMap<String, String>() {
        init {
            put(".3gp", "video/3gpp")
            put(".apk", "application/vnd.android.package-archive")
            put(".asf", "video/x-ms-asf")
            put(".avi", "video/x-msvideo")
            put(".bin", "image/bmp")
            put(".bmp", "image/bmp")
            put(".c", "text/plain")
            put(".class", "application/octet-stream")
            put(".conf", "text/plain")
            put(".cpp", "text/plain")
            put(".doc", "application/msword")
            put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            put(".xls", "application/vnd.ms-excel")
            put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(".exe", "application/octet-stream")
            put(".gif", "image/gif")
            put(".gtar", "application/x-gtar")
            put(".gz", "application/x-gzip")
            put(".h", "text/plain")
            put(".htm", "text/html")
            put(".html", "text/html")
            put(".jar", "application/java-archive")
            put(".java", "text/plain")
            put(".jpeg", "image/jpeg")
            put(".jpg", "image/jpeg")
            put(".js", "application/x-javascript")
            put(".log", "text/plain")
            put(".m3u", "audio/x-mpegurl")
            put(".m4a", "audio/mp4a-latm")
            put(".m4b", "audio/mp4a-latm")
            put(".m4p", "audio/mp4a-latm")
            put(".m4u", "video/vnd.mpegurl")
            put(".m4v", "video/x-m4v")
            put(".mov", "video/quicktime")
            put(".mp2", "audio/x-mpeg")
            put(".mp3", "audio/x-mpeg")
            put(".mp4", "video/mp4")
            put(".mpc", "application/vnd.mpohun.certificate")
            put(".mpe", "video/mpeg")
            put(".mpeg", "video/mpeg")
            put(".mpg", "video/mpeg")
            put(".mpg4", "video/mp4")
            put(".mpga", "audio/mpeg")
            put(".msg", "application/vnd.ms-outlook")
            put(".ogg", "audio/ogg")
            put(".pdf", "application/pdf")
            put(".png", "image/png")
            put(".pps", "application/vnd.ms-powerpoint")
            put(".ppt", "application/vnd.ms-powerpoint")
            put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
            put(".prop", "text/plain")
            put(".rc", "text/plain")
            put(".rmvb", "audio/x-pn-realaudio")
            put(".rtf", "application/rtf")
            put(".sh", "text/plain")
            put(".tar", "application/x-tar")
            put(".tgz", "application/x-compressed")
            put(".txt", "text/plain")
            put(".wav", "audio/x-wav")
            put(".wma", "audio/x-ms-wma")
            put(".wmv", "audio/x-ms-wmv")
            put(".wps", "application/vnd.ms-works")
            put(".xml", "text/plain")
            put(".z", "application/x-compress")
            put(".zip", "application/x-zip-compressed")
            put("", "*/*")
        }
    }

    fun getMIMEType(fName: String): String {

        var type = "*/*"
        //获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        /* 获取文件的后缀名*/
        val end = fName.substring(dotIndex, fName.length).toLowerCase()
        val s = MIME_MAP.get(end)
        if (s != null) {
            type = s
        }
        return type
    }
}