# SimonTeeth

[![](https://www.jitpack.io/v/cjjwzj/SimonTeeth.svg)](https://www.jitpack.io/#cjjwzj/SimonTeeth)

## Android BLE蓝牙连接库(双端)

### 用法:
- 依赖:
  ```gradle
  allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
  ```
  ```gradle
	dependencies {
	        implementation 'com.github.cjjwzj:SimonTeeth:v1.0.7'
	}
  ```

- 初始化:  

  ```kotlin
  val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
  SimonCore.init(bluetoothManager)
  ```

- 主机端:  开启扫描  

  ```kotlin
  STeethCen.startLeScan()
  ```

- 从机端:  开启广播  

  ```kotlin
  STeethPeri.startPeripheral()
  ```

  
  
### 额外信息可参考WIKI


