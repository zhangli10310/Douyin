package com.zl.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;


/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/3/13 15:46.<br/>
 */
public class CameraHelper extends ICameraHelper {

    private static final String TAG = "CameraHelper";

    private WeakReference<Activity> mActivityWeakReference;

    private Camera mCamera;
    private int mCurrentOpen = -1;

    private SparseIntArray mIdMap;

    private StateCallBack mStateCallBack;

    private int mDisplayRotate;

    private Size mPreviewSize;


    public CameraHelper(Activity activity) {

        mActivityWeakReference = new WeakReference<>(activity);
        mIdMap = new SparseIntArray();
        mIdMap.put(CAMERA_BACK, Camera.CameraInfo.CAMERA_FACING_BACK);
        mIdMap.put(CAMERA_FRONT, Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    public void setPreviewSurface(SurfaceHolder holder) {
        int displayOrientation = getDisplayOrientation();
        mCamera.setDisplayOrientation(displayOrientation);
        mCamera.getParameters().setPictureSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        try {
            mCamera.setPreviewDisplay(holder);
            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPreviewSurface(SurfaceTexture surfaceTexture) {
        int displayOrientation = getDisplayOrientation();
        mCamera.setDisplayOrientation(displayOrientation);
        mCamera.getParameters().setPictureSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open(final int width, final int height, final int id, final @NonNull StateCallBack callBack) {
        mStateCallBack = callBack;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = Camera.getNumberOfCameras();
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    int cid = -1;
                    for (int i = 0; i < count; i++) {
                        Camera.getCameraInfo(i, info);
                        if (info.facing == mIdMap.get(id, -1)) {
                            cid = i;
                            break;
                        }
                    }
                    if (cid == -1) {
                        mStateCallBack.onError(NO_CAMERA_ID);
                        return;
                    }
                    mCamera = Camera.open(cid);
                    mCurrentOpen = id;
                    List<Camera.Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                    Camera.Size pictureSize = mCamera.getParameters().getPictureSize();
                    mPreviewSize = chooseOptimalSize(supportedPreviewSizes, width, height, pictureSize);
                    mStateCallBack.onOpened(mPreviewSize);
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "open: ", e);
            mCurrentOpen = -1;
            mStateCallBack.onError(-1);
        }

    }

    private Size chooseOptimalSize(List<Camera.Size> sizes, int viewWidth, int viewHeight, Camera.Size pictureSize) {
        int totalRotation = getDisplayOrientation();
        boolean swapRotation = totalRotation == 90 || totalRotation == 270;
        int width = swapRotation ? viewHeight : viewWidth;
        int height = swapRotation ? viewWidth : viewHeight;
        return getSuitableSize(sizes, width, height, pictureSize);
    }

    private int getDisplayOrientation() {
        int orientation = 0;
        if (mActivityWeakReference.get() != null) {
            orientation = mActivityWeakReference.get().getWindowManager().getDefaultDisplay().getRotation();
        }
        int degress = 0;
        switch (orientation) {
            case Surface.ROTATION_0:
                degress = 0;
                break;
            case Surface.ROTATION_90:
                degress = 90;
                break;
            case Surface.ROTATION_180:
                degress = 180;
                break;
            case Surface.ROTATION_270:
                degress = 270;
                break;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mIdMap.get(mCurrentOpen, Camera.CameraInfo.CAMERA_FACING_BACK), cameraInfo);
        int result = (cameraInfo.orientation - degress + 360) % 360;
        return result;
    }

    private Size getSuitableSize(List<Camera.Size> sizes, int width, int height, Camera.Size pictureSize) {
        int minDelta = Integer.MAX_VALUE; // 最小的差值，初始值应该设置大点保证之后的计算中会被重置
        int index = 0; // 最小的差值对应的索引坐标
        float aspectRatio = pictureSize.height * 1.0f / pictureSize.width;
        Log.d(TAG, "getSuitableSize. aspectRatio: " + aspectRatio);
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            // 先判断比例是否相等
            if (size.width * aspectRatio == size.height) {
                int delta = Math.abs(width - size.width);
                if (delta == 0) {
                    return new Size(size.width, size.height);
                }
                if (minDelta > delta) {
                    minDelta = delta;
                    index = i;
                }
            }
        }
        return new Size(sizes.get(index).width, sizes.get(index).height);
    }

    @Override
    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void release() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

}
