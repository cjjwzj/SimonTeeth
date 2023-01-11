package cn.sinowonder.bluetoothtest

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import cn.sinowonder.simonteeth.SimonCore
import com.kongzue.dialogx.DialogX

/**
 * <br>
 * function:
 * <p>
 * @author:Richard_Chamberlain
 * @date:2022/11/10 16:19
 * @since:V1
 * @desc:cn.sinowonder.bluetoothtest
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化
        DialogX.init(this)
        DialogX.globalHoverWindow = true
        DialogX.implIMPLMode = DialogX.IMPL_MODE.WINDOW
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        SimonCore.init(bluetoothManager)


    }
}