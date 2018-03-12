package com.zl.douyin.ui.splash

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.zl.core.base.ModeActivity
import com.zl.douyin.BuildConfig
import com.zl.douyin.R
import com.zl.douyin.ui.main.MainActivity
import com.zl.douyin.update.BackgroundDownloadService
import kotlinx.android.synthetic.main.activity_splash.*

/**
 *
 *<p></p>
 *
 * Created by zhangli.<br/>
 */
class SplashActivity : ModeActivity() {

    private val TAG = SplashActivity::class.java.simpleName

    private lateinit var viewModel: SplashViewModel

    override fun layoutId(): Int = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {
//        version.text = "版本号:${BuildConfig.VERSION_NAME}"
    }

    override fun afterView() {

//        requestPermission(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), {
//            if (it) {
//                BackgroundDownloadService.start(this, "", "")
//            }
//        })
        //fixme 内存泄漏
        mHandler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    override fun setListener() {

    }

    override fun observe() {
        viewModel = ViewModelProviders.of(this, SplashViewModel.Factory()).get(SplashViewModel::class.java)

        viewModel.isLoadingInner.observe(this, Observer {
            if (it != null && it) { //正在加载
                loadingInner()
            } else {
                cancelLoadingInner()
            }
        })

        viewModel.isLoading.observe(this, Observer {
            if (it != null && it) { //正在加载
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.errorMsg.observe(this, Observer {
            if (it?.msg != null) {
                showToastSafe(it.msg!!)
            }
        })
    }

    override fun loadingProgressBarId() = R.id.progressBar
}