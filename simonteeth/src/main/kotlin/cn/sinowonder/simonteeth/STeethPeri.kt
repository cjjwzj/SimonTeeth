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
object STeethPeri {

    lateinit var mBleClientDevice: BluetoothDevice
    lateinit var mBleClientGatt: BluetoothGatt
    lateinit var mBleGattServer: BluetoothGattServer
    lateinit var mServerConnectListener: ServerConnectListener
    lateinit var mServerCharacteristicListener: ServerCharacteristicListener
    lateinit var mOnServerNotificationSentListener: OnServerNotificationSentListener
    lateinit var mOnServerMtuChangedListener: OnServerMtuChangedListener
    lateinit var mOnExecuteWriteListener: OnExecuteWriteListener

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
        val bleAdertiser = STeethCen.mBluetoothAdapter.bluetoothLeAdvertiser
        initServer(context)
        if (establishCustomeService != null) {
            establishCustomeService(mBleGattServer)
        }
        bleAdertiser.startAdvertising(advertiseSetting, advertiseData, mAdsCallback)
    }


    @SuppressLint("MissingPermission")
    fun initServer(context: Context) {

        mBleGattServer =
            STeethCen.mBluetoothManager.openGattServer(context, mBluetoothGattServerCallback)

    }



    fun setServerConnectListener(serverConnectListener: ServerConnectListener) {
        this.mServerConnectListener = serverConnectListener
    }

    fun setServerCharacteristicListener(serverCharacteristicListener: ServerCharacteristicListener) {
        this.mServerCharacteristicListener = serverCharacteristicListener
    }

    fun setOnServerNotificationSentListener(onServerNotificationSentListener: OnServerNotificationSentListener) {
        this.mOnServerNotificationSentListener = onServerNotificationSentListener
    }

    fun setOnServerMtuChangeListener(onServerMtuChangedListener: OnServerMtuChangedListener) {
        this.mOnServerMtuChangedListener = onServerMtuChangedListener
    }

    fun setOnExecuteWriteListener(onExecuteWriteListener: OnExecuteWriteListener) {
        this.mOnExecuteWriteListener = onExecuteWriteListener
    }


    val mBluetoothGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            when (newState) {
                BluetoothProfile.STATE_DISCONNECTED -> {
                    if (STeethPeri::mServerConnectListener.isInitialized) {
                        mServerConnectListener.onDisconnect(device, status, newState)

                    }
                }
                BluetoothProfile.STATE_CONNECTED -> {
                    if (STeethPeri::mServerConnectListener.isInitialized) {
                        mServerConnectListener.onConnectSuccess(device, status, newState)

                    }
                }

            }
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
            if (STeethPeri::mServerConnectListener.isInitialized) {

                mServerConnectListener.onServicesAdded(status, service)
            }
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            if (STeethPeri::mServerCharacteristicListener.isInitialized) {
                mServerCharacteristicListener.onCharacteristicReadRequest(
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
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )

            if (STeethPeri::mServerCharacteristicListener.isInitialized) {
                mServerCharacteristicListener.onCharacteristicWriteRequest(
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
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
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
            super.onDescriptorWriteRequest(
                device,
                requestId,
                descriptor,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            if (STeethPeri::mOnExecuteWriteListener.isInitialized) {
                mOnExecuteWriteListener.onExecuteWrite(device, requestId, execute)
            }
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            if (STeethPeri::mOnServerNotificationSentListener.isInitialized) {
                mOnServerNotificationSentListener.onNotificationSent(device, status)
            }
        }

        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            super.onMtuChanged(device, mtu)

            if (STeethPeri::mOnServerMtuChangedListener.isInitialized) {
                mOnServerMtuChangedListener.onMtuChanged(device, mtu)
            }
        }

        override fun onPhyUpdate(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(device, txPhy, rxPhy, status)
        }

        override fun onPhyRead(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(device, txPhy, rxPhy, status)
        }
    }


}