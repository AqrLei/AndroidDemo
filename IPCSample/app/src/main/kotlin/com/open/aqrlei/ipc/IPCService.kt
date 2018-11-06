package com.open.aqrlei.ipc

import android.app.Service
import android.content.Intent
import android.os.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author  aqrLei on 2018/7/20
 */
class IPCService : Service() {
    companion object {
        const val MESSENGER_BINDER_CODE = 0
        const val AIDL_BINDER_CODE = 1
        const val RECEIVE_FROM_CLIENT_CODE_INIT = 2
        const val RECEIVE_FROM_CLIENT_CODE_NORMAL = 21
        const val RECEIVE_FROM_CLIENT_DATA = "receiveDataFromClient"
    }

    private var changeListener: IChangeListener? = null
    private var client: Messenger? = null
    private var job: Job? = null
    private var serviceMessengerHandler: Handler? = null

    private val mIBinderPool = object : IBinderPool.Stub() {
        @Throws(RemoteException::class)
        override fun queryBinder(binderCode: Int): IBinder {
            return when (binderCode) {
                MESSENGER_BINDER_CODE -> {
                    /*此处必须加上Looper.getMainLooper()*/
                    serviceMessengerHandler = object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message?) {
                            when (msg?.what) {
                                RECEIVE_FROM_CLIENT_CODE_INIT -> {
                                    if (client != msg.replyTo) {
                                        client = msg.replyTo
                                    }
                                    client?.let {
                                        sendMsgInit(it)
                                    }
                                }
                                RECEIVE_FROM_CLIENT_CODE_NORMAL -> {
                                    client?.let { sendMsg(it) }
                                }
                                else -> {
                                    super.handleMessage(msg)
                                }
                            }
                        }
                    }
                    Messenger(serviceMessengerHandler).binder
                }
                else -> {
                    mListenerManager
                }
            }
        }
    }
    private val mListenerManager = object : IListenerManager.Stub() {
        override fun setChangeListener(listener: IChangeListener?) {
            changeListener = listener
            job = launch {
                var i = 0
                while (true) {
                    i++
                    Thread.sleep(1000L)
                    changeListener?.msgChange(Info("receive from service", i))
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mIBinderPool
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        clear()
        super.onDestroy()
    }

    private fun clear() {
        serviceMessengerHandler = null
        job = null
        changeListener = null
        client = null
    }

    fun sendMsgInit(client: Messenger) {
        client.send(Message.obtain(null, IPCActivity.RECEIVE_FROM_SERVICE_CODE_INIT))
    }

    fun sendMsg(client: Messenger) {
        client.send(
                Message.obtain(
                        null,
                        IPCActivity.RECEIVE_FROM_SERVICE_CODE_NORMAL)
                        .apply {
                            data = Bundle().also {
                                it.putString(
                                        IPCActivity.RECEIVE_FROM_SERVICE_DATA,
                                        "receive msg from service")
                            }
                        })
    }
}