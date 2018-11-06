package  aqrlei.open.com.viewpagersample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_viewpager.*
import kotlinx.android.synthetic.main.layout_viewpager_item.view.*
/**
 * @author  aqrLei on 2018/7/20
 */
fun queryActivities(context: Context, intent: Intent): Boolean {
    return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY) != null
}
class ViewPagerActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        fun open(context: Context) {
            val intent = Intent(context, ViewPagerActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }

    private val viewList: ArrayList<View>
            by lazy {
                ArrayList<View>().apply {
                    for (i in 0 until 3) {
                        val view = LayoutInflater.from(this@ViewPagerActivity).inflate(R.layout.layout_viewpager_item, null)
                        view.text.text = "测试${i + 1}"
                        add(view)
                    }
                }
            }

    private val pagerListener: TabLayout.ViewPagerOnTabSelectedListener
            by lazy {
                object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {}
            }
    private val tabListener: TabLayout.TabLayoutOnPageChangeListener
            by lazy {
                object : TabLayout.TabLayoutOnPageChangeListener(labelTl) {}
            }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.withTabPagerTv -> {
                labelTl.addOnTabSelectedListener(pagerListener)
                viewPager.addOnPageChangeListener(tabListener)
            }
            R.id.removeTabPagerTv -> {
                labelTl.removeOnTabSelectedListener(pagerListener)
                viewPager.removeOnPageChangeListener(tabListener)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager)
        withTabPagerTv.setOnClickListener(this)
        viewPager.adapter = ViewPagerSimpleAdapter(viewList)
        removeTabPagerTv.setOnClickListener(this)
    }
}