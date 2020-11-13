package com.nushine.nshlbus.com.driverlayer.os_driverServer;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverProtocol;

public class DriverCfg {
	Logger log = Logger.getLogger(DriverCfg.class);
	public Global_Cfg m_gcfg=new Global_Cfg();

	public DriverCfg(){
		m_gcfg.m_stack.m_port = new DriverProtocol(); //Protocol启动
	}
	//定义一系列的配置数据
	public  class Global_Cfg{	//配置参数
		public   cfgPortStack m_stack=new cfgPortStack();
		public   cfgBmu m_bmu=new cfgBmu();
	}
	//协议栈配置，与外设数量对应
	public  class cfgPortStack{//protocol栈初始化配置
		public DriverProtocol m_port ; //Protocol启动
	}
	//BCU参数配置，比如一个BCU包含多少节电池
	public  class cfgBmu {//配置bcu
		public int bat_num=8;
		public int cell_num=50;
		public int temp_num=50;
		public byte batIdSerial_length=16;
		public byte carVIN_length=17;

		public int warring_num=50;
		public int err_num=50;
	}
}

