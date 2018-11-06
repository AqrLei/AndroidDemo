package open.aqrlei.com.coordinatorsample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import kotlinx.android.synthetic.main.activity_coordinator.*

/**
 * @author  aqrLei on 2018/7/27
 */
fun queryActivities(context: Context, intent: Intent): Boolean {
    return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY) != null
}

class CoordinatorActivity : AppCompatActivity() {
    companion object {
        fun open(context: Context) {
            val intent = Intent(context, CoordinatorActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }

    private val data: ArrayList<String>
            by lazy {
                arrayListOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve")
            }
    private val bufferData: ArrayList<String>
            by lazy {
                arrayListOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve")
            }
    private val mAdapter: RecyclerSimpleAdapter
            by lazy {
                RecyclerSimpleAdapter(this@CoordinatorActivity, data)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator)
        initView()
        initListener()
    }

    private fun initView() {
        listSearchV.isIconified = false // 一开始处于展开状态
        listSearchV.onActionViewExpanded()// 展开无内容时，没有关闭按钮
        listSearchV.setIconifiedByDefault(false) // false时搜索图标在框外，默认true在框内
        listSearchV.queryHint = "输入名称搜索"
        listSearchV.isSubmitButtonEnabled = true // 显示提交按钮

        rv.apply {
            layoutManager = LinearLayoutManager(this@CoordinatorActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    private fun initListener() {
        listSearchV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    queryData("")
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                queryData(query ?: "")
                return true
            }
        })
    }

    private fun queryData(query: String) {
        data.clear()
        if (query.isEmpty()) {
            data.addAll(bufferData)
            mAdapter.notifyDataSetChanged()
        } else {
            bufferData.filter { it.contains(query) }
                    .forEach { data.add(it) }
            mAdapter.notifyDataSetChanged()
        }
    }

}