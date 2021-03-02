/*ver:0.0.2
 *bref:该类中主要工作： 1、负责底层数据读取和解析
 *				  2、负责应用成数据打包发送
 *				  3、对设备超时做出判断
 */
package com.nushine.nshlbus.com.driverlayer.os_driverProtocol;

import android.util.Log;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.app.App;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.DriverHard.DriverHardType;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanRxFrame;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.DriverJ1939;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_Error;
import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverProtocolStack.ProtocolAppData.ProtocolPara;
import com.nushine.nshlbus.com.driverlayer.os_driverTime.DriverTimeEngine;

public class DriverProtocol{
	Logger log = Logger.getLogger(DriverProtocol.class);
	DriverJ1939 m_j1939;
	public ArrayList<DriverProtocolStack> m_proapp = new ArrayList<DriverProtocolStack>(20);
	public boolean crateProtApp(DriverProtocolStack stack){
		if(m_proapp.add(stack)){
			return true;
		}else{
			return false;
		}
	}

	public DriverProtocol(){
		m_proapp.clear();
		m_j1939 = new DriverJ1939();
		//启动原始数据接收
		ProManager m_mangerTx = new ProManager();
		m_mangerTx.setName("ProManager");
		m_mangerTx.setPriority(Thread.MAX_PRIORITY);
		m_mangerTx.start();
	}

	public void DriverUnload(){
		m_j1939.DriverJ1939Close();
	}

	class ProManager extends Thread{//原始数据发送
		public void run(){
			while(true){
//				Log.i("MainActivity","ProManager-->"+Thread.currentThread().getName());
				if(App.get().isDestroy()){
					break;
				}
				ProtRecvFrame();
				ProtSendFrame();
				try {
					Thread.sleep(5);
				}catch (Exception e){
				}
			}
		}
	}

	/*
	 * 数据接收处理任务
	 */
	private void ProtRecvFrame(){

		DriverProtocolStack m_port; //应用
		ProtocolPara m_pPng; //接收参数组
		ProtRecvSourceFrame();
		for(byte app=0;app<m_proapp.size();app++){
			m_port = m_proapp.get(app);
			for(byte addr=0;addr<m_port.m_appdata.TotalAddr;addr++){
				for(byte png=0;png<m_port.m_appdata.TotalRecvPara;png++){
					m_pPng = m_port.m_appdata.Recv[addr][png];
					if(m_pPng.mEnable){
						if(m_pPng.mPGState == DriverConstValue.DriverPgStatus.PG_RX_FULL){
							m_pPng.mTimer = DriverTimeEngine.GetSysTem_ms();
							if(!m_port.m_callpro.rxFrame(addr, png)){//解析不同硬件的数据
								m_pPng.mPGState = DriverConstValue.DriverPgStatus.PG_RX_FREE;
							}
						}else if(m_pPng.mTransType==DriverConstValue.DriverTransType.TT_CYCLE){
							if(DriverTimeEngine.CheckTimeOut(m_pPng.mTimer,DriverTimeEngine.GetSysTem_ms(),m_pPng.mTransRate)){
								m_port.m_callpro.rxTimeout(addr, png);//数据接收超时
								m_pPng.mTimer = DriverTimeEngine.GetSysTem_ms();/*reset timer*/
							}
						}
					}else{
					}
				}
			}
		}
	}

	/*
	 * 数据发送处理任务
	 */
	private void ProtSendFrame(){
		DriverProtocolStack m_port; //应用
		ProtocolPara m_pPng; //发送参数组
		m_j1939.J1939_Trans_Main();
		for(byte app=0;app<m_proapp.size();app++){
			m_port = m_proapp.get(app);
			for(byte addr=0;addr<m_port.m_appdata.TotalAddr;addr++){
				for(byte png=0;png<m_port.m_appdata.TotalSendPara;png++){
					m_pPng = m_port.m_appdata.Send[addr][png];
					if(m_pPng.mEnable){
						if(m_pPng.mTransType==DriverConstValue.DriverTransType.TT_CYCLE){
							long newtime = DriverTimeEngine.GetSysTem_ms();
							if(DriverTimeEngine.CheckTimeOut(m_pPng.mTimer,newtime,m_pPng.mTransRate)
									&& m_pPng.mPGState == DriverConstValue.DriverPgStatus.PG_TX_REQ){
								if(allHardWareSend(m_port,addr,png)){
									m_pPng.mPGState = DriverConstValue.DriverPgStatus.PG_TX_TX;
								}
								m_pPng.mTimer = DriverTimeEngine.GetSysTem_ms();
								break;
							}else if(m_pPng.mPGState == DriverConstValue.DriverPgStatus.PG_TX_TX){
								m_pPng.mPGState = DriverConstValue.DriverPgStatus.PG_TX_REQ;
								m_pPng.mTimer = DriverTimeEngine.GetSysTem_ms();
								m_port.m_callpro.txFrame(addr, png); //发送数据准备
								break;
							}
						}else if(m_port.m_appdata.Send[addr][png].mTransType==DriverConstValue.DriverTransType.TT_ASYNC){
							if(m_pPng.mPGState == DriverConstValue.DriverPgStatus.PG_TX_REQ){
								if(allHardWareSend(m_port,addr,png)){
									m_pPng.mPGState = DriverConstValue.DriverPgStatus.PG_TX_TX;
								}
								break;
							}else if(m_pPng.mPGState == DriverConstValue.DriverPgStatus.PG_TX_TX){
								m_pPng.mPGState = DriverConstValue.DriverPgStatus.PG_TX_FREE;
								m_port.m_callpro.txFrame(addr, png);
								break;
							}
						}
					}else{
					}
				}
			}
		}
	}

	//驱动数据发送接口
	private boolean allHardWareSend(DriverProtocolStack app, byte addr, byte png){
		if(app.m_appdata.mHardType == DriverHardType.DH_CAN){
			if(m_j1939.J1939_SendFram(app.m_appdata.Send[addr][png].mHardNum, app.m_callpro.SendFrame(addr, png))
					== J1939_Error.J1939_ERR_OK){
				return true;
			}
		}else if(app.m_appdata.mHardType == DriverHardType.DH_GPRS){

		}
		return false;
	}

	/*
	 * 底层原理数据读取接口
	 */
	private boolean ProtRecvSourceFrame(){
		//原始数据接收
		CanRxFrame msg = new CanRxFrame();
		DriverProtocolStack m_port; //应用
		msg = m_j1939.m_can.Can_GetRecvBuffer();
		if(msg!=null){
			for(byte app=0;app<m_proapp.size();app++){//查找对应设备的协议栈
				m_port = m_proapp.get(app);
				if(m_port.m_appdata.mHardType == DriverHardType.DH_CAN){
					if(m_port.m_callpro.RxSourceData(m_j1939.J1939_RecvFram(msg.mHardNum,msg))){
						//m_j1939.m_can.Can_FreeRecvBuffer();
					}
				}
			}
			m_j1939.m_can.Can_FreeRecvBuffer();
			return true;
		}else{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}

