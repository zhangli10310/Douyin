package com.zl.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;


/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/3/13 15:46.<br/>
 */
public class CameraHelper extends ICameraHelper {

    private static final String TAG = "CameraHelper";

    private Camera mCamera;
    private int mCurrentOpen = -1;

    private SparseIntArray mIdMap;

    private StateCallBack mStateCallBack;

    public CameraHelper() {

        mIdMap = new SparseIntArray();
        mIdMap.put(CAMERA_BACK, Camera.CameraInfo.CAMERA_FACING_BACK);
        mIdMap.put(CAMERA_FRONT, Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    public void setPreviewSurface(SurfaceHolder holder) {

    }

    @Override
    public void setPreviewSurface(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void open(int width, int height, int id, @NonNull StateCallBack callBack) {
        mStateCallBack = callBack;
        try {
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
            mStateCallBack.onOpened(null);
        } catch (Exception e) {
            Log.e(TAG, "open: ", e);
            mCurrentOpen = -1;
            mStateCallBack.onError(-1);
        }

    }

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void release() {

    }

}
