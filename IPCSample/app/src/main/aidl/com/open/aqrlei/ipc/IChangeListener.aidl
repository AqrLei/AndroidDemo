// IChangeListener.aidl
package com.open.aqrlei.ipc;
import com.open.aqrlei.ipc.Info;
// Declare any non-default types here with import statements

interface IChangeListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void msgChange(in Info info);
}
