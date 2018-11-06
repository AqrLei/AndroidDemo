package com.open.aqrlei.ipc

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_ipc.*


/**
 * @author  aqrLei on 2018/7/20
 */
class IPCActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val RECEIVE_FROM_SERVICE_CODE_INIT = 1
        const val RECEIVE_FROM_SERVICE_CODE_NORMAL = 11
        const val RECEIVE_FROM_SERVICE_DATA = "receiveDataFromService"
        fun open(context: Context) {
            val intent = Intent(context, IPCActivity::class.java)
            if (queryActivities(context, intent)) {
                context.startActivity(intent)
            }
        }

        internal class ClientMessengerHandler(private val activity: IPCActivity) : Handler() {
            var service: Messenger? = null
            override fun handleMessage(msg: Message?) {
                when (msg?.what) {
                    RECEIVE_FROM_SERVICE_CODE_INIT -> {
                        service?.let {
                            activity.sendMsgNormal(it)
                        }
                    }
                    RECEIVE_FROM_SERVICE_CODE_NORMAL -> {
                        activity.setContextText(msg.data)
                    }
                }
                super.handleMessage(msg)
            }
        }
    }


    private var mBinder: IBinderPool? = null
    private val clientMessengerHandler: ClientMessengerHandler
            by lazy {
                ClientMessengerHandler(this)
            }
    private var listenerManager: IListenerManager? = null

    private val changeListener: IChangeListener
            by lazy {
                object : IChangeListener.Stub() {
                    override fun msgChange(info: Info?) {
                        runOnUiThread {
                            info?.let {
                                contentAIDLTv.text = "msg: ${it.data}, times: ${it.times}"
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
                            listenerManager = IListenerManager.Stub.asInterface(it.queryBinder(IPCService.AIDL_BINDER_CODE))
                        }

                    }

                    override fun onServiceDisconnected(name: ComponentName?) {

                    }
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipc)
        setListener()
    }

    private fun setListener() {
        bindServiceTv.setOnClickListener(this)
        unBindServiceTv.setOnClickListener(this)
        sendMsgTv.setOnClickListener(this)
        setChangeListenerTv.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bindServiceTv -> {
                val intent = Intent(this, IPCService::class.java)
                bindService(intent, mCon, Service.BIND_AUTO_CREATE)
            }
            R.id.unBindServiceTv -> {
                unbindService(mCon)
                clientMessengerHandler.service = null
            }
            R.id.sendMsgTv -> {
                clientMessengerHandler.service?.let {
                    sendMsgInit(it)
                }
            }
            R.id.setChangeListenerTv -> {
                listenerManager?.setChangeListener(changeListener)
            }
        }
    }

    fun setContextText(bundle: Bundle) {
        contentTv.text = bundle.getString(RECEIVE_FROM_SERVICE_DATA)
    }

    fun sendMsgNormal(service: Messenger) {
        service.send(Message.obtain(null, IPCService.RECEIVE_FROM_CLIENT_CODE_NORMAL))
    }

    private fun sendMsgInit(service: Messenger) {
        service.send(
                Message.obtain(null, IPCService.RECEIVE_FROM_CLIENT_CODE_INIT).apply {
                    replyTo = Messenger(clientMessengerHandler)
                }
        )
    }
}