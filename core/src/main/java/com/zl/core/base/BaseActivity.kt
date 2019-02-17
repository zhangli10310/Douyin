package com.zl.core.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.zl.core.dialog.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.collections.HashSet


/**
 *
 *<p></p>
 *
 * Created by zhangli<br/>
 */
open class BaseActivity : AppCompatActivity() {

    private val TAG = BaseActivity::class.java.simpleName

    private val PERMISSION_REQUEST_CODE = 0x1001
    private var mPermissionReturn: ((Boolean) -> Unit)? = null

    private var mToast: Toast? = null
    private var mProgressDialog: Dialog? = null

    protected val mDisposable = CompositeDisposable()
    protected val mDialogSet = HashSet<Dialog>()
    private val onTouchListenerList: MutableList<OnTouchListener> = mutableListOf()

    protected val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
//            window.navigationBarColor = ContextCompat.getColor(this, R.color.bg)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        ARouter.getInstance().inject(this)
    }

    @SuppressLint("ShowToast")
    fun showToastSafe(text: CharSequence, gravity: Int = Gravity.BOTTOM, xOffset: Int = 0, yOffset: Int = 0) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(text)
        }
        mToast!!.setGravity(gravity, xOffset, yOffset)
        runOnUiThread {
            mToast!!.show()
        }
    }

    /**
     * 不要频繁调用，Timer有内存问题
     */
    @SuppressLint("ShowToast")
    fun showToastTime(text: CharSequence, duration: Int = 2000, gravity: Int = Gravity.BOTTOM, xOffset: Int = 0, yOffset: Int = 0) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(text)
        }
        mToast!!.setGravity(gravity, xOffset, yOffset)
        runOnUiThread {
            showToastTimeSafe(mToast!!, duration)
        }
    }

    private fun showToastTimeSafe(toast: Toast, delay: Int) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                toast.show()
            }
        }, 0, 2000)

        mHandler.postDelayed({
            toast.cancel()
            timer.cancel()
        }, delay.toLong())
    }

    fun showLoading() {
        runOnUiThread {
            if (mProgressDialog == null) {
                mProgressDialog = LoadingDialog(this@BaseActivity)
                mProgressDialog!!.setCanceledOnTouchOutside(false)
            }
            mProgressDialog!!.show()
        }
    }

    fun hideLoading() {
        runOnUiThread {
            if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                mProgressDialog!!.dismiss()
            }
        }
    }

    /**
     * activity界面内的ProgressBarId，不阻塞页面操作的加载提示 [loadingInner()]
     */
    open protected fun loadingProgressBarId() = 0

    /**
     * activity界面内的ProgressBarId，不阻塞页面操作的加载提示 [loadingProgressBarId()]
     */
    open fun loadingInner() {
        if (loadingProgressBarId() == 0) {
            return
        }
        val progressBar = findViewById<View>(loadingProgressBarId())
        progressBar.visibility = View.VISIBLE

    }

    open fun cancelLoadingInner() {
        if (loadingProgressBarId() == 0) {
            return
        }
        val progressBar = findViewById<ProgressBar>(loadingProgressBarId())
        progressBar.visibility = View.GONE
    }

    protected fun requestPermission(permissions: Array<String>, permissionReturn: ((Boolean) -> Unit)? = null) {
        mPermissionReturn = permissionReturn

        var grant = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                grant = false
            }
        }

        if (!grant) {
            val dialog = AlertDialog.Builder(this)
                    .setMessage("请授予权限")
                    .setPositiveButton("确定", { _, _ ->
                        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
                    })
                    .create()
            mDialogSet.add(dialog)
            dialog.show()
        } else {
            permissionReturn?.invoke(true)
        }
    }

    fun showDialogMessage(msg: CharSequence,
                          textPositive: CharSequence? = null, listenerPositive: ((DialogInterface) -> Unit)? = null,
                          textNegative: CharSequence? = null, listenerNegative: ((DialogInterface) -> Unit)? = null,
                          title: CharSequence? = null,
                          onCancelListener: ((DialogInterface) -> Unit)? = null) {
        val dialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(textPositive, { dia, _ -> listenerPositive?.invoke(dia) })
                .setNegativeButton(textNegative, { dia, _ -> listenerNegative?.invoke(dia) })
                .create()
        dialog.setOnCancelListener {
            onCancelListener?.invoke(it)
        }
        mDialogSet.add(dialog)
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                mPermissionReturn?.invoke(true)
            } else {
                // Permission Denied
                mPermissionReturn?.invoke(false)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun backClick(view: View) {
        onBackPressed()
    }

    override fun onDestroy() {
        mDisposable.clear()
        mDialogSet.forEach {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        hideLoading()
        super.onDestroy()
    }

    override fun startActivity(intent: Intent) {
        try {
            super.startActivity(intent)
        } catch (e: Exception) {
            showToastSafe("启动失败")
            Log.i(TAG, "startActivity: failed $e")
        }
    }

    fun registerOnTouchListener(listener: OnTouchListener) {
        onTouchListenerList.add(listener)
    }

    fun removeOnTouchListener(listener: OnTouchListener) {
        onTouchListenerList.remove(listener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        onTouchListenerList.forEach {
            it.onTouchEvent(event = ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    interface OnTouchListener {
        fun onTouchEvent(event: MotionEvent)
    }
}