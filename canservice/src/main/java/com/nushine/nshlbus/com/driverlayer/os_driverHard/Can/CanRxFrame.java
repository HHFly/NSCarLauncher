/*ver:0.0.1
 *bref:CAN接收消息帧的数据格式定义
 */
package com.nushine.nshlbus.com.driverlayer.os_driverHard.Can;

import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_DataType;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_FrameType;

public class CanRxFrame implements Cloneable{//can收发数据格式
	public int StdId;	//标准帧
	public int ExtId;	//扩展帧
	public J1939_FrameType IDE;		//表示帧类别
	public J1939_DataType  RTR;	//数据帧和远程帧(保留)
	public byte Can_DLC;
	public short[] mData = new short[8];
	public short FMI;
	public CanDeviceIndex mHardNum;
	public Object clone(){
		CanRxFrame obj=null;
		try{
			obj = (CanRxFrame)super.clone();
			obj.mData = (short[])obj.mData.clone();//深克隆
		}catch(CloneNotSupportedException e){
			e.getStackTrace();
		}
		return obj;

	}
	public String PrintCanRxFrame(String message)
	{
		message = "StdId:"+Integer.toHexString(this.StdId)+","+
				"ExtId:"+Integer.toHexString(this.ExtId)+"\n"+"IDE:"+this.IDE+","+
				"RTR:"+this.RTR+","+"Can_DLC:"+this.Can_DLC+"\n";
		String str="";
		for(int i=0;i<this.mData.length;i++){
			str+=Integer.toHexString(this.mData[i]);
			str+=",";
		}
		message+=str+"\n";
		return message;
	}
}
