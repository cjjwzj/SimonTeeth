package cn.sinowonder.simonteeth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.ArrayMap
import androidx.annotation.RequiresPermission
import cn.sinowonder.simonteeth.entity.STeethDevice
import cn.sinowonder.simonteeth.interfaces.central.CharacteristicListener
import cn.sinowonder.simonteeth.interfaces.central.ConnectListener
import cn.sinowonder.simonteeth.interfaces.central.MtuListener
import cn.sinowonder.simonteeth.interfaces.central.RssiListener
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/10 14:48
 * @since:V1
 * @desc:cn.sinowonder.simonteeth
 */
object STeethCen : BluetoothGattCallback() {


    private lateinit var mCurrentBleDevice: BluetoothDevice
    private lateinit var mCurrentBleGatt: BluetoothGatt


    val mConnectedBleDeviceList = arrayListOf<STeethDevice>()


    //回调监听器
    val mCharacteristicListenerMap = ArrayMap<Int, CharacteristicListener>()
    lateinit var mConnectListener: ConnectListener
    lateinit var mMtuListener: MtuListener
    lateinit var mRssiListener: RssiListener


    val stopExecutors = Executors.newSingleThreadScheduledExecutor()


    fun getLastConnectedDevice() = mCurrentBleDevice

    fun getLastConnectedGatt() = mCurrentBleGatt


    fun addCharacteristicListener(
        listenerTag: Int,
        characteristicListener: CharacteristicListener
    ) {
        this.mCharacteristicListenerMap[listenerTag] = characteristicListener
    }

    fun removeCharacteristicListener(listenerTag: Int) {
        this.mCharacteristicListenerMap.remove(listenerTag)

    }

    fun setConnectListener(
        connectListener: ConnectListener
    ) {
        this.mConnectListener = connectListener
    }

    fun setMtuListener(
        mtuListener: MtuListener
    ) {
        this.mMtuListener = mtuListener
    }

    fun setRssiListener(
        rssiListener: RssiListener
    ) {
        this.mRssiListener = rssiListener
    }


    /**
     * 开启ble扫描
     *
     * @param scanCallback 扫描回调
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startLeScan(
        scanCallback: ScanCallback,
        scanTime: Long = SimonCore.mScanTime,
        filters: List<ScanFilter>? = SimonCore.mScanFilterList,
        scanSettings: ScanSettings = SimonCore.mScanSettings
    ) {
        SimonCore.mBluetoothAdapter.bluetoothLeScanner.startScan(
            filters,
            scanSettings,
            scanCallback
        )
        stopExecutors.schedule(Callable {
            SimonCore.mBluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        }, scanTime, TimeUnit.MILLISECONDS)
    }


    /**
     * 获取外围设备广播信息
     *
     * @param address 外围设备ble地址Mac
     * @return 通过MAC获取到的外围设备
     */
    fun getRemoteDevice(address: String): BluetoothDevice {
        mCurrentBleDevice = SimonCore.mBluetoothAdapter.getRemoteDevice(address)
        return mCurrentBleDevice
    }

    @SuppressLint("MissingPermission")
    fun disconnect(bluetoothGatt: BluetoothGatt) {

        bluetoothGatt.disconnect()
    }


    @SuppressLint("MissingPermission")
    fun connect(
        context: Context,
        isAutoConnect: Boolean,
        bluetoothDevice: BluetoothDevice
    ): BluetoothGatt {

        mCurrentBleGatt = bluetoothDevice.connectGatt(context, isAutoConnect, this)

        return mCurrentBleGatt

    }


    override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
    }

    override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
    }

    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                gatt?.device?.let { STeethDevice(it) }?.let { mConnectedBleDeviceList.add(it) }
                mConnectedBleDeviceList.last().gatt = gatt
                mConnectListener.onConnectSuccess()
                gatt?.discoverServices()

            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                mConnectedBleDeviceList.removeIf {
                    it.macAddress == gatt?.device?.address
                }
                mConnectListener.onDisconnect()
                gatt?.close()
            }

        }


    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                gatt?.services?.let {
                    mConnectedBleDeviceList.last().serviceList = it
                    mConnectListener.onServicesDiscovered(it)
                }
            }
            else -> {
                mConnectListener.onServicesDiscoverFailed(status)

            }

        }

    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        this.mCharacteristicListenerMap.forEach { (tag, listener) ->
            listener.onCharacteristicRead(tag, gatt, characteristic, status)
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {

        this.mCharacteristicListenerMap.forEach { (tag, listener) ->
            listener.onCharacteristicWrite(tag, gatt, characteristic, status)
        }

    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        this.mCharacteristicListenerMap.forEach { (tag, listener) ->
            listener.onCharacteristicChange(tag, gatt, characteristic)
        }
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        this.mRssiListener.onReadRemoteRssi(gatt, rssi, status)
    }


    @SuppressLint("MissingPermission")
    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        gatt?.discoverServices()
        mMtuListener.onMtuChange(gatt, mtu, status)
    }

    override fun onServiceChanged(gatt: BluetoothGatt) {
        this.mConnectListener.onServicesChange(gatt)
    }
}