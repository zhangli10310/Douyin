package com.zl.ijk;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2019/2/19 16:54.<br/>
 */
public class ZImageView extends AppCompatImageView {

    private ZMeasureHelper mMeasureHelper;

    public ZImageView(Context context) {
        super(context);
    }

    public ZImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ZMeasureHelper getMeasureHelper() {
        if (mMeasureHelper == null) {
            mMeasureHelper = new ZMeasureHelper(this);
        }
        return mMeasureHelper;
    }

    public void setMeasureHelper(ZMeasureHelper measureHelper) {
        mMeasureHelper = measureHelper;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");

        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }
}
