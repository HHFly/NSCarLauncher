/*ver:0.0.1
 *bref:定义基本的protocol层各种设备的一些公共参数。
 */
package com.nushine.nshlbus.com.driverlayer.os_driverProtocol;

import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverCallPro;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.DriverHard.DriverHardType;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanDeviceIndex;
import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverConstValue.DriverPgStatus;
import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverConstValue.DriverTransType;

public class DriverProtocolStack{
	public ProtocolAppData m_appdata;
	public DriverCallPro   m_callpro;
	public DriverProtocolStack(short totaladdr,short totalsendpng,short totalrecvPng){
		m_appdata = new ProtocolAppData(totaladdr,totalsendpng,totalrecvPng);
		m_callpro = null;
	}
	public class ProtocolAppData{
		public short	TotalAddr; 		//地址数量
		public short	TotalSendPara; 	//发送的参数数量
		public short	TotalRecvPara; 	//接收的参数数量
		public DriverHardType 	mHardType;		//设备类型
		public ProtocolPara[][] Send;	//发送的参数组(支持多个地址)
		public ProtocolPara[][] Recv;	//接受的参数组
		ProtocolAppData(short totaladdr,short totalsendpng,short totalrecvPng){
			TotalAddr = totaladdr;
			TotalSendPara = totalsendpng;
			TotalRecvPara = totalrecvPng;
			Send = new ProtocolPara[TotalAddr][TotalSendPara];
			Recv = new ProtocolPara[TotalAddr][TotalRecvPara];
			for(int i=0;i<Send.length;i++)
				for(int j=0;j<Send[i].length;j++){
					Send[i][j] = new ProtocolPara();
				}
			for(int i=0;i<Recv.length;i++)
				for(int j=0;j<Recv[i].length;j++){
					Recv[i][j] = new ProtocolPara();
				}
		}
		public class ProtocolPara{
			public byte	mDataMaxLen=0;	//报文最大长度
			public byte	mDataLen=0;		//报文实际长度
			public short mSource=0;		//设备起始地址
			public int		mTransRate=0; 	//发送或者接收周期
			public long		mTimer=0;			//上个报文(发送或者接收)时间戳
			public boolean	mEnable=false;		//报文使能
			public DriverPgStatus	mPGState;		//报文状态
			public DriverTransType	mTransType;		//发送方式
			public CanDeviceIndex	mHardNum;		//硬件编号(对于单个的设备该值为0，多个设备的话累加如CAN可能存在多个)
		}
	}
}
