package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.sinowonder.simonteeth.STeethPeri
import cn.sinowonder.simonteeth.interfaces.peripheral.OnServerCharacteristicListener
import cn.sinowonder.simonteeth.interfaces.peripheral.OnServerConnectListener
import cn.sinowonder.simonteeth.readUUID
import cn.sinowonder.simonteeth.serviceUUID
import cn.sinowonder.simonteeth.writeUUID

class PeripheralActivity : AppCompatActivity(), OnServerCharacteristicListener,
    OnServerConnectListener {

    val btnOpenDetection: Button by lazy { findViewById(R.id.btn_open_detection) }
    val tvDetectState: TextView by lazy { findViewById(R.id.tv_detect_state) }
    val tvConState: TextView by lazy { findViewById(R.id.tv_con_state) }
    val edtSendContent: TextView by lazy { findViewById(R.id.edt_send_content) }
    val btnSend: TextView by lazy { findViewById(R.id.btn_send) }
    val tvReceive: TextView by lazy { findViewById(R.id.tv_notify_receive) }

    lateinit var mWriteCharacteristic: BluetoothGattCharacteristic
    lateinit var mBleClientDevice: BluetoothDevice

    val mAdsCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            tvDetectState.text = "开启成功"
            tvDetectState.setTextColor(Color.GREEN)
        }

        override fun onStartFailure(errorCode: Int) {
            tvDetectState.text = "开启失败,错误码:${errorCode}"
            tvDetectState.setTextColor(Color.RED)

        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peripheral)
        STeethPeri.setServerConnectListener(this)
        STeethPeri.setServerCharacteristicListener(this)
        tvReceive.movementMethod = ScrollingMovementMethod()
        btnOpenDetection.setOnClickListener {
            STeethPeri.startPeripheral(this, {
                val gattService = BluetoothGattService(
                    serviceUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY
                )
                mWriteCharacteristic = BluetoothGattCharacteristic(
                    writeUUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                    BluetoothGattCharacteristic.PERMISSION_WRITE
                )
                val mNotifyCharacteristic = BluetoothGattCharacteristic(
                    readUUID,
                    BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                    BluetoothGattCharacteristic.PERMISSION_READ
                )
                gattService.addCharacteristic(mWriteCharacteristic)
                gattService.addCharacteristic(mNotifyCharacteristic)
                it.clearServices()
                it.addService(gattService)
            }, mAdsCallback)

        }

        btnSend.setOnClickListener {
            val content = edtSendContent.text.toString().toByteArray()
            mWriteCharacteristic.value = content
            STeethPeri.mBleGattServer.notifyCharacteristicChanged(
                mBleClientDevice,
                mWriteCharacteristic,
                false
            )

        }

    }

    @SuppressLint("MissingPermission")
    override fun onCharacteristicReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        characteristic: BluetoothGattCharacteristic?
    ) {
        STeethPeri.mBleGattServer.sendResponse(
            device,
            requestId,
            BluetoothGatt.GATT_SUCCESS,
            offset,
            characteristic?.value
        )
    }

    @SuppressLint("MissingPermission")
    override fun onCharacteristicWriteRequest(
        device: BluetoothDevice?,
        requestId: Int,
        characteristic: BluetoothGattCharacteristic?,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray?
    ) {
        tvReceive.text = "${String(value ?: "null".toByteArray())} \n ${tvReceive.text}"

        STeethPeri.mBleGattServer.sendResponse(
            device,
            requestId,
            BluetoothGatt.GATT_SUCCESS,
            offset,
            value
        )
    }

    override fun onConnectSuccess(device: BluetoothDevice?, status: Int, newState: Int) {
        runOnUiThread {

            tvConState.text = "已连接"
            tvConState.setTextColor(Color.GREEN)
            mBleClientDevice = device!!
        }

    }

    override fun onDisconnect(device: BluetoothDevice?, status: Int, newState: Int) {
        runOnUiThread {


            tvConState.text = "已断开"
            tvConState.setTextColor(Color.RED)
        }

    }


    override fun onServicesAdded(status: Int, service: BluetoothGattService?) {

    }

}