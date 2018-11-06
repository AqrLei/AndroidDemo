package open.aqrlei.com.coordinatorsample.behavior

import android.content.Context
import android.os.Handler
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.widget.Scroller
import open.aqrlei.com.coordinatorsample.R
import java.lang.ref.WeakReference

/**
 * @author  aqrLei on 2018/7/27
 */
class HeaderScrollingBehavior(context: Context, attrs: AttributeSet) :
       CoordinatorLayout.Behavior<View>(context, attrs) {

    private var isScrolling: Boolean = false
    private var dependView: WeakReference<View>? = null
    private val scroller: Scroller
            by lazy { Scroller(context) }
    private val handler: Handler
            by lazy {
                Handler()
            }
    private val flingRunnable: Runnable
            by lazy {
                object : Runnable {
                    override fun run() {
                        dependView?.get()?.let {
                            if (scroller.computeScrollOffset()) {
                                it.translationY = scroller.currY.toFloat()
                                handler.post(this)
                            } else {
                                isScrolling = false
                            }
                        }
                    }
                }
            }


    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        dependency.let {
            if (dependency.id == R.id.headerIV) {
                dependView = WeakReference(dependency)
                return true
            }
        }
        return false
    }

    private fun getDependViewCollapsedHeight() =
            dependView?.get()?.resources?.getDimension(R.dimen.collapsed_header_height) ?: 0F

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency.resources?.let {
            val keepHeight = it.getDimension(R.dimen.collapsed_header_height)
            val progress = 1.0f - Math.abs(dependency.translationY / (dependency.height - keepHeight))
            val initOffset = it.getDimension(R.dimen.init_float_offset_y)
            val translationY = keepHeight + progress * initOffset
            child.translationY = translationY
            val scale = 1 + 0.4f * (1 - progress)
            dependency.scaleX = scale
            dependency.scaleY = scale
            dependency.alpha = progress
            true
        } ?: false
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        //竖直方向滑动才care
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int) {
        // 新的滑动，终止原来的Scroller动画
        scroller.abortAnimation()
        isScrolling = false
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        /*
        * dy  竖直方向上滑动的距离，< 0 向下滑动，> 0 向上滑动
        * consumed[0]:x轴上消费的距离
        * consumed[1]:y轴上消费的距离
        * 此方法里，做折叠时的处理
        * */
        if (dy < 0) return// 短路向下滑动展开的情况，直接到onNestedScroll
        dependView?.get()?.let {
            /* view原本的位移（translationY,为负数) - dy(用户滑动的距离，为正数),新的折叠位移距离*/
            val newTranslateY = it.translationY - dy
            /*view的高度 - 默认保留的折叠后的高度 = 可以位移的距离 */
            val minHeaderTranslate = -(it.height - getDependViewCollapsedHeight())
            /*如果可以折叠，位移的距离（负数) > 限定值(负数),位移*/
            if (newTranslateY > minHeaderTranslate) {
                it.translationY = newTranslateY
                consumed[1] = dy
            }
        }
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        /*
        * 此方法里，做展开时的处理
        * */
        if (dyUnconsumed > 0) return // 短路折叠时的情况
        dependView?.get()?.let {
            /*
            * dyUnconsumed，有数值说明NSC(此例中为RecyclerView)滑到头了,可以做HeaderView的展开操作
            * translationY(为负数),dyUnconsumed，用户向下滑动(为负数)
            * */
            val newTranslateY = it.translationY - dyUnconsumed
            val maxHeaderTranslate = 0
            /*
            * newTranslateY为0时，说明HeaderView已完全展开
            * newTranslateY<0，还未完全展开
            * */
            if (newTranslateY < maxHeaderTranslate) {
                it.translationY = newTranslateY
            }
        }
    }

    /*松开手指，发生惯性滚动时调用此方法*/
    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float): Boolean {
        return onUserStopDragging(velocityY)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        if (!isScrolling) {
            onUserStopDragging(800f)
        }
    }

    private fun onUserStopDragging(velocity: Float): Boolean {
        dependView?.get()?.let {
            val translateY = it.translationY
            /*最小的移动距离(负数，故其实是最大的移动距离)*/
            val minHeaderTranslate = -(it.height - getDependViewCollapsedHeight())
            /*已到最终态，就不需要处理了，返回false*/
            if (translateY == 0F || translateY == minHeaderTranslate) {
                return false
            }
            /*
            * targetState为true时，滑向折叠
            * targetState为false时，滑向展开
            * */
            val targetState = when {
                Math.abs(velocity) <= 800 -> {
                    /*translateY<= -225 时满足*/
                    Math.abs(translateY) >= Math.abs(translateY - minHeaderTranslate)
                }
                else -> {
                    /*向上滑时满足*/
                    velocity > 0
                }
            }
            /*
            * HeaderView折叠时， HeaderView的Y轴最终位置为 minHeaderTranslate
            * HeaderView展开时， 最终位置为0
            * */
            val targetTranslateY = if (targetState) minHeaderTranslate else 0F
            scroller.startScroll(0, translateY.toInt(), 0, (targetTranslateY - translateY).toInt(), (1000000 / Math.abs(velocity)).toInt())
            handler.post(flingRunnable)
            isScrolling = true
            return true
        } ?: return false
    }
}