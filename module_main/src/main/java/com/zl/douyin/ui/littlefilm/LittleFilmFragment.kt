package com.zl.douyin.ui.littlefilm

import android.Manifest
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.view.TextureView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.zl.core.base.ModeFragment
import com.zl.douyin.R
import com.zl.douyin.ui.main.SharedViewModel
import kotlinx.android.synthetic.main.fragment_little_film.*
import java.lang.Exception

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2019/3/4 15:26.<br/>
 */
class LittleFilmFragment : ModeFragment(), TextureView.SurfaceTextureListener {

    private lateinit var shareViewModel: SharedViewModel

    private var preview = false
    private var mCamera: Camera? = null
    private var mSurfaceTexture: SurfaceTexture? = null

    override fun layoutId() = R.layout.fragment_little_film

    override fun initView(savedInstanceState: Bundle?) {

        cameraView.surfaceTextureListener = this
    }

    override fun setListener() {

        closeImg.setOnClickListener {
            shareViewModel.gotoViewPagerPosition.value = 1
        }
    }

    override fun observe() {
        shareViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        shareViewModel.onViewPagerChange.observe(this, Observer {
            if (it == 0) {
                getBaseActivity().requestPermission(arrayOf(Manifest.permission.CAMERA)) { grand ->
                    if (grand) {
                        preview()
                    } else {
                        showToastSafe("请打开相机权限")
                    }
                }
            } else if (preview && it != 0) {
                stopPreview()
            }
        })
    }

    override fun afterView() {

    }

    private fun preview() {
        preview = true
        try {
            mCamera = Camera.open()
            mCamera!!.setDisplayOrientation(90)
            mCamera!!.setPreviewTexture(mSurfaceTexture)
            mCamera!!.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun stopPreview() {
        preview = false
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        stopPreview()
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurfaceTexture = surface
    }
}