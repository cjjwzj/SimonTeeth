package cn.sinowonder.simonteeth.interfaces.central

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
interface ReliableListener {

    fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int)


}