package com.zl.app.splash

import android.animation.ObjectAnimator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zl.app.R
import com.zl.core.base.ModeActivity
import com.zl.douyin.ui.main.MainActivity
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

        ObjectAnimator.ofFloat(launchImg, View.ALPHA, 0f, 1f)
                .setDuration(1500)
                .start()

        requestPermission(arrayOf(android.Manifest.permission.READ_PHONE_STATE)) {
            if (it) {
                viewModel.countDown()
            } else {
                finish()
            }
        }
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

        viewModel.jump.observe(this, Observer {
            if (it != null && it) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }
}