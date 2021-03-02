/*ver:0.0.1
 *bref:该文件中定义了一些J1939链路控制的常量
 */
package com.nushine.nshlbus.com.driverlayer.os_driverJ1939;

import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanDeviceIndex;

public class J1939ValueDefine {
	final short J1939_ADDR = 0xf4;
	final byte J1939_TRANS_MAX=1;	//最大并发传输数量
	final byte J1939_TRANS_LEN_MAX=60;//一次传输最大字节数
	final byte J1939_TRANS_PKG_LEN=8;//单包J1939数据的最大长度
	final byte J1939_TRANS_DTDATA_LEN=J1939_TRANS_PKG_LEN-1;

	final boolean J1939_Little_Endium = true;//使能小端数据发送
	short j1939_GetLSB(short var){
		return var;
	}
	short j1939_GetMLSB(short var){
		return (short) (var>>8);
	}

	//传输节点状态定义
	enum J1939_Pg_Status{
		J1939_PG_RECV,
		J1939_PG_SEND,
		J1939_PG_IDLE
	}
	//传输连接状态
	enum J1939_Connect_Status{
		J1939_CONNECT_IDLE,
		//发送
		J1939_CONNECT_SENDRTS,
		J1939_CONNECT_WAITCTS,
		J1939_CONNECT_SENDDT,
		J1939_CONNECT_WAITENDACK,
		//接收
		J1939_CONNECT_WAITRTS
	}
	//传输节点数据结构定义
	class  j1939TransPg{
		J1939_Pg_Status PgStatus;
		J1939_Connect_Status ConnectStatus;
		short  NextFramNum;//下一个要接收或者发送的帧号
		short  FramStart_CTS;	//本次CTS请求发送起始帧号
		short  FramTotal_CTS;	//本次CTS请求发送帧数
		short  TotalFramNum;
		short 	TotalLen;
		int 	LastTime;	//最后操作时间
		short[] Data = new short[J1939ValueDefine.this.J1939_TRANS_LEN_MAX];
		short[] PGN=new short[3];//发送参数群编号，ps,pf
		CanDeviceIndex CanNum;
	}
	//传输命令定义
	final static short J1939_PF_PDU2=0xf0;
	final static short J1939_TP_PF=0xec;
	final static short J1939_TPCMD_RTS=16;
	final static short J1939_TPCMD_CTS=17;
	final static short J1939_TPCMD_ENDACK=19;
	final static short J1939_TPCMD_ABORT=255;
	final static short J1939_TPCMD_BAM=32;
	//传输超时定义
	final static short J1939_OVERTIME_CTS=1500;
	final static short J1939_OVERTIME_ENDACK=1500;
	final static short J1939_OVERTIME_DT=30;
	//传输缓存定义
	j1939TransPg[] J1939_TransPG;
}
