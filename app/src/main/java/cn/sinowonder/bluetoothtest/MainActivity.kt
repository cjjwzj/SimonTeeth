package cn.sinowonder.bluetoothtest

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.sinowonder.simonteeth.STeeth
import cn.sinowonder.simonteeth.utils.STUtils
import com.blankj.utilcode.util.ActivityUtils

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
    val btnPerfPeri by lazy { findViewById<Button>(R.id.btn_perf_peri) }
    val btnPerfCent by lazy { findViewById<Button>(R.id.btn_perf_central) }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenBleS.setOnClickListener {
            STUtils.enableBlueToothSilently()

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