package com.aqrlei.sample.viewsample

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View

/**
 * @author aqrlei on 2019/4/12
 */

class SampleView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {


    private var velocityTracker: VelocityTracker = VelocityTracker.obtain()
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        velocityTracker.addMovement(event)
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
}