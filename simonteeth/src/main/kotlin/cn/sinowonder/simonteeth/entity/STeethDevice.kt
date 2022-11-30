package cn.sinowonder.simonteeth.entity

import android.annotation.SuppressLint
import android.bluetooth.*

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/23 13:24
 * @since:V1
 * @desc:cn.sinowonder.simonteeth.entity
 */
@SuppressLint("MissingPermission")
data class STeethDevice(val originDevice:BluetoothDevice){

    val deviceName:String = originDevice.name
    var macAddress:String = originDevice.address
    var gatt:BluetoothGatt? = null
    var serviceList:List<BluetoothGattService>? = null


}
