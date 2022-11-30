package cn.sinowonder.simonteeth.interfaces.peripheral

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/22 13:30
 * @since:V1
 * @desc:cn.sinowonder.simonteeth.interface
 */
interface ServerConnectListener {

    fun onConnectSuccess(device: BluetoothDevice?, status: Int, newState: Int)
    fun onDisconnect(device: BluetoothDevice?, status: Int, newState: Int)
    fun onServicesAdded(status: Int, service: BluetoothGattService?)


}