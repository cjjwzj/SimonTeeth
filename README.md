# SimonTeeth
Android BLE蓝牙连接库(双端)
用法:
      初始化:  val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
              SimonCore.init(bluetoothManager)
      主机端:  开启扫描  STeethCen.startLeScan()
      从机端:  开启广播  STeethPeri.startPeripheral()
