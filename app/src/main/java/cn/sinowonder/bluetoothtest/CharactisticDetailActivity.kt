package cn.sinowonder.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.sinowonder.simonteeth.STeethCen
import cn.sinowonder.simonteeth.interfaces.central.CharacteristicListener

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/18 17:10
 * @since:V
 * @desc:cn.sinowonder.bluetoothtest
 */
class CharactisticDetailActivity : AppCompatActivity(), View.OnClickListener,
    CharacteristicListener {

    val tvUUID: TextView by lazy { findViewById(R.id.tv_charactistic_uuid) }
    val edtSend: EditText by lazy { findViewById(R.id.edt_send) }
    val btnSend: TextView by lazy { findViewById(R.id.btn_send) }
    val tvReceive: TextView by lazy { findViewById(R.id.tv_receive) }

    lateinit var bleChar: BluetoothGattCharacteristic


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characteristic_detail)
        title = "特征详情"
        tvUUID.text = selectedCharacteristicUUID?.toString()
        btnSend.setOnClickListener(this)
        tvReceive.movementMethod = ScrollingMovementMethod()
        bleChar = STeethCen.getLastConnectedGatt().getService(selectedServiceUUID)
            .getCharacteristic(selectedCharacteristicUUID)
        STeethCen.getLastConnectedGatt().setCharacteristicNotification(bleChar, true)
        STeethCen.addCharacteristicListener(99, this)

    }


    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_send -> {
                bleChar.value = edtSend.text.toString().toByteArray()
                STeethCen.getLastConnectedGatt().writeCharacteristic(bleChar)
            }


        }

    }

    override fun onCharacteristicRead(
        listenerTag: Int,
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {

    }

    override fun onCharacteristicWrite(
        listenerTag: Int,
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {


    }


    override fun onCharacteristicChange(
        listenerTag: Int,
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        tvReceive.text = "${characteristic?.getStringValue(0)}\n${tvReceive.text}"
    }
}