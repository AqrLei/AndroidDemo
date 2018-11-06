package aqrlei.open.com.dialogfragmentsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val dialog = SimpleDialogFragment.newInstance().setTitleText("测试")
                .setContentText("这就是一个测试")
                .setNegativeTask {
                    Toast.makeText(this@MainActivity, "negative测试", Toast.LENGTH_SHORT).show()
                }
                .setPositiveTask {
                    Toast.makeText(this@MainActivity, "positive测试", Toast.LENGTH_SHORT).show()
                }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showDialogTv.setOnClickListener {
            dialog.show(supportFragmentManager)
        }
    }
}
