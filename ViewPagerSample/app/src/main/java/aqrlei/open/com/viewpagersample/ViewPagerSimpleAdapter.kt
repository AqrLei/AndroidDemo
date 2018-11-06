package  aqrlei.open.com.viewpagersample

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * @author  aqrLei on 2018/7/23
 */
class ViewPagerSimpleAdapter(private val viewList: List<View>) : PagerAdapter() {
    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == any
    }

    override fun getCount() = viewList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(viewList[position])
        return viewList[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(viewList[position])
    }
}