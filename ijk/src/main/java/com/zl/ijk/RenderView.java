package com.zl.ijk;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.zl.ijk.media.IRenderView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/2/26 18:56.<br/>
 */
public interface RenderView {

    View getView();

    boolean shouldWaitForResize();

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    void setVideoRotation(int degree);

    void setAspectRatio(int aspectRatio);

    void addRenderCallback(@NonNull IRenderCallback callback);

    void removeRenderCallback(@NonNull IRenderCallback callback);

    void setMeasureHelper(ZMeasureHelper measureHelper);


    interface ISurfaceHolder {
        void bindToMediaPlayer(IMediaPlayer mp);

        @NonNull
        RenderView getRenderView();

        @Nullable
        SurfaceHolder getSurfaceHolder();

        @Nullable
        Surface openSurface();

        @Nullable
        SurfaceTexture getSurfaceTexture();
    }

    interface IRenderCallback {
        /**
         * @param holder
         * @param width  could be 0
         * @param height could be 0
         */
        void onSurfaceCreated(@NonNull RenderView.ISurfaceHolder holder, int width, int height);

        /**
         * @param holder
         * @param format could be 0
         * @param width
         * @param height
         */
        void onSurfaceChanged(@NonNull RenderView.ISurfaceHolder holder, int format, int width, int height);

        void onSurfaceDestroyed(@NonNull RenderView.ISurfaceHolder holder);
    }
}
