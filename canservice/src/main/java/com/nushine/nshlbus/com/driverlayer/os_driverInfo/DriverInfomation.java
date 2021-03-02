/*ver:0.0.1
 *bref:用于存放车载设备的数据信息。
 */

package com.nushine.nshlbus.com.driverlayer.os_driverInfo;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.app.App;
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.Global_Cfg;


//信息库，包含整个系统所有信息，同时负责上传信息给UI和接收UI控制
public class DriverInfomation{
	Logger log = Logger.getLogger(DriverInfomation.class);
	public InformationBufferDefine m_DriverInfo;

	public DriverInfomation() {
		super();
		App.get().setM_myinfo(this);
	}
	public DriverInfomation(Global_Cfg driver_cfg){
		App.get().setM_myinfo(this);
		m_DriverInfo = new InformationBufferDefine(driver_cfg);
	}
}

