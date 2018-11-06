package open.aqrlei.com.fragmentsample


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_simple_fragment.*

/**
 * @author  aqrLei on 2018/7/25
 */
fun queryActivities(context: Context, intent: Intent): Boolean {
    return context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY) != null
}

class SimpleFragmentActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val FRAGMENT_TAG = "fragment_tag"

        fun open(context: Context) {
            val intent = Intent(context, SimpleFragmentActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }

    private var bundle: Bundle? = null
    private var count = 0
    private lateinit var fragment: SimpleFragment
    override fun onClick(v: View?) {
        fragment = bundle?.let {
            supportFragmentManager.findFragmentByTag(FRAGMENT_TAG + count.toString()) as? SimpleFragment
                    ?: SimpleFragment.newInstance()
        } ?: SimpleFragment.newInstance()
        count++
        val t = supportFragmentManager.beginTransaction()
        when (v?.id) {
            R.id.addTv -> {

                t.add(R.id.containerLl, fragment, FRAGMENT_TAG + count.toString())
                //  t.show(fragment)
                t.commit()
                Log.d("test", "add: $count")
            }
            R.id.replaceTv -> {
                Log.d("test", "replace: $count")
                t.replace(R.id.containerLl, fragment, FRAGMENT_TAG + count.toString())
                t.commit()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_fragment)
        bundle = savedInstanceState
        addTv.setOnClickListener(this)
        replaceTv.setOnClickListener(this)

    }
}