package cn.sinowonder.bluetoothtest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.sinowonder.simonteeth.utils.STUtils
import com.blankj.utilcode.util.ActivityUtils
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.PopNotification
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener


/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/17 14:54
 * @since:V
 * @desc:cn.sinowonder.bluetoothtest
 */
class MainActivity : AppCompatActivity() {

    val btnOpenBleS by lazy { findViewById<Button>(R.id.btn_open_ble_s) }
    val btnOpenBleT by lazy { findViewById<Button>(R.id.btn_open_ble_t) }
    val btnOpenNotify by lazy { findViewById<Button>(R.id.btn_open_notify_dialog) }
    val btnPerfPeri by lazy { findViewById<Button>(R.id.btn_perf_peri) }
    val btnPerfCent by lazy { findViewById<Button>(R.id.btn_perf_central) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenBleS.setOnClickListener {
            STUtils.enableBlueToothSilently()

        }
        btnOpenNotify.setOnClickListener {
            PopNotification.build()
                .setDialogImplMode(DialogX.IMPL_MODE.WINDOW)
                .setTitle("这是一条消息 ")
                .setMessage("ssss")
                .show().noAutoDismiss()

        }
        btnOpenBleT.setOnClickListener {
            STUtils.enableBlueToothWithDialog(this)

        }
        btnPerfPeri.setOnClickListener {
            ActivityUtils.startActivity(PeripheralActivity::class.java)
        }
        btnPerfCent.setOnClickListener {
            ActivityUtils.startActivity(CentralActivity::class.java)

        }

    }
}