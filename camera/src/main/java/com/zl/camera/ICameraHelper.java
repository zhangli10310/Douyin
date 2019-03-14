package com.zl.camera;

import android.graphics.SurfaceTexture;
import android.util.Size;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/3/13 15:50.<br/>
 */
public abstract class ICameraHelper {

    public static int CAMERA_FRONT = 1;
    public static int CAMERA_BACK = 0;


    public static int NO_CAMERA_ID = 99;

    public abstract void setPreviewSurface(SurfaceHolder holder);

    public abstract void setPreviewSurface(SurfaceTexture surfaceTexture);

    public abstract void open(int width, int height, int id, @NonNull StateCallBack callBack);

    public abstract void startPreview();
    public abstract void stopPreview();
    public abstract void release();

    public interface StateCallBack {
        void onOpened(Size preSize);

        void onDisconnected();

        void onError(int error);

        void onClosed();
    }
}
