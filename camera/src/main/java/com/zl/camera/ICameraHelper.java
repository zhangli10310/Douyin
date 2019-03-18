package com.zl.camera;

import android.graphics.SurfaceTexture;
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
        void onOpened(Size size);

        void onDisconnected();

        void onError(int error);

        void onClosed();
    }

    public static class Size {
        private int width;
        private int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
