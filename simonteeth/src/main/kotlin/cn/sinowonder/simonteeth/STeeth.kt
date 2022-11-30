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
object STeeth : BluetoothGattCallback() {

    lateinit var mBluetoothManager: BluetoothManager
    lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mCurrentBleDevice: BluetoothDevice
    private lateinit var mCurrentBleGatt: BluetoothGatt
    val mConnectedBleDeviceList = arrayListOf<STeethDevice>()
    val mCharacteristicListenerMap = ArrayMap<Int, CharacteristicListener>()
    lateinit var mConnectListener: ConnectListener
    lateinit var mMtuListener: MtuListener
    lateinit var mRssiListener: RssiListener
    val stopExecutors = Executors.newSingleThreadScheduledExecutor()


    //默认扫描BLE蓝牙时长
    var mScanTime = 10000L


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
     * 初始化
     *
     * @param context app上下文
     */
    fun init(bluetoothManager: BluetoothManager) {
        this.mBluetoothManager = bluetoothManager
        this.mBluetoothAdapter = bluetoothManager.adapter
    }


    /**
     * 开启ble扫描
     *
     * @param scanCallback 扫描回调
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startLeScan(
        scanCallback: ScanCallback,
        scanTime: Long = mScanTime,
        filters: List<ScanFilter>? = null,
        scanSettings: ScanSettings = ScanSettings.Builder().build()
    ) {

        mBluetoothAdapter.bluetoothLeScanner.startScan(filters, scanSettings, scanCallback)

        stopExecutors.schedule(Callable {
            mBluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        }, scanTime, TimeUnit.MILLISECONDS)


    }


    /**
     * 获取外围设备广播信息
     *
     * @param address 外围设备ble地址Mac
     * @return
     */
    fun getRemoteDevice(address: String): BluetoothDevice {
        mCurrentBleDevice = mBluetoothAdapter.getRemoteDevice(address)



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
        super.onPhyUpdate(gatt, txPhy, rxPhy, status)
    }

    override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyRead(gatt, txPhy, rxPhy, status)


    }

    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
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
        super.onServicesDiscovered(gatt, status)
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
        super.onCharacteristicRead(gatt, characteristic, status)

        this.mCharacteristicListenerMap.forEach { (tag, listener) ->
            listener.onCharacteristicRead(tag, gatt, characteristic, status)

        }

    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)

        this.mCharacteristicListenerMap.forEach { (tag, listener) ->
            listener.onCharacteristicWrite(tag, gatt, characteristic, status)
        }

    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicChanged(gatt, characteristic)
        this.mCharacteristicListenerMap.forEach { (tag, listener) ->
            listener.onCharacteristicChange(tag, gatt, characteristic)
        }
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorRead(gatt, descriptor, status)
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        super.onDescriptorWrite(gatt, descriptor, status)
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
        super.onReliableWriteCompleted(gatt, status)
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        super.onReadRemoteRssi(gatt, rssi, status)
        this.mRssiListener.onReadRemoteRssi(gatt, rssi, status)
    }


    @SuppressLint("MissingPermission")
    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
        gatt?.discoverServices()
        mMtuListener.onMtuChange(gatt, mtu, status)
    }

    override fun onServiceChanged(gatt: BluetoothGatt) {
        super.onServiceChanged(gatt)
        this.mConnectListener.onServicesChange(gatt)
    }
}