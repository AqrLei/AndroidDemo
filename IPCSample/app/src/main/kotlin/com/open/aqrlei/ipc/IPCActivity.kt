package com.open.aqrlei.ipc

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.open.aqrlei.ipc.contentprovider.OrderProvider
import com.open.aqrlei.ipc.file.FileStreamUtil
import kotlinx.android.synthetic.main.activity_ipc.*
import java.io.*
import java.lang.ref.WeakReference
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread


/**
 * @author  aqrLei on 2018/7/20
 */
class IPCActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val RECEIVE_FROM_SERVICE_CODE_INIT = 1
        const val RECEIVE_FROM_SERVICE_CODE_NORMAL = 11
        const val LOCAL_SOCKET_CONNECTED = 21
        const val LOCAL_SOCKET_SEND_MESSAGE = 22
        const val RECEIVE_FROM_SERVICE_CODE_FILE = 111
        const val RECEIVE_FROM_SERVICE_DATA = "receiveDataFromService"
        fun start(context: Context) {
            val intent = Intent(context, IPCActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }
    }


    private var mBinder: IBinderPool? = null
    private val clientMessengerHandler: ClientMessengerHandler
            by lazy {
                ClientMessengerHandler(WeakReference(this))
            }
    private var listenerManager: IListenerManager? = null

    private val changeListener: IChangeListener
            by lazy {
                object : IChangeListener.Stub() {
                    override fun msgChange(info: Info?) {
                        runOnUiThread {
                            info?.let {
                                val msg = "${it.data}-$time"
                                setFirstContentText(msg)
                            }
                        }
                    }
                }
            }
    private val mCon: ServiceConnection
            by lazy {
                object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        mBinder = IBinderPool.Stub.asInterface(service)
                        mBinder?.let {
                            clientMessengerHandler.service = Messenger(it.queryBinder(IPCService.MESSENGER_BINDER_CODE))
                            sendMsgInit(clientMessengerHandler.service!!)
                            listenerManager = IListenerManager.Stub.asInterface(it.queryBinder(IPCService.AIDL_BINDER_CODE))
                            listenerManager?.setChangeListener(changeListener)
                        }
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {

                    }
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipc)
        val intent = Intent(this, IPCService::class.java)
        bindService(intent, mCon, Service.BIND_AUTO_CREATE)
        setListener()
        firstContentTv.movementMethod = ScrollingMovementMethod.getInstance()
        file = FileStreamUtil.getCacheFile(this)
        thread {
            connectTcpServer()
        }

    }

    private fun setListener() {
        unBindServiceTv.setOnClickListener(this)
        socketTv.setOnClickListener(this)
        fileTestTv.setOnClickListener(this)
        contentProviderTv.setOnClickListener(this)
        contentProviderTv.isEnabled = false

    }

    private val time: String
        get() = SimpleDateFormat("hh:mm:ss.SSS", Locale.ROOT).format(System.currentTimeMillis())
    private val threadPool = Executors.newSingleThreadExecutor()
    private var file: File? = null
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fileTestTv -> {
                file?.let {
                    val str = "Write by Client-$time"
                    FileStreamUtil.writeChar(it, str)
                    clientMessengerHandler.service?.let { service ->
                        notifyFileChange(service)
                    }
                }
            }
            R.id.unBindServiceTv -> {
                unbindService(mCon)
                clientMessengerHandler.service = null
            }
            R.id.socketTv -> {
                threadPool.execute {
                    mPrintWriter?.println("Hello Socket:$time")
                }
            }
            R.id.contentProviderTv -> {
                contentResolver.insert(OrderProvider.ORDER_URL, null)
                clientMessengerHandler.service?.let {
                    sendMsgNormal(it)
                }
            }
        }
    }


    fun setSecondContentText(bundle: Bundle) {
        secondContentTv.text = ""
        secondContentTv.append(bundle.getString(RECEIVE_FROM_SERVICE_DATA))
        secondContentTv.append("\n")
    }


    fun setFirstContentText(str: String) {
        firstContentTv.append(str)
        firstContentTv.append("\n")
    }

    fun receiveFileChange() {
        file?.let { file ->
            FileStreamUtil.readChar(file) {
                setFirstContentText("File: $it-$time")
            }
        }

    }

    fun sendMsgNormal(service: Messenger) {
        service.send(Message.obtain(null, IPCService.RECEIVE_FROM_CLIENT_CODE_NORMAL))
    }

    private fun notifyFileChange(service: Messenger) {
        service.send(Message.obtain(null, IPCService.RECEIVE_FROM_CLIENT_CODE_FILE))
    }

    private fun sendMsgInit(service: Messenger) {
        service.send(
                Message.obtain(null, IPCService.RECEIVE_FROM_CLIENT_CODE_INIT).apply {
                    replyTo = Messenger(clientMessengerHandler)
                }
        )
    }


    private var mClientSocket: Socket? = null
    private var mPrintWriter: PrintWriter? = null
    private fun connectTcpServer() {
        var socket: Socket? = null
        while (socket == null) {
            try {
                socket = Socket("localhost", 9999)
                mClientSocket = socket
                mPrintWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                clientMessengerHandler.sendEmptyMessage(LOCAL_SOCKET_CONNECTED)
            } catch (e: IOException) {
                SystemClock.sleep(1000)
            }
        }

        try {
            BufferedReader(InputStreamReader(socket.getInputStream())).use {
                while (!this.isFinishing) {
                    var msg = it.readLine()
                    if (!msg.isNullOrEmpty()) {
                        msg = "$msg-$time"
                        clientMessengerHandler.obtainMessage(LOCAL_SOCKET_SEND_MESSAGE, msg).sendToTarget()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onDestroy() {
        if (mClientSocket != null) {
            try {
                mPrintWriter?.close()
                mClientSocket?.shutdownInput()
                mClientSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onDestroy()
    }

    class ClientMessengerHandler(private val activity: WeakReference<IPCActivity>) : Handler(Looper.getMainLooper()) {
        var service: Messenger? = null
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                RECEIVE_FROM_SERVICE_CODE_INIT -> {
                    service?.let {
                        activity.get()?.sendMsgNormal(it)
                    }
                }
                RECEIVE_FROM_SERVICE_CODE_NORMAL -> {
                    activity.get()?.setSecondContentText(msg.data)
                }
                RECEIVE_FROM_SERVICE_CODE_FILE -> {
                    activity.get()?.receiveFileChange()
                }
                LOCAL_SOCKET_CONNECTED -> { // socket 连接创建完毕
                    activity.get()?.contentProviderTv?.isEnabled = true
                }
                LOCAL_SOCKET_SEND_MESSAGE -> { // socket 回传信息
                    val time = SimpleDateFormat("hh:mm:ss.SSS", Locale.ROOT).format(System.currentTimeMillis())
                    val message = (msg.obj?.toString() ?: "Empty") + "-$time"
                    activity.get()?.setFirstContentText(message)
                }
            }
            super.handleMessage(msg)
        }
    }
}