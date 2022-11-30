package cn.sinowonder.simonteeth.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import cn.sinowonder.simonteeth.STeeth

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/10 14:28
 * @since:V1
 * @desc:cn.sinowonder.simonteeth.utils
 */
object STUtils {


    const val REQUEST_OPEN_BLE = 0x44


    /**
     * 是否支持Ble蓝牙
     *
     *
     * @return true=支持 false=不支持
     */
    fun isSupportedBle(context: Context): Boolean {

        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

    }


    /**
     * 直接开启手机蓝牙开关
     *
     */
    @SuppressLint("MissingPermission")
    fun enableBlueToothSilently() {
        STeeth.mBluetoothAdapter.enable()
    }


    /**
     * 关闭手机蓝牙
     *
     */
    @SuppressLint("MissingPermission")
    fun closeBlueTooth() {
        STeeth.mBluetoothAdapter.disable()

    }

    /**
     * 跳转蓝牙开启界面
     *
     */
    @SuppressLint("MissingPermission")
    fun enableBlueToothWithDialog(activity: AppCompatActivity) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBtIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivityForResult(enableBtIntent, REQUEST_OPEN_BLE)

    }

    /**
     * 读取远程连接设备信号强度
     *
     * @param bluetoothGatt 回调在 BluetoothGattCallback.onReadRemoteRssi中
     */
    @SuppressLint("MissingPermission")
    fun readReadRssi(bluetoothGatt: BluetoothGatt) {
        bluetoothGatt.readRemoteRssi()
    }

    /**
     * 请求更改通信最大字节数
     *
     * @param bluetoothGatt
     * @param mtu 最大字节数
     */
    @SuppressLint("MissingPermission")
    fun requestChangeMtu(bluetoothGatt: BluetoothGatt, mtu: Int) {
        bluetoothGatt.requestMtu(mtu)
    }


    /**
     * 是否正在扫描
     *
     * @return true = 扫描中
     */
    @SuppressLint("MissingPermission")
    fun isScanning(): Boolean {


        return STeeth.mBluetoothAdapter.isDiscovering
    }

    /**
     * 识别特征功能
     *
     * @param characteristic service中的特征
     * @return
     */
    fun detectCharacteristic(characteristic: BluetoothGattCharacteristic): String {

        val propSb = StringBuilder()
        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            propSb.append("Read")

        }
        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
            if (propSb.isNotEmpty()) {
                propSb.append("   ")
            }
            propSb.append("Write")
        }

        if (characteristic.properties and BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE > 0) {
            if (propSb.isNotEmpty()) {
                propSb.append("   ")
            }
            propSb.append("Write No Response")
        }

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            if (propSb.isNotEmpty()) {
                propSb.append("   ")
            }
            propSb.append("Notify")
        }

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
            if (propSb.isNotEmpty()) {
                propSb.append("   ")
            }
            propSb.append("Indicate")
        }

        return propSb.toString()
    }

}