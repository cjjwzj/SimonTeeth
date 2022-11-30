package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/11 11:02
 * @since:V1
 * @desc:cn.sinowonder.bluetoothtest
 */
class BleServiceAdapter(
    val datas: List<BluetoothGattService>,
    val onItemClickListener: BleServiceAdapter.OnItemClickListener
) :
    RecyclerView.Adapter<BleServiceAdapter.BleViewHolder>() {

    val mOnItemClickListener = onItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(position: Int, holder: BleViewHolder)
        fun onContentClicked(position: Int, content: View, holder: BleViewHolder)

    }

    class BleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceUuid = itemView.findViewById<TextView>(R.id.tv_service_uuid)
        val serviceProp = itemView.findViewById<TextView>(R.id.tv_service_prop)
        val serviceDetail = itemView.findViewById<Button>(R.id.service_detail)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ble_services, parent, false)


        return BleViewHolder(itemView)

    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {

        holder.serviceUuid.text = datas[position].uuid.toString()
        holder.serviceProp.text = "特征数量:" + datas[position].characteristics.size.toString()
        holder.serviceDetail.setOnClickListener {
            mOnItemClickListener.onContentClicked(position, holder.serviceDetail, holder)

        }

    }

    override fun getItemCount(): Int = datas.size
}