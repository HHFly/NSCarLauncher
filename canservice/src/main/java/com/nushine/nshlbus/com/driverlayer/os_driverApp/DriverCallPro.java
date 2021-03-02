/*ver:0.0.1
 *bref:定义一个抽象类，用于动态管理所有的驱动设备
 */

package com.nushine.nshlbus.com.driverlayer.os_driverApp;

import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939FrameFormat;


public abstract class DriverCallPro{
	public abstract void txFrame(int lpara,int wpara); //发送报文回调接口
	public abstract boolean rxFrame(int lpara,int wpara); //接收的报文解析回调接口
	public abstract void rxTimeout(int lpara,int wpara);//数据接收超时回调接口
	public abstract void txTimeout(int lpara,int wpara);
	//利用覆盖的方法去实现各个硬件的数据发送和接受接口
	public J1939FrameFormat SendFrame(int lpara,int wpara){
		J1939FrameFormat m_send = null;
		return m_send;
	}

	public boolean RxSourceData(J1939FrameFormat j1939){
		return false;
	}
	/*
	public boolean RxSourceData(GPS pdta){ 
		return false;
	}
	public boolean RxSourceData(WIFI pdta){ 
		return false;
	}*/
}
