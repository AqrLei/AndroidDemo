package open.aqrlei.com.recyclerviewsample

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author aqrlei on 2019/3/23
 * spanCount: 设置好的列数
 * spacing:设置好的间距
 * includeEdge:边缘是否由间距
 */
class RecyclerItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean = true
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position < spanCount) outRect.top = spacing else outRect.top = 0
        outRect.bottom = spacing

        //当前Item处于第几列
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = spacing
            if ((position + 1) % spanCount == 0) outRect.right = spacing else outRect.right = 0
            /*outRect.left = spacing - column * spacing / spanCount
            outRect.right = (1 + column) * spacing / spanCount*/

            if (position < spanCount) outRect.top = spacing else outRect.top = 0
            outRect.bottom = spacing
        } else {
            if ((position + 1) % spanCount == 0) outRect.right = 0 else outRect.right = spacing
            /*  outRect.left = column * spacing / spanCount
              outRect.right = spacing - (column + 1) * spacing / spanCount*/

            if (position >= spanCount) outRect.top = spacing else outRect.top = 0
        }
    }
}