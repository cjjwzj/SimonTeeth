package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.sinowonder.simonteeth.utils.STUtils

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/11 11:02
 * @since:V1
 * @desc:cn.sinowonder.bluetoothtest
 */
class BleCharacteristicAdapter(
    val datas: List<BluetoothGattCharacteristic>,
    val onItemClickListener: BleCharacteristicAdapter.OnItemClickListener
) :
    RecyclerView.Adapter<BleCharacteristicAdapter.BleViewHolder>() {

    val mOnItemClickListener = onItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(position: Int, holder: BleViewHolder)
        fun onContentClicked(position: Int, content: View, holder: BleViewHolder)

    }

    class BleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val characteristicUuid = itemView.findViewById<TextView>(R.id.tv_characteristic_uuid)
        val characteristicProp = itemView.findViewById<TextView>(R.id.tv_characteristic_prop)
        val characteristicDetail = itemView.findViewById<Button>(R.id.characteristic_detail)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ble_characteristic, parent, false)


        return BleViewHolder(itemView)

    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {


        val propSb = STUtils.detectCharacteristic(datas[position])


        holder.characteristicUuid.text = datas[position].uuid.toString()
        holder.characteristicProp.text = propSb
        holder.characteristicDetail.setOnClickListener {
            mOnItemClickListener.onContentClicked(position, holder.characteristicDetail, holder)
        }

    }

    override fun getItemCount(): Int = datas.size
}