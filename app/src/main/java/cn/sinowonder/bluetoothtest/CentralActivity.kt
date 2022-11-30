package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import cn.sinowonder.bluetoothtest.BleDeviceDetailActivity.Companion.BLE_RESULT
import cn.sinowonder.simonteeth.STeeth
import com.blankj.utilcode.util.ActivityUtils


class CentralActivity : AppCompatActivity(), BleDeviceAdapter.OnItemClickListener {


    val rv: RecyclerView by lazy { findViewById(R.id.rv_device) }
    val btnStartScan: Button by lazy { findViewById(R.id.btn_start_scan) }

    val scanBleResults = arrayListOf<ScanResult>()
    val realAdapter = BleDeviceAdapter(scanBleResults, this)



    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_central)
        rv.adapter = realAdapter
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        btnStartScan.setOnClickListener {
            scanBleResults.clear()
            realAdapter.notifyDataSetChanged()
            STeeth.startLeScan(object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    super.onScanResult(callbackType, result)
                    if (scanBleResults.none { it.device.address == result.device.address }) {
                        scanBleResults.add(result)
                        realAdapter.notifyItemChanged(scanBleResults.size - 1)
                    }


                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    super.onBatchScanResults(results)


                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)


                }
            })
        }

    }

    override fun onItemClicked(position: Int, holder: BleDeviceAdapter.BleViewHolder) {

    }

    @SuppressLint("MissingPermission")
    override fun onContentClicked(position: Int, content: View, holder: BleDeviceAdapter.BleViewHolder) {

        val bleIntent = Intent(this, BleDeviceDetailActivity::class.java).putExtra(
            BLE_RESULT,
            scanBleResults[position]
        )
        ActivityUtils.startActivity(bleIntent)



    }


}