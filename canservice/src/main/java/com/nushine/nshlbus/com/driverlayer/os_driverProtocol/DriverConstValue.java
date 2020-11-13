/*ver:0.0.1
 *bref:BCU应用，通过一系列接口，将上报数据解析为对应的信息存放到缓存中
 */
package com.nushine.nshlbus.com.driverlayer.os_driverProtocol;

public class DriverConstValue { //驱动层常用值
	public enum DriverTransType{//参数发送方式
		TT_CYCLE,
		TT_ASYNC
	}
	public enum DriverPgStatus{//buff状态
		PG_DEFAULT,
		//发送
		PG_TX_FREE,
		PG_TX_REQ,
		PG_TX_TX,
		//接收
		PG_RX_FREE,
		PG_RX_FULL
	}
}