package com.zl.core.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import com.zl.core.utils.DisplayUtils
import java.util.*

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/3/26 20:34.<br/>
 */
class LikeHeartView : View {

    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val path = Path()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        paint.shader = LinearGradient(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), intArrayOf(Color.RED, Color.YELLOW), null, Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.reset()
        path.moveTo(width / 2f, height / 4f)
        path.cubicTo(width * 6f / 7, height / 9f, width * 12f / 13, height * 2f / 5, width / 2f, height * 7f / 12)
        canvas.drawPath(path, paint)

        path.reset()
        path.moveTo(width / 2f, height / 4f)
        path.cubicTo(width / 7f, height / 9f, width / 13f, height * 2 / 5f, width / 2f, height * 7f / 12)
        canvas.drawPath(path, paint)
    }

    fun reset() {
        alpha = 1f
        translationX = 0f
        translationY = 0f
        scaleX = 0f
        scaleY = 0f
    }

    fun anim(onAnimEnd: ((LikeHeartView) -> Unit)? = null) {
        val set = AnimatorSet()
        val random = Random()
        val ran = DisplayUtils.dp2px(context, random.nextInt(30).toFloat())
        set.playTogether(ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_X, translationX, translationX + (ran - DisplayUtils.dp2px(context, 15f)) * 2),
                ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, translationY, translationY - ran),
                ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 1.2f),
                ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 1.2f)
        )
        set.interpolator = AccelerateInterpolator()
        set.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                onAnimEnd?.invoke(this@LikeHeartView)
            }

        })
        set.start()
    }

}