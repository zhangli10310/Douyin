package com.zl.ijk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/2/18 19:06.<br/>
 */
public class ZSurfaceView extends SurfaceView {

    private ZMeasureHelper mMeasureHelper;

    public ZSurfaceView(Context context) {
        this(context, null);
    }

    public ZSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMeasureHelper = new ZMeasureHelper(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");

        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }
}
