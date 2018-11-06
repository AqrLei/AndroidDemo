package open.aqrlei.com.coordinatorsample.behavior

import android.animation.ArgbEvaluator
import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import open.aqrlei.com.coordinatorsample.R

/**
 * @author  aqrLei on 2018/7/27
 */
class HeaderFloatBehavior(context: Context, attrs: AttributeSet) :
        CoordinatorLayout.Behavior<View>(context, attrs) {
    private val argbEvaluator: ArgbEvaluator
            by lazy {
                ArgbEvaluator()
            }


    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency.id == R.id.headerIV
    }



    override fun onDependentViewChanged(parent:CoordinatorLayout, child: View, dependency: View):
            Boolean {
        dependency.resources?.let {


            // Y轴平移
            /*滑动时会保留50dp的空间*/
            val keepHeight = it.getDimension(R.dimen.collapsed_header_height)
            /*
            * CoordinatorLayout的ChildView默认都是与父布局的top对齐
            * 获取ChildView在Y轴s上移动的距离
            * */
            val progress = 1.0f - Math.abs(dependency.translationY / (dependency.height - keepHeight))
            /*
            * 顶部预留5dp,因为ChildView的Height为40dp, (50 - 40)/2 =5
            * */
            val collapsedOffset = it.getDimension(R.dimen.collapsed_float_offset_y)
            val initOffset = it.getDimension(R.dimen.init_float_offset_y)
            val translateY = collapsedOffset + (initOffset - collapsedOffset) * progress
            child.translationY = translateY

            // 背景颜色
            child.setBackgroundColor(argbEvaluator.evaluate(
                    progress,
                    it.getColor(R.color.colorCollapsedFloatBackground),
                    it.getColor(R.color.colorInitFloatBackground)) as Int)

            // 左右边距
            val collapsedMargin = it.getDimension(R.dimen.collapsed_float_margin)
            val initMargin = it.getDimension(R.dimen.init_float_margin)
            val margin = (collapsedMargin + (initMargin - collapsedMargin) * progress).toInt()
            val lp = child.layoutParams as? CoordinatorLayout.LayoutParams
            lp?.setMargins(margin, 0, margin, 0)
            child.layoutParams = lp
            return true
        }
        return false
    }

}