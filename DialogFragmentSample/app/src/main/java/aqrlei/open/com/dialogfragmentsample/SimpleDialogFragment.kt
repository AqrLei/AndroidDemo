package aqrlei.open.com.dialogfragmentsample

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fragment_simple_dialog.*

/**
 * @author  aqrLei on 2018/7/23
 */
class SimpleDialogFragment : DialogFragment() {
    companion object {
        private const val simpleTag = "simple_dialog_fragment"
        fun newInstance() = SimpleDialogFragment()
    }

    private var title: String? = null
    private var content: String? = null
    private var bitmap: Bitmap? = null
    private var negativeTask: (() -> Unit?)? = null
    private var positiveTask: (() -> Unit?)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SimpleDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
            val width = (metrics.widthPixels * 0.75F).toInt()
            dialog.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_simple_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        title?.let {
            titleTv.text = if (it.isEmpty()) "提示" else it
            titleTv.visibility = View.VISIBLE
        }
        content?.let {
            contentTv.text = it
        }
        negativeTask?.run {
            negativeTv.setOnClickListener {
                this.invoke()
                dismiss()
            }
            negativeTv.visibility = View.VISIBLE
        }
        positiveTask?.run {
            positiveTv.setOnClickListener {
                this.invoke()
                dismiss()
            }
            positiveTv.visibility = View.VISIBLE
        }
        bitmap?.run {
            contentIv.setImageBitmap(this)
            contentIv.visibility = View.VISIBLE
        }
    }

    fun setTitleText(title: String): SimpleDialogFragment {
        this.title = title
        return this
    }

    fun setContentText(content: String): SimpleDialogFragment {
        this.content = content
        return this
    }

    fun setContentImage(bitmap: Bitmap): SimpleDialogFragment {
        this.bitmap = bitmap
        return this
    }

    fun setNegativeTask(negativeTask: () -> Unit): SimpleDialogFragment {
        this.negativeTask = negativeTask
        return this
    }

    fun setPositiveTask(positiveTask: () -> Unit): SimpleDialogFragment {
        this.positiveTask = positiveTask
        return this
    }

    fun show(manager: FragmentManager?) {
        super.show(manager, simpleTag)
    }
}