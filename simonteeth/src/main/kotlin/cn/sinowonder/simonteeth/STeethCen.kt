package cn.sinowonder.simonteeth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import androidx.annotation.RequiresPermission
import cn.sinowonder.simonteeth.entity.STeethDevice
import cn.sinowonder.simonteeth.interfaces.central.*
import java.util.*
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


    val mConnectedBleDeviceList = arrayListOf<STeethDevice>()


    lateinit var mConnectListener: ConnectListener
    lateinit var mMtuListener: MtuListener
    lateinit var mRssiListener: RssiListener
    lateinit var mNotifyListener: NotifyListener
    lateinit var mCharacteristicListener: CharacteristicListener
    lateinit var mPhyListener: PhyListener
    lateinit var mReliableListener: ReliableListener
    lateinit var mDescriptorListener: DescriptorListener


    val stopExecutors = Executors.newSingleThreadScheduledExecutor()


    fun setCharacteristicListener(
        characteristicListener: CharacteristicListener
    ) {
        this.mCharacteristicListener = characteristicListener
    }

    fun setDescriptorListener(
        descriptorListener: DescriptorListener
    ) {
        this.mDescriptorListener = descriptorListener
    }


    fun setNotifyListener(
        notifyListener: NotifyListener
    ) {
        this.mNotifyListener = notifyListener
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

    fun setPhyListener(
        phyListener: PhyListener
    ) {
        this.mPhyListener = phyListener
    }

    fun setRssiListener(
        rssiListener: RssiListener
    ) {
        this.mRssiListener = rssiListener
    }

    fun serReliableListener(
        reliableListener: ReliableListener
    ) {
        this.mReliableListener = reliableListener
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
        return SimonCore.mBluetoothAdapter.getRemoteDevice(address)

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
    ) {

        bluetoothDevice.connectGatt(context, isAutoConnect, this)


    }

    @SuppressLint("MissingPermission")
    fun subscribeNotify(
        bleGatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        descriptorUUID: UUID
    ) {
        bleGatt.setCharacteristicNotification(characteristic, true)
        val clientConfig = characteristic.getDescriptor(descriptorUUID);
        clientConfig.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
        bleGatt.writeDescriptor(clientConfig);

    }

    @SuppressLint("MissingPermission")
    fun unsubscribeNotify(
        bleGatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        descriptorUUID: UUID
    ) {
        bleGatt.setCharacteristicNotification(characteristic, false)
        val clientConfig = characteristic.getDescriptor(descriptorUUID);
        clientConfig.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
        bleGatt.writeDescriptor(clientConfig)

    }


    override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        this.mPhyListener.onPhyUpdate(gatt, txPhy, rxPhy, status)
    }

    override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
        this.mPhyListener.onPhyRead(gatt, txPhy, rxPhy, status)

    }

    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                gatt?.device?.let { STeethDevice(it) }?.let { mConnectedBleDeviceList.add(it) }
                mConnectedBleDeviceList.last().gatt = gatt
                gatt?.let { this.mConnectListener.onConnectSuccess(it) }
                gatt?.discoverServices()

            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                mConnectedBleDeviceList.removeIf {
                    it.macAddress == gatt?.device?.address
                }
                this.mConnectListener.onDisconnect()
                gatt?.close()
            }

        }


    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                gatt?.services?.let {
                    mConnectedBleDeviceList.last().serviceList = it
                    this.mConnectListener.onServicesDiscovered(it)
                }
            }
            else -> {
                this.mConnectListener.onServicesDiscoverFailed(status)

            }

        }

    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        this.mCharacteristicListener.onCharacteristicRead(gatt, characteristic, status)
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {

        this.mCharacteristicListener.onCharacteristicWrite(gatt, characteristic, status)

    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {

        this.mNotifyListener.onCharacteristicChange(gatt, characteristic)
    }

    override fun onDescriptorRead(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {
        this.mDescriptorListener.onDescriptorRead(gatt, descriptor, status)

    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt?,
        descriptor: BluetoothGattDescriptor?,
        status: Int
    ) {

        this.mDescriptorListener.onDescriptorWrite(gatt, descriptor, status)
    }

    override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
        this.mReliableListener.onReliableWriteCompleted(gatt, status)

    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        this.mRssiListener.onReadRemoteRssi(gatt, rssi, status)
    }


    @SuppressLint("MissingPermission")
    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        gatt?.discoverServices()
        this.mMtuListener.onMtuChange(gatt, mtu, status)
    }

    override fun onServiceChanged(gatt: BluetoothGatt) {
        this.mConnectListener.onServicesChange(gatt)
    }
}