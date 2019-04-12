package com.aqrlei.sample.viewsample

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.Scroller
import android.widget.TextView
import java.lang.ref.WeakReference

/**
 * @author aqrlei on 2019/4/12
 */

class SampleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    companion object {
        const val SLIDE_MODE_SCROLL = 0
        const val SLIDE_MODE_SMOOTH_SCROLL = 10
        const val SLIDE_MODE_ANIMATION = 1
        const val SLIDE_MODE_ANIMATOR = 2
        const val SLIDE_MODE_ANIMATOR_SCROLL = 12
        const val SLIDE_MODE_LAYOUT_PARAMS = 3
        const val SLIDE_MODE_HANDLER_SMOOTH = 13
    }


    private var slideMode = SLIDE_MODE_SCROLL

    private var mScrollX = 0
    private var lastScrollX = 0
    private var intervalX = 10
    private val mHandler = SlideHandler(WeakReference(this))

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SampleView).apply {
            slideMode = getInt(R.styleable.SampleView_slideMode, slideMode)
            recycle()
        }
    }

    private var velocityTracker: VelocityTracker = VelocityTracker.obtain()
    private var mScroller: Scroller = Scroller(context)

    private fun smoothScrollTo(destX: Int) {
        val deltaX = destX - scrollX
        val time = Math.abs(deltaX) / 100 * 1000
        mScroller.startScroll(scrollX, y.toInt(), deltaX, y.toInt(), time)
        invalidate()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        Log.d("View", "left:$left-top:$top-right:$right-bottom:$bottom")
        if (event != null) {
            when (slideMode) {
                // 只是移动View内容的距离
                // scrollBy()是相对目前的scrollX, scrollY进行移动
                // scrollTo()则是覆盖之前的scrollX, scrollY进行移动
                // scrollX == View的左边坐标 - View内容的左边坐标 ，即scrollX为负值时，View内容向右移
                // scrollY == View的顶部坐标 - View内容的顶部坐标， scrollY为负值时，View内容向下移动
                SLIDE_MODE_SCROLL -> {
                    Log.d("Scroll", "Event: ${event.x.toInt()}- ${event.y.toInt()}")
                    Log.d("Scroll", "Before: $scrollX - $scrollY")

                    scrollTo(-event.x.toInt(), 0)
                    Log.d("Scroll", "After: $scrollX - $scrollY")
                }
                SLIDE_MODE_SMOOTH_SCROLL -> smoothScrollTo(-event.x.toInt())
                //移动后，点击响应的位置还在原来的区域
                // left,top,right,bottom属性不变
                // x, y属性不变
                SLIDE_MODE_ANIMATION -> {
                    Log.d(
                        "Animation",
                        "view: $x - $y, event: ${event.x} - ${event.y}, translation: $translationX - $translationY"
                    )
                    val translateTime = (Math.abs(event.x - x) / 100 * 1000).toLong()
                    val translateAnimation = TranslateAnimation(x, event.x, y, y).apply {
                        duration = translateTime
                        fillAfter = true
                    }
                    this.startAnimation(translateAnimation)
                }
                //移动后，点击响应的位置在移动后的区域
                // left,top,right,bottom属性不变
                // translationX,translationY改变 导致x, y属性改变
                SLIDE_MODE_ANIMATOR -> {
                    Log.d(
                        "Animator",
                        "view: $x - $y, event: ${event.x} - ${event.y}, translation: $translationX - $translationY"
                    )
                    val translateTime = (Math.abs(event.x - x) / 100 * 100).toLong()
                    ObjectAnimator.ofFloat(this, "translationX", x, event.x).setDuration(translateTime).start()
                }
                SLIDE_MODE_ANIMATOR_SCROLL -> {
                    lastScrollX = scrollX
                    mScrollX = -event.x.toInt() - scrollX
                    ValueAnimator.ofInt(0, 1).apply {
                        duration = Math.abs(mScrollX) / 100 * 1000.toLong()
                        addUpdateListener {
                            it.animatedFraction
                            this@SampleView.scrollTo((it.animatedFraction * mScrollX).toInt() + lastScrollX, 0)
                        }
                        start()
                    }
                }
                //移动后，点击响应的位置在移动后的区域
                // left,top,right,bottom属性根据情况改变
                // left,top改变 导致x, y属性改变
                SLIDE_MODE_LAYOUT_PARAMS -> {
                    Log.d(
                        "Params",
                        "view: $x - $y, event: ${event.x} - ${event.y}, translation: $translationX - $translationY"
                    )
                    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                        leftMargin = event.x.toInt()
                        this@SampleView.requestLayout()
                    }
                }
                SLIDE_MODE_HANDLER_SMOOTH -> {
                    lastScrollX = scrollX
                    mScrollX = -event.x.toInt() - scrollX
                    intervalX = if (mScrollX < 0) -10 else 10
                    mHandler.sendMessage(Message.obtain(null, SLIDE_MODE_HANDLER_SMOOTH, intervalX, 0))

                }
            }
        }
        return super.onTouchEvent(event)
    }


    private fun computeVelocity() {
        velocityTracker.computeCurrentVelocity(1000)
        val xVelocity = velocityTracker.xVelocity
        val yVelocity = velocityTracker.yVelocity
    }

    override fun onDetachedFromWindow() {
        velocityTracker.clear()
        velocityTracker.recycle()
        super.onDetachedFromWindow()
    }

    private class SlideHandler(private val weakReference: WeakReference<SampleView>) : Handler() {
        private var scrollerX = 0
        override fun handleMessage(msg: Message?) {
            if (msg?.what == SLIDE_MODE_HANDLER_SMOOTH) {
                weakReference.get()?.run {
                    scrollerX += msg.arg1
                    scrollTo(scrollerX + lastScrollX, 0)
                    mScrollX -= intervalX
                    if (Math.abs(mScrollX) >= Math.abs(intervalX)) {
                        mHandler.sendMessageDelayed(Message.obtain(null, SLIDE_MODE_HANDLER_SMOOTH, intervalX, 0), 100)
                    } else {
                        scrollerX = 0
                    }
                }
            }
        }
    }

}