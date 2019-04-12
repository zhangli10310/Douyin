package com.zl.ijk;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;

import com.zl.ijk.media.FileMediaDataSource;
import com.zl.ijk.media.IMediaController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by zhangli on 2019/2/16,14:28<br/>
 */
public class ZVideoView extends FrameLayout implements MediaController.MediaPlayerControl {

    private static final String TAG = "ZVideoView";


    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_STOPPED = 5;
    private static final int STATE_PLAYBACK_COMPLETED = 6;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private ZImageView mPreviewImage;
    private RenderView mRenderView;
    private RenderView.ISurfaceHolder mSurfaceHolder;

    private IMediaPlayer mMediaPlayer = null;

    // private int         mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaController mMediaController;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    private List<UriHeader> mUriHeaders;
    private int mCurrentUriIndex = 0;

    private int mVideoSarNum;
    private int mVideoSarDen;

    private long mPrepareStartTime = 0;
    private long mPrepareEndTime = 0;

    private long mSeekStartTime = 0;
    private long mSeekEndTime = 0;

    private boolean mLoop = false;

    private ZMeasureHelper mMeasureHelper;


    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {

            Log.i(TAG, "onPrepared: 准备完毕");

            mPrepareEndTime = System.currentTimeMillis();
            mCurrentState = STATE_PREPARED;

            // Get the capabilities of the player for this stream
            // REMOVED: Metadata

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mSurfaceHolder != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    mMeasureHelper.setVideoSize(mVideoWidth, mVideoHeight);
                    if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        // We didn't actually change the size (it was already at the size
                        // we need), so we won't get a "surface changed" callback, so
                        // start the video here instead of in the callback.
                        if (mTargetState == STATE_PLAYING) {
                            start();
                            if (mMediaController != null) {
                                mMediaController.show();
                            }
                        } else if (!isPlaying() &&
                                (seekToPosition != 0 || getCurrentPosition() > 0)) {
                            if (mMediaController != null) {
                                // Show the media controls when we're paused into a video and make 'em stick.
                                mMediaController.show(0);
                            }
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    Log.d(TAG, "onCompletion: ");
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            mPreviewImage.setVisibility(INVISIBLE);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            mVideoRotationDegree = arg2;
                            Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
//                            if (mRenderView != null)
//                                mRenderView.setVideoRotation(arg2);
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            break;
                    }
                    return true;
                }
            };

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    Log.d(TAG, "onVideoSizeChanged: mVideoSarNum:" + sarNum + " ,mVideoSarDen" + mVideoSarDen);
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
//                        if (mRenderView != null) {
//                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
//                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
//                        }
//                        requestLayout();
                        mMeasureHelper.setVideoSize(mVideoWidth, mVideoHeight);
                    }
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }

                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    if (mCurrentUriIndex < mUriHeaders.size() - 1) {
                        mCurrentUriIndex++;
                        openVideo();
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    Log.d(TAG, "onBufferingUpdate: " + percent);
                    mCurrentBufferPercentage = percent;
                }
            };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.d(TAG, "onSeekComplete: ");
            mSeekEndTime = System.currentTimeMillis();
        }
    };

    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            Log.d(TAG, "onTimedText: " + text);
        }
    };

    private RenderView.IRenderCallback mSurfaceRenderCallback = new RenderView.IRenderCallback() {

        @Override
        public void onSurfaceCreated(@NonNull RenderView.ISurfaceHolder holder, int width, int height) {
            Log.d(TAG, "surfaceCreated: ");
            mSurfaceHolder = holder;

            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
        }

        @Override
        public void onSurfaceChanged(@NonNull RenderView.ISurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged: ");
            mSurfaceHolder = holder;

            mSurfaceWidth = width;
            mSurfaceHeight = height;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == width && mVideoHeight == height);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
//                start();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull RenderView.ISurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed: ");

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
            releaseWithoutStop();
        }

    };

    public ZVideoView(Context context) {
        super(context);
        init();
    }

    public ZVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mUriHeaders = new ArrayList<>();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        mMeasureHelper = new ZMeasureHelper(this);

        mRenderView = new ZTextureRenderView(getContext());


        mRenderView.setMeasureHelper(mMeasureHelper);
        mRenderView.addRenderCallback(mSurfaceRenderCallback);
        mPreviewImage = new ZImageView(getContext());
        mPreviewImage.setMeasureHelper(mMeasureHelper);

        addView(mRenderView.getView(), lp);
        addView(mPreviewImage, lp);

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    private IMediaPlayer createDefaultIjkMediaPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_INFO);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1); //使用硬解码
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0); //使用软解码

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);  //自动旋转屏幕
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);//处理分辨率变化
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);


        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 3); //丢帧  是在视频帧处理不过来的时候丢弃一些帧达到同步的效果

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1); //需要准备好后自动播放
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48); //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);//设置播放前的探测时间 1,达到首屏秒开效果

        return ijkMediaPlayer;
    }

    private void bindSurfaceHolder(IMediaPlayer mp, RenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    public void setVideoURI(Uri uri) {
        mUriHeaders.clear();
        mUriHeaders.add(new UriHeader(uri, null));
        mCurrentUriIndex = 0;

        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setUriList(List<UriHeader> list) {
        mUriHeaders.clear();
        mUriHeaders.addAll(list);
        mCurrentUriIndex = 0;

        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    private void openVideo() {


        if (mUriHeaders.isEmpty() || mSurfaceHolder == null) {
            return;
        }
        release(false);

        try {
            mMediaPlayer = createDefaultIjkMediaPlayer();

            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
            mMediaPlayer.setLooping(mLoop);

            mCurrentBufferPercentage = 0;

            UriHeader uriHeader = mUriHeaders.get(mCurrentUriIndex);
            String scheme = uriHeader.uri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(uriHeader.uri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(getContext().getApplicationContext(), uriHeader.uri, uriHeader.headers);
            } else {
                mMediaPlayer.setDataSource(uriHeader.uri.toString());
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            // REMOVED: mPendingSubtitleTracks

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
//            attachMediaController();
        } catch (Exception e) {
            Log.e(TAG, "openVideo: ", e);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
        }

    }

    /*
     * release the media player in any state
     */
    public void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            // FIXME: 2019/4/12  这个地方直接开线程不好，但是可以解决卡顿，有待优化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }).start();
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
            abandonAudioFocus();
        }
        mPreviewImage.setVisibility(VISIBLE);
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_STOPPED &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    public void stopPlayback() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//            mCurrentUriIndex = 0;
//            mCurrentState = STATE_IDLE;
//            mTargetState = STATE_IDLE;
//        abandonAudioFocus();
//        }
        release(true);
    }

    public void stopAndPrepare() {
        mTargetState = STATE_PREPARED;
        if (isInPlaybackState()) {
            mMediaPlayer.stop();
            mCurrentState = STATE_STOPPED;
            mMediaPlayer.prepareAsync();
        }
        mPreviewImage.setVisibility(VISIBLE);
    }

    public void abandonAudioFocus(){
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public IMediaPlayer.OnPreparedListener getPreparedListener() {
        return mPreparedListener;
    }

    public void setPreparedListener(IMediaPlayer.OnPreparedListener preparedListener) {
        mPreparedListener = preparedListener;
    }

    public IMediaPlayer.OnCompletionListener getCompletionListener() {
        return mCompletionListener;
    }

    public void setCompletionListener(IMediaPlayer.OnCompletionListener completionListener) {
        mCompletionListener = completionListener;
    }

    public IMediaPlayer.OnInfoListener getInfoListener() {
        return mInfoListener;
    }

    public void setInfoListener(IMediaPlayer.OnInfoListener infoListener) {
        mInfoListener = infoListener;
    }

    public IMediaPlayer.OnVideoSizeChangedListener getSizeChangedListener() {
        return mSizeChangedListener;
    }

    public void setSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener sizeChangedListener) {
        mSizeChangedListener = sizeChangedListener;
    }

    public IMediaPlayer.OnErrorListener getErrorListener() {
        return mErrorListener;
    }

    public void setErrorListener(IMediaPlayer.OnErrorListener errorListener) {
        mErrorListener = errorListener;
    }

    public IMediaPlayer.OnBufferingUpdateListener getBufferingUpdateListener() {
        return mBufferingUpdateListener;
    }

    public void setBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener bufferingUpdateListener) {
        mBufferingUpdateListener = bufferingUpdateListener;
    }

    public IMediaPlayer.OnSeekCompleteListener getSeekCompleteListener() {
        return mSeekCompleteListener;
    }

    public void setSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener seekCompleteListener) {
        mSeekCompleteListener = seekCompleteListener;
    }

    public IMediaPlayer.OnTimedTextListener getOnTimedTextListener() {
        return mOnTimedTextListener;
    }

    public void setOnTimedTextListener(IMediaPlayer.OnTimedTextListener onTimedTextListener) {
        mOnTimedTextListener = onTimedTextListener;
    }

    public void setVideoSize(int width, int height) {
        mMeasureHelper.setVideoSize(width, height);
    }

    public boolean isLoop() {
        return mLoop;
    }

    public void setLoop(boolean loop) {
        mLoop = loop;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(loop);
        }
    }

    public ImageView getPreviewImage() {
        return mPreviewImage;
    }
}
