package cn.sinowonder.simonteeth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/12/2 13:42
 * @since:V1
 * @desc:cn.sinowonder.simonteeth
 */
object SimonCore {


    lateinit var mBluetoothManager: BluetoothManager
    lateinit var mBluetoothAdapter: BluetoothAdapter

    //默认扫描BLE蓝牙时长10s 可更改
    var mScanTime = 10000L

    var mScanFilterList = arrayListOf<ScanFilter>()

    var mScanSettings = ScanSettings.Builder().build()


    /**
     * 初始化
     *
     * @param bluetoothManager android 蓝牙管理类
     *                         通过 getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager 获取
     */
    fun init(bluetoothManager: BluetoothManager) {
        this.mBluetoothManager = bluetoothManager
        this.mBluetoothAdapter = bluetoothManager.adapter
    }

}