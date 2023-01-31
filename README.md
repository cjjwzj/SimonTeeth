# SimonTeeth

[![](https://www.jitpack.io/v/cjjwzj/SimonTeeth.svg)](https://www.jitpack.io/#cjjwzj/SimonTeeth)
![](https://img.shields.io/badge/API-18%2B-brightgreen)
![](https://img.shields.io/badge/License-Apache--2.0-brightgreen)

## Android BLE蓝牙连接库(双端)

### 用法:
- 依赖:
  ```gradle
  allprojects {
		repositories {
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

### 权限要求
  ```xml
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
  ```
  
### 额外信息可参考WIKI


