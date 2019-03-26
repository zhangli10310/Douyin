package com.zl.douyin.ui.littlefilm

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.TextureView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.zl.camera.Camera2Helper
import com.zl.camera.CameraHelper
import com.zl.camera.ICameraHelper
import com.zl.core.base.ModeFragment
import com.zl.douyin.R
import com.zl.douyin.ui.main.SharedViewModel
import kotlinx.android.synthetic.main.fragment_little_film.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2019/3/4 15:26.<br/>
 */
class LittleFilmFragment : ModeFragment(), TextureView.SurfaceTextureListener {

    private val TAG = LittleFilmFragment::class.java.simpleName
    private lateinit var shareViewModel: SharedViewModel

    private var preview = false
    private var permissionOk = false
    private lateinit var mCameraHelper: ICameraHelper
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0


    override fun layoutId() = R.layout.fragment_little_film

    override fun initView(savedInstanceState: Bundle?) {
        cameraView.surfaceTextureListener = this

        mCameraHelper = CameraHelper(activity!!)
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
                        permissionOk = true
                        if (!preview && mSurfaceTexture != null) {
                            preview()
                        }
                    } else {
                        permissionOk = false
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

    @SuppressLint("MissingPermission")
    private fun preview() {
        preview = true

        mCameraHelper.open(mWidth, mHeight, Camera2Helper.CAMERA_BACK, object : ICameraHelper.StateCallBack {
            override fun onOpened(size: ICameraHelper.Size) {
                Log.i(TAG, "onOpened: ")
                mSurfaceTexture?.setDefaultBufferSize(size.width, size.height)
                mCameraHelper.setPreviewSurface(mSurfaceTexture)
//                mCameraHelper.ini
            }

            override fun onDisconnected() {
                Log.i(TAG, "onDisconnected: ")
                preview = false
            }

            override fun onError(error: Int) {
                Log.i(TAG, "onError: ")
                preview = false
            }

            override fun onClosed() {
                Log.i(TAG, "onClosed: ")
                preview = false
            }

        })

    }

    private fun stopPreview() {
        preview = false
        mCameraHelper.stopPreview()
        mCameraHelper.release()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        stopPreview()
        mSurfaceTexture = null
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurfaceTexture = surface
        mWidth = width
        mHeight = height
        if (permissionOk && !preview) {
            preview()
        }

    }
}