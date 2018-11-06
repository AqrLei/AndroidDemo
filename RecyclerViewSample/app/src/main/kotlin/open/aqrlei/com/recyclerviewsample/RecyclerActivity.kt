package open.aqrlei.com.recyclerviewsample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_recycler.*

/**
 * @author  aqrLei on 2018/7/20
 */
fun queryActivities(context: Context, intent: Intent): Boolean {
    return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY) != null
}

class RecyclerActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        fun open(context: Context) {
            val intent = Intent(context, RecyclerActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linearTv -> {
                setRecyclerView(RecyclerType.LINEAR)

            }
            R.id.gridTv -> {
                setRecyclerView(RecyclerType.CRID)
            }
            R.id.staggeredTv -> {
                setRecyclerView(RecyclerType.STAGGERED)
            }
        }
    }

    private fun setRecyclerView(type: RecyclerType) {
        val data = arrayListOf("one", "two", "three", "one-two-three-four-five-six-seven-eight-nine-ten-eleven-twelve",
                "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve")
        when (type) {
            RecyclerType.LINEAR -> {
                recyclerV.apply {
                    layoutManager = LinearLayoutManager(this@RecyclerActivity, RecyclerView.VERTICAL, false)
                    adapter = RecyclerSimpleAdapter(this@RecyclerActivity, data)
                    addItemDecoration(DividerItemDecoration(this@RecyclerActivity, RecyclerView.VERTICAL))
                }

            }
            RecyclerType.CRID -> {
                recyclerV.apply {
                    /*一行分成二列*/
                    layoutManager = GridLayoutManager(this@RecyclerActivity, 2, RecyclerView.VERTICAL, false).also {
                        it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                Log.d("recycler", "position: $position,data: ${data[position]}")
                                return if (position <= 7) 1// 占二分之一
                                else 1// 占二分之一
                            }
                        }
                    }
                    adapter = RecyclerSimpleAdapter(this@RecyclerActivity, data)
                }

            }
            RecyclerType.STAGGERED -> {
                /*交错网格布局*/
                recyclerV.apply {
                    layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                    adapter = RecyclerSimpleAdapter(this@RecyclerActivity, data)
                }

            }
        }
    }

    private enum class RecyclerType {
        LINEAR, CRID, STAGGERED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        linearTv.setOnClickListener(this)
        gridTv.setOnClickListener(this)
        staggeredTv.setOnClickListener(this)
    }

}