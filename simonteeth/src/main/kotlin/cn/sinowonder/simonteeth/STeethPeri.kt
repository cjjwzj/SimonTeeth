package cn.sinowonder.simonteeth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import cn.sinowonder.simonteeth.interfaces.peripheral.*

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/16 18:51
 * @since:V1
 * @desc:cn.sinowonder.simonteeth
 */
object STeethPeri : BluetoothGattServerCallback() {


    lateinit var mBleGattServer: BluetoothGattServer
    lateinit var mOnServerConnectListener: OnServerConnectListener
    lateinit var mOnServerCharacteristicListener: OnServerCharacteristicListener
    lateinit var mOnServerNotificationSentListener: OnServerNotificationSentListener
    lateinit var mOnServerMtuChangedListener: OnServerMtuChangedListener
    lateinit var mOnExecuteWriteListener: OnExecuteWriteListener
    lateinit var mOnPhyListener: OnPhyListener
    lateinit var mOnDescriptorListener: OnDescriptorListener

    @SuppressLint("MissingPermission")
    fun startPeripheral(
        context: Context,
        establishCustomeService: ((mBleGattServer: BluetoothGattServer) -> Unit)?,
        mAdsCallback: AdvertiseCallback,
        advertiseSetting: AdvertiseSettings = AdvertiseSettings.Builder()
            .setConnectable(true)
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setTimeout(0)
            .build(),
        advertiseData: AdvertiseData = AdvertiseData.Builder()
            .setIncludeTxPowerLevel(true)
            .setIncludeDeviceName(true)
            .build()
    ) {
        val bleAdertiser = SimonCore.mBluetoothAdapter.bluetoothLeAdvertiser
        initServer(context)
        if (establishCustomeService != null) {
            establishCustomeService(mBleGattServer)
        }
        bleAdertiser.startAdvertising(advertiseSetting, advertiseData, mAdsCallback)
    }


    @SuppressLint("MissingPermission")
    fun initServer(context: Context) {
        mBleGattServer =
            SimonCore.mBluetoothManager.openGattServer(context, this)

    }


    fun setServerConnectListener(onServerConnectListener: OnServerConnectListener) {
        this.mOnServerConnectListener = onServerConnectListener
    }

    fun setServerCharacteristicListener(onServerCharacteristicListener: OnServerCharacteristicListener) {
        this.mOnServerCharacteristicListener = onServerCharacteristicListener
    }

    fun setOnServerNotificationSentListener(onServerNotificationSentListener: OnServerNotificationSentListener) {
        this.mOnServerNotificationSentListener = onServerNotificationSentListener
    }

    fun setOnServerMtuChangeListener(onServerMtuChangedListener: OnServerMtuChangedListener) {
        this.mOnServerMtuChangedListener = onServerMtuChangedListener
    }

    fun setOnPhyListener(onPhyListener: OnPhyListener) {
        this.mOnPhyListener = onPhyListener
    }

    fun setOnDescriptorListener(onDescriptorListener: OnDescriptorListener) {
        this.mOnDescriptorListener = onDescriptorListener
    }

    fun setOnExecuteWriteListener(onExecuteWriteListener: OnExecuteWriteListener) {
        this.mOnExecuteWriteListener = onExecuteWriteListener
    }


    override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_DISCONNECTED -> {
                if (STeethPeri::mOnServerConnectListener.isInitialized) {
                    this.mOnServerConnectListener.onDisconnect(device, status, newState)

                }
            }
            BluetoothProfile.STATE_CONNECTED -> {
                if (STeethPeri::mOnServerConnectListener.isInitialized) {
                    this.mOnServerConnectListener.onConnectSuccess(device, status, newState)

                }
            }

        }
    }

    override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
        if (STeethPeri::mOnServerConnectListener.isInitialized) {
            this.mOnServerConnectListener.onServicesAdded(status, service)
        }
    }

    override fun onCharacteristicReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        characteristic: BluetoothGattCharacteristic?
    ) {
        if (STeethPeri::mOnServerCharacteristicListener.isInitialized) {
            this.mOnServerCharacteristicListener.onCharacteristicReadRequest(
                device,
                requestId,
                offset,
                characteristic
            )

        }
    }

    override fun onCharacteristicWriteRequest(
        device: BluetoothDevice?,
        requestId: Int,
        characteristic: BluetoothGattCharacteristic?,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray?
    ) {

        if (STeethPeri::mOnServerCharacteristicListener.isInitialized) {
            this.mOnServerCharacteristicListener.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )

        }

    }

    override fun onDescriptorReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        descriptor: BluetoothGattDescriptor?
    ) {
        if (STeethPeri::mOnDescriptorListener.isInitialized) {
            this.mOnDescriptorListener.onDescriptorReadRequest(
                device,
                requestId,
                offset,
                descriptor
            )
        }

    }

    override fun onDescriptorWriteRequest(
        device: BluetoothDevice?,
        requestId: Int,
        descriptor: BluetoothGattDescriptor?,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray?
    ) {

        if (STeethPeri::mOnDescriptorListener.isInitialized) {
            this.mOnDescriptorListener.onDescriptorWriteRequest(
                device,
                requestId,
                descriptor,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
        }
    }

    override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
        if (STeethPeri::mOnExecuteWriteListener.isInitialized) {
            this.mOnExecuteWriteListener.onExecuteWrite(device, requestId, execute)
        }
    }

    override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
        if (STeethPeri::mOnServerNotificationSentListener.isInitialized) {
            this.mOnServerNotificationSentListener.onNotificationSent(device, status)
        }
    }

    override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
        if (STeethPeri::mOnServerMtuChangedListener.isInitialized) {
            this.mOnServerMtuChangedListener.onMtuChanged(device, mtu)
        }
    }

    override fun onPhyUpdate(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
        if (STeethPeri::mOnPhyListener.isInitialized) {
            this.mOnPhyListener.onPhyUpdate(device, txPhy, rxPhy, status)
        }
    }

    override fun onPhyRead(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
        if (STeethPeri::mOnPhyListener.isInitialized) {
            this.mOnPhyListener.onPhyRead(device, txPhy, rxPhy, status)


        }

    }


}