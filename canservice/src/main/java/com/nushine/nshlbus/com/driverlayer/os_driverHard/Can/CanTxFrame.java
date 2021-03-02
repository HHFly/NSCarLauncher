/*ver:0.0.1
 *bref:CAN发送消息帧的数据格式定义
 */
package com.nushine.nshlbus.com.driverlayer.os_driverHard.Can;

import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_DataType;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_FrameType;

public class CanTxFrame {//can收发数据格式
	public int StdId;	//标准帧
	public int ExtId;	//扩展帧
	public J1939_FrameType IDE;		//表示帧类别
	public J1939_DataType  RTR;	//数据帧和远程帧(保留)
	public int Can_ID;
	public byte Can_DLC;
	public short[] mData = new short[8];
}
