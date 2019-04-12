package com.zl.ijk;

import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/2/18 19:12.<br/>
 */
public class ZMeasureHelper {

    private static final String TAG = "ZMeasureHelper";

    public static final int AR_FILL_PARENT = 0; //充满屏幕，可能拉伸
    public static final int AR_MATCH_WIDTH = 1; //宽度充满，高度按比例放缩
    public static final int AR_MATCH_HEIGHT = 2; //高度充满，宽度按比例放缩
    public static final int AR_MATCH_PARENT = 3; //宽高均按比例放缩充满父容器，可能有丢失
    public static final int AR_FILL_LOSS_10 = 4; //丢失在10%以内充满屏幕，否则按高度或者宽度适配

    private WeakReference<View> mWeakView;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mVideoRotationDegree;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mCurrentAspectRatio = AR_FILL_LOSS_10;

    public ZMeasureHelper(View view) {
        mWeakView = new WeakReference<View>(view);
    }

    public View getView() {
        if (mWeakView == null)
            return null;
        return mWeakView.get();
    }

    public void setVideoSize(int videoWidth, int videoHeight) {
        if (mVideoWidth != videoWidth || mVideoHeight != videoHeight) {
            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;

            View view = getView();
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mVideoSarNum = videoSarNum;
        mVideoSarDen = videoSarDen;
    }

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    /**
     * Must be called by View.onMeasure(int, int)
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    public void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "doMeasure mVideoWidth:" + mVideoWidth + " mVideoHeight:" + mVideoHeight);
        Log.d(TAG, "onMeasure(" + View.MeasureSpec.toString(widthMeasureSpec) + ", "
                + View.MeasureSpec.toString(heightMeasureSpec) + ")");
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            int tempSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempSpec;
        }

        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);

        Log.d(TAG, "doMeasure,default width:" + width + " height:" + height);
        if (mCurrentAspectRatio == AR_MATCH_PARENT) {
            width = widthMeasureSpec;
            height = heightMeasureSpec;
        } else if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

            Log.d(TAG, "doMeasure widthSpecSize:" + widthSpecSize + " heightSpecSize:" + heightSpecSize);
            if (mCurrentAspectRatio == AR_FILL_PARENT) {
                width = widthSpecSize;
                height = (int) (width * 1.0 * mVideoHeight / mVideoWidth);
            } else if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
                float displayAspectRatio;
                switch (mCurrentAspectRatio) {
//                    case AR_MATCH_WIDTH:
//                        displayAspectRatio = 16.0f / 9.0f;
//                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
//                            displayAspectRatio = 1.0f / displayAspectRatio;
//                        break;
//                    case AR_MATCH_HEIGHT:
//                        displayAspectRatio = 4.0f / 3.0f;
//                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270)
//                            displayAspectRatio = 1.0f / displayAspectRatio;
//                        break;
//                    case AR_MATCH_PARENT:
//                        break;
//                    case AR_FILL_LOSS_10:
//                        break;
                    default:
                        displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
                        if (mVideoSarNum > 0 && mVideoSarDen > 0)
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen;
                        break;
                }
                boolean shouldBeWider = displayAspectRatio > specAspectRatio;

                Log.d(TAG, "doMeasure displayAspectRatio:" + displayAspectRatio + ", specAspectRatio" + specAspectRatio);
                switch (mCurrentAspectRatio) {
                    case AR_MATCH_WIDTH:
                        width = widthSpecSize;
                        height = (int) (width * 1.0 * mVideoHeight / mVideoWidth);
                        break;
                    case AR_MATCH_HEIGHT:
                        height = heightSpecSize;
                        width = (int) (height * 1.0 * mVideoWidth / mVideoHeight);
                        break;
                    case AR_MATCH_PARENT:
                        if (specAspectRatio < displayAspectRatio) {
                            height = heightSpecSize;
                            width = (int) (height * 1.0 * mVideoWidth / mVideoHeight);
                        } else {
                            width = widthSpecSize;
                            height = (int) (width * 1.0 * mVideoHeight / mVideoWidth);
                        }
                        break;
                    case AR_FILL_LOSS_10:
                        if (specAspectRatio / displayAspectRatio < 1.1f && specAspectRatio / displayAspectRatio > 0.9) {
                            if (specAspectRatio < displayAspectRatio) {
                                height = heightSpecSize;
                                width = (int) (height * 1.0 * mVideoWidth / mVideoHeight);
                            } else {
                                width = widthSpecSize;
                                height = (int) (width * 1.0 * mVideoHeight / mVideoWidth);
                            }
                        } else if (specAspectRatio / displayAspectRatio >= 1.1f){
                            height = heightSpecSize;
                            width = (int) (height * 1.0 * mVideoWidth / mVideoHeight);
                        } else {
                            width = widthSpecSize;
                            height = (int) (width * 1.0 * mVideoHeight / mVideoWidth);
                        }
                        break;
                    default:
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = Math.min(mVideoWidth, widthSpecSize);
                            height = (int) (width / displayAspectRatio);
                        } else {
                            // too high, fix height
                            height = Math.min(mVideoHeight, heightSpecSize);
                            width = (int) (height * displayAspectRatio);
                        }
                        break;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }

        mMeasuredWidth = width;
        mMeasuredHeight = height;
    }

    public int getMeasuredWidth() {
        return mMeasuredWidth;
    }

    public int getMeasuredHeight() {
        return mMeasuredHeight;
    }

    public void setAspectRatio(int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
    }

    public void reset() {
        mVideoHeight = 0;
        mVideoWidth = 0;
        mVideoRotationDegree = 0;
        mVideoSarDen = 0;
        mVideoSarNum = 0;
    }
}
