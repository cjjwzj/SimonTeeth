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

  

### 可靠写入

可靠的写入允许检查传输的值并原子执行一个或多个传输的消息.在[BLE 部分可以找到对可靠写入程序的很好的解释Mozillas Boot 2 Gecko 项目文档](https://wiki.mozilla.org/B2G/Bluetooth/WebBluetooth-v2/BluetoothGatt).尽管它是针对 JavaScript 的，但对 `beginReliableWrite()` 的描述对于理解过程非常有帮助:一旦启动了可靠的写事务，所有调用feature.writeValue() 被发送到远程设备验证并排队等待原子执行.一个承诺携带写入的值被返回以响应每个feature.writeValue() 调用和应用程序负责用于验证该值是否已准确传输.后所有特征都已排队验证，executeReliableWrite() 将执行所有写入.如果一个特征未正确写入，调用 abortReliableWrite() 将取消当前事务而不在远程 LE 上提交任何值设备.你开始可靠的写作，

```kotlin
gatt.beginReliableWrite();
```

设置特性的值并写入.

```kotlin
characteristic.setValue(value);
gatt.writeCharacteristic(characteristic);
```

`writeCharacteristic()` 调用将触发其正常"回调.参数`characteristic` 包含实际的、可验证的写入值:

```kotlin
@Override
public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, 
                int status) {
    ...

    if(characteristic.getValue() != value) { 
        gatt.abortReliableWrite();
    } else {
        gatt.executeReliableWrite();
    }

    ...
}
```

执行可靠写入将触发`onReliableWriteCompleted(BluetoothGatt gatt, int status)`回调.
