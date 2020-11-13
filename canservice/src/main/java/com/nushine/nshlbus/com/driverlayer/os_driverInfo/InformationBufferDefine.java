/*ver:0.0.1
 *auth:hpp
 *data:15.4.29
 *bref:AC应用解析及控制车身信息
 *Revision record:0.0.1 初版实现《康迪K12汽车空调通讯规约V1.0》
 */
package com.nushine.nshlbus.com.driverlayer.os_driverInfo;

import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverAC;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverBcm;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverLCG;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverMFL;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverTBOX;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.DriverVCU;
import com.nushine.nshlbus.com.driverlayer.os_driverApp.Temperature;
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.Global_Cfg;

public class InformationBufferDefine{
	public DriverAC m_AC;
	public DriverBcm m_Bcm;
	public DriverLCG m_LCG;
	public DriverMFL m_MFL;
	public DriverVCU m_Vcu;
	public DriverTBOX m_TBOX;
	public Temperature m_SensorTemp;	//集控屏温度值

	public InformationBufferDefine(Global_Cfg cfg){
		m_AC = new DriverAC(cfg);
		m_Bcm = new DriverBcm(cfg);
		m_LCG = new DriverLCG(cfg);
		m_MFL = new DriverMFL(cfg);
		m_Vcu = new DriverVCU(cfg);
		m_TBOX = new DriverTBOX(cfg);
		m_SensorTemp = new Temperature();	//温度应用
	}
}