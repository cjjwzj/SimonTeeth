package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
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
class BleDeviceAdapter(
    val datas: List<ScanResult>,
    val onItemClickListener: BleDeviceAdapter.OnItemClickListener
) :
    RecyclerView.Adapter<BleDeviceAdapter.BleViewHolder>() {

    val mOnItemClickListener = onItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(position: Int, holder: BleViewHolder)
        fun onContentClicked(position: Int, content: View, holder: BleViewHolder)

    }

    class BleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bleName = itemView.findViewById<TextView>(R.id.ble_name)
        val bleMac = itemView.findViewById<TextView>(R.id.ble_mac)
        val btnDetail = itemView.findViewById<Button>(R.id.btn_detail)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_ble_device, parent, false)


        return BleViewHolder(itemView)

    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {

        holder.bleName.text = datas[position].device.name
        holder.bleMac.text = datas[position].device.address
        holder.btnDetail.setOnClickListener {
            mOnItemClickListener.onContentClicked(position,holder.btnDetail,holder)

        }

    }

    override fun getItemCount(): Int = datas.size
}