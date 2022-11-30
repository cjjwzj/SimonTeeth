package cn.sinowonder.simonteeth.interfaces.central

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/22 13:30
 * @since:V1
 * @desc:cn.sinowonder.simonteeth.interface
 */
interface CharacteristicListener {

    fun onCharacteristicRead(
        listenerTag: Int, gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    )

    fun onCharacteristicWrite(
        listenerTag: Int, gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    )

    fun onCharacteristicChange(
        listenerTag: Int, gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    )


}