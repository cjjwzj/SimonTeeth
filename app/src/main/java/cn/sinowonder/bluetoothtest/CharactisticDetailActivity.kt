package cn.sinowonder.bluetoothtest

import ClientCharacteristicConfiguration
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.sinowonder.simonteeth.STeethCen
import cn.sinowonder.simonteeth.interfaces.central.CharacteristicListener
import cn.sinowonder.simonteeth.interfaces.central.NotifyListener
import java.util.*

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
    CharacteristicListener, NotifyListener {

    val tvUUID: TextView by lazy { findViewById(R.id.tv_charactistic_uuid) }
    val edtSend: EditText by lazy { findViewById(R.id.edt_send) }
    val btnSend: Button by lazy { findViewById(R.id.btn_send) }
    val btnRead: Button by lazy { findViewById(R.id.btn_read) }
    val btnSubscribe: Button by lazy { findViewById(R.id.btn_subscribe) }
    val btnUnSubscribe: Button by lazy { findViewById(R.id.btn_unsubscribe) }
    val tvNotifyReceive: TextView by lazy { findViewById(R.id.tv_notify_receive) }
    val tvWriteReply: TextView by lazy { findViewById(R.id.tv_write_reply) }
    val tvReadReceive: TextView by lazy { findViewById(R.id.tv_read_receive) }

    lateinit var bleChar: BluetoothGattCharacteristic


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characteristic_detail)
        title = "特征详情"
        tvUUID.text = selectedCharacteristicUUID?.toString()
        btnSend.setOnClickListener(this)
        btnRead.setOnClickListener(this)
        btnSubscribe.setOnClickListener(this)
        btnUnSubscribe.setOnClickListener(this)
        tvNotifyReceive.movementMethod = ScrollingMovementMethod()
        tvReadReceive.movementMethod = ScrollingMovementMethod()
        tvWriteReply.movementMethod = ScrollingMovementMethod()
        bleChar = STeethCen.getLastConnectedGatt().getService(selectedServiceUUID)
            .getCharacteristic(selectedCharacteristicUUID)
        STeethCen.getLastConnectedGatt().setCharacteristicNotification(bleChar, true)
        STeethCen.setCharacteristicListener(this)
        STeethCen.setNotifyListener(this)
    }


    @SuppressLint("MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_send -> {
                bleChar.value = edtSend.text.toString().toByteArray()
                STeethCen.getLastConnectedGatt().writeCharacteristic(bleChar)
            }
            R.id.btn_read -> {
                STeethCen.getLastConnectedGatt().readCharacteristic(bleChar)
            }

            R.id.btn_subscribe -> {
                STeethCen.subscribeNotify(
                    bleChar,
                    UUID.fromString(ClientCharacteristicConfiguration)
                )
            }
            R.id.btn_unsubscribe -> {
                STeethCen.unsubscribeNotify(
                    bleChar,
                    UUID.fromString(ClientCharacteristicConfiguration)
                )
            }


        }

    }


    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {

        val a = characteristic?.value

//        LogUtils.a(ArrayUtils.toString(a))
        tvReadReceive.text = "${characteristic?.getStringValue(0)}\n${tvReadReceive.text}"
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {

        tvWriteReply.text = "${characteristic?.getStringValue(0)}\n${tvWriteReply.text}"
    }


    override fun onCharacteristicChange(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        tvNotifyReceive.text = "${characteristic?.getStringValue(0)}\n${tvNotifyReceive.text}"
    }


}