package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.ScanResult
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import cn.sinowonder.simonteeth.STeethCen
import cn.sinowonder.simonteeth.interfaces.central.ConnectListener
import cn.sinowonder.simonteeth.interfaces.central.RssiListener
import cn.sinowonder.simonteeth.utils.STUtils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/18 17:10
 * @since:V
 * @desc:cn.sinowonder.bluetoothtest
 */
class BleDeviceDetailActivity : AppCompatActivity(), View.OnClickListener,
    BleServiceAdapter.OnItemClickListener, ConnectListener, RssiListener {

    companion object {
        const val BLE_RESULT = "BLE_RESULT"
        lateinit var bleGatt: BluetoothGatt

    }

    val btnConnect by lazy { findViewById<Button>(R.id.btn_connect) }
    val btnReadRssi by lazy { findViewById<Button>(R.id.btn_read_rssi) }
    val btnDisconnect by lazy { findViewById<Button>(R.id.btn_disconnect) }
    val tvDeviceName by lazy { findViewById<TextView>(R.id.tv_device_name) }
    val tvMac by lazy { findViewById<TextView>(R.id.tv_mac) }
    val tvRssi by lazy { findViewById<TextView>(R.id.tv_rssi) }
    val tvState by lazy { findViewById<TextView>(R.id.tv_state) }
    val rvService by lazy { findViewById<RecyclerView>(R.id.rv_services) }
    val btnChangeMtu by lazy { findViewById<Button>(R.id.btn_change_mtu) }
    var bleResult: ScanResult? = null
    val datas = arrayListOf<BluetoothGattService>()
    val serviceAdapter = BleServiceAdapter(datas, this)
    lateinit var bleDevice: BluetoothDevice


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble_device_detail)
        title = "设备详情"

        bleResult = intent.getParcelableExtra<ScanResult>(BLE_RESULT)
        rvService.adapter = serviceAdapter
        rvService.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        btnConnect.setOnClickListener(this)
        btnDisconnect.setOnClickListener(this)
        btnReadRssi.setOnClickListener(this)
        btnChangeMtu.setOnClickListener(this)

        bleDevice = bleResult?.device?.address?.let { STeethCen.getRemoteDevice(it) }!!
        tvDeviceName.text = bleDevice.name
        tvMac.text = bleDevice.address
        tvRssi.text = bleResult?.rssi.toString()
        tvState.text = "未连接"
        tvState.setTextColor(Color.RED)
        STeethCen.setConnectListener(this)
        STeethCen.setRssiListener(this)

    }

    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_connect -> {
                STeethCen.connect(this, false, bleDevice)
            }
            R.id.btn_disconnect -> {
                bleGatt.disconnect()
                datas.clear()
                serviceAdapter.notifyDataSetChanged()
            }
            R.id.btn_read_rssi -> {
                try {
                    STUtils.readReadRssi(bleGatt)
                } catch (e: UninitializedPropertyAccessException) {
                    ToastUtils.showShort(e.message)

                }
            }
            R.id.btn_change_mtu -> {

                bleGatt.requestMtu(512)

            }
        }

    }

    override fun onItemClicked(position: Int, holder: BleServiceAdapter.BleViewHolder) {


    }

    override fun onContentClicked(
        position: Int,
        content: View,
        holder: BleServiceAdapter.BleViewHolder
    ) {
        selectedServiceUUID = datas[position].uuid
        ActivityUtils.startActivity(ServicesDetailActivity::class.java)
    }


    override fun onConnectSuccess(gatt: BluetoothGatt) {
        bleGatt = gatt
        runOnUiThread {
            tvState.text = "已连接"
            tvState.setTextColor(Color.GREEN)
        }
    }

    override fun onDisconnect() {
        runOnUiThread {
            tvState.text = "未连接"
            tvState.setTextColor(Color.RED)
        }
    }

    override fun onServicesDiscovered(services: MutableList<BluetoothGattService>) {
        datas.clear()
        services.let { datas.addAll(it) }
        runOnUiThread {
            serviceAdapter.notifyDataSetChanged()
        }
    }

    override fun onServicesDiscoverFailed(status: Int) {
        TODO("Not yet implemented")
    }

    override fun onServicesChange(gatt: BluetoothGatt) {
        TODO("Not yet implemented")
    }

    override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
        tvRssi.text = rssi.toString()

    }
}