package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import cn.sinowonder.simonteeth.STeeth
import com.blankj.utilcode.util.ActivityUtils

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/18 17:10
 * @since:V
 * @desc:cn.sinowonder.bluetoothtest
 */
class ServicesDetailActivity : AppCompatActivity(),
    BleCharacteristicAdapter.OnItemClickListener {


    val rvService by lazy { findViewById<RecyclerView>(R.id.rv_characteristic) }
    val datas = arrayListOf<BluetoothGattCharacteristic>()
    val bleCharacteristicAdapter = BleCharacteristicAdapter(datas, this)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)
        title = "服务详情"
        rvService.adapter = bleCharacteristicAdapter
        rvService.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        val currentService = STeeth.getLastConnectedGatt().getService(selectedServiceUUID)
        currentService?.characteristics?.let { datas.addAll(it) }
        bleCharacteristicAdapter.notifyDataSetChanged()

    }


    override fun onItemClicked(position: Int, holder: BleCharacteristicAdapter.BleViewHolder) {


    }

    override fun onContentClicked(
        position: Int,
        content: View,
        holder: BleCharacteristicAdapter.BleViewHolder
    ) {
        selectedCharacteristicUUID = datas[position].uuid
        ActivityUtils.startActivity(CharactisticDetailActivity::class.java)
    }
}