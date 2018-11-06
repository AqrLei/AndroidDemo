package open.aqrlei.com.rxjavasample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_main.*
import open.aqrlei.com.rxjavasample.rxjava.CombinationOperator
import open.aqrlei.com.rxjavasample.rxjava.CreateOperator
import open.aqrlei.com.rxjavasample.rxjava.FilterOperator
import open.aqrlei.com.rxjavasample.rxjava.TransformOperator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val content = StringBuilder()
        contentTv.movementMethod = ScrollingMovementMethod.getInstance()

        createTv.setOnClickListener {
            content.delete(0, content.length)
            CreateOperator.subscribe { text ->
                content.append("\n")
                content.append(text)
                contentTv.text = content
            }
        }
        transformTv.setOnClickListener {
            content.delete(0, content.length)
            TransformOperator.subscribe { text ->
                content.append("\n")
                content.append(text)
                contentTv.text = content
            }
        }
        filterTv.setOnClickListener {
            content.delete(0, content.length)
            FilterOperator.subscribe { text ->
                content.append("\n")
                content.append(text)
                contentTv.text = content
            }
        }
        combinationTv.setOnClickListener {
            content.delete(0, content.length)
            CombinationOperator.subscribe { text ->
                content.append("\n")
                content.append(text)
                contentTv.text = content
            }
        }
    }
}
