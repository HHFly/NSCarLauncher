/*ver:0.0.1
 *bref:BCU应用，通过一系列接口，将上报数据解析为对应的信息存放到缓存中
 *log:调试等级：ERROR WARN INFO
 */
package com.nushine.nshlbus.com.driverlayer.os_driverHard.Can;

import android.util.Log;

import imax.can.CanNormalFrame;
import imax.can.CanOperation;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_DataType;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_FrameType;
import com.nushine.nshlbus.app.App;

public class Can{
	public static final Object Frist_Lock = new Object();
	Logger log = Logger.getLogger(Can.class);
	final static boolean CANSEND_USE_BUFF = false;
	final static byte MAX_CAN_NUM = 2;
	final static short MAX_RECVBUFF_SIZE=2048;
	final static int DEBUG_CAN_TEST_EXID = 0X18F50000;
	boolean CAN_Test = false;	//can总线压力测试标志位
	CanRxFrame[] m_Recvbuff;
	short RecvData_GetAddr=0;
	short RecvData_SetAddr=0;
	CanOperation m_can0;
	CanOperation m_can1;
	Hard_CanRecv m_recv0,m_recv1;
	int Tatolcount = 0;
	public Can(){
		RecvData_GetAddr = 0;
		RecvData_SetAddr = 0;
		//接收缓存初始化
		m_Recvbuff = new CanRxFrame[MAX_RECVBUFF_SIZE];
		//初始化can设备
		m_can0 = new CanOperation("can0", 250000);
		m_can1 = new CanOperation("can1", 250000);
		m_recv0 = new Hard_CanRecv(m_can0,100, CanDeviceIndex.CAN_NO0);//动力总线CAN
		m_recv0.setName("Hard_CanRecv0");
		m_recv1 = new Hard_CanRecv(m_can1,50, CanDeviceIndex.CAN_NO1);//车身控制CAN
		m_recv1.setName("Hard_CanRecv1");
		//关闭CAN调试信息输出
		m_can0.isCloseDebugInfo(false);
		m_can1.isCloseDebugInfo(false);
		m_recv0.setPriority(Thread.MAX_PRIORITY);
		m_recv0.start();
		m_recv1.setPriority(Thread.MAX_PRIORITY);
		m_recv1.start();
	}
	public void canClose(){
		m_can0.CanOperationDown();
		m_can1.CanOperationDown();
		//关闭线程
		m_recv0.run_flag=false;
		m_recv1.run_flag=false;
	}
	//从接收缓存中读取一个数据
	public CanRxFrame Can_GetRecvBuffer(){
		CanRxFrame msg;
		if(RecvData_GetAddr==RecvData_SetAddr){
			return null;
		}else{
			synchronized(this){
				msg = (CanRxFrame) m_Recvbuff[RecvData_GetAddr].clone();
			}
		}
		return msg;
	}
	public void Can_FreeRecvBuffer(){
		RecvData_GetAddr = (short) ((RecvData_GetAddr+1)%MAX_RECVBUFF_SIZE);
	}
	//接收数据写入缓存
	public  boolean Can_SetRecvBuffer(CanRxFrame pdat)
	{
		if(((RecvData_SetAddr+1)%MAX_RECVBUFF_SIZE)==RecvData_GetAddr){
//			log.warn("Can Recv Buff Full");
			return false;
		}else{
			synchronized(this){
				m_Recvbuff[RecvData_SetAddr]=null;
				m_Recvbuff[RecvData_SetAddr] = (CanRxFrame) pdat.clone();
				RecvData_SetAddr = (short) ((RecvData_SetAddr+1)%MAX_RECVBUFF_SIZE);
			}
		}
		return true;
	}
	//底层数据数据发送
	public  boolean can_SendMsg(CanDeviceIndex CanNum, final CanTxFrame pSend)
	{
//		Logger log = Logger.getLogger(Hard_CanRecv.class);
		if(CANSEND_USE_BUFF){
			//return CanSendBuff_Set(CanNum, pSend);//采取缓存方式管理数据发送
		}else{
			CanNormalFrame m_canx = new CanNormalFrame();
			if(pSend.IDE == J1939_FrameType.CAN_FRAMTYPE_EXT){
				m_canx.setExtId(pSend.ExtId);
				m_canx.setStdId(0);
				m_canx.setIDE((byte)1);
				m_canx.setRTR((byte)0);
				m_canx.setCan_DLC(pSend.Can_DLC);
				m_canx.setmData(pSend.mData.clone());
			}else{
				m_canx.setExtId(0);
				m_canx.setStdId(pSend.StdId);
				m_canx.setIDE((byte)1);
				m_canx.setRTR((byte)0);
				m_canx.setCan_DLC(pSend.Can_DLC);
				m_canx.setmData(pSend.mData.clone());
			}
//			String str="";
			if(CanNum == CanDeviceIndex.CAN_NO0){
				if(m_can0.CanOperationWrite(m_canx)<=-1){
//					log.error("Can"+CanNum.ordinal()+" Send "+"Failed");
					App.get().setCanStatus(false);
					return false;
				}else{
//					str = m_canx.printfCanNormalFrame(str);
//					log.warn("Can"+CanNum.ordinal()+" Send "+str);
					App.get().setCanStatus(true);
					return true;
				}
			}else if(CanNum == CanDeviceIndex.CAN_NO1){
				if(m_can1.CanOperationWrite(m_canx)<=-1){
//					log.error("Can"+CanNum.ordinal()+" Send "+"Failed");
					App.get().setCanStatus(false);
					return false;
				}else{
//					str = m_canx.printfCanNormalFrame(str);
//					log.warn("Can"+CanNum.ordinal()+" Send "+str);
					App.get().setCanStatus(true);
					return true;
				}
			}else{
				return true;
			}
		}
		return false;
	}

	/*
	 * can0:接收动力数据
	 * can1:接收车身状态数据和升级数据
	 */
	class Hard_CanRecv extends Thread{
		int ReadLen ;	//数据接收触发帧数量
		CanOperation m_can;
		CanDeviceIndex m_num;
		int m_len=0;
		int m_timeout=0;
		final int Time_OutValue=3000;	//3秒钟内收不到数据认为数据接收停止，线程进入休眠状态
		final int Thread_Min_Sleep=5;
		final int Thread_Max_Sleep=10;
		int Cur_SleepTime=0;
		CanNormalFrame[] buff_Can;
		boolean run_flag=false;
		Hard_CanRecv(CanOperation m_can, int event_count, CanDeviceIndex m_no){
			this.m_can = m_can;
			this.ReadLen = event_count;
			this.m_num = m_no;
			buff_Can = new CanNormalFrame[this.ReadLen];
			for(int i=0;i<buff_Can.length;i++){
				buff_Can[i] = new CanNormalFrame();
			}
			Cur_SleepTime=Thread_Max_Sleep;
			run_flag = true;
		}

		public void run(){
			while(run_flag){
//				Log.i("MainActivity","canthread-->"+Thread.currentThread().getName());
				if(App.get().isDestroy()){
					break;
				}
				try{
					m_len = m_can.CanOperationRead(buff_Can,buff_Can.length);
				}catch(Exception e){
					log.error(e.getMessage());
				}
				if(m_len>0){
					Can_Change(m_len,buff_Can,m_num);
					m_timeout=0;
					Cur_SleepTime=Thread_Min_Sleep;
					try {
						Thread.sleep(Cur_SleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					try {
//						if(Cur_SleepTime==Thread_Min_Sleep){
//							m_timeout+=Thread_Min_Sleep;
//						}else{
//							m_timeout += Thread_Max_Sleep;
//						}
//						if(m_timeout>=Time_OutValue){
//							m_timeout = Time_OutValue;
//							Cur_SleepTime = Thread_Max_Sleep;
//						}
//						sleep(Cur_SleepTime-1);
						sleep(Thread_Max_Sleep);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		void Can_Change(int bufflength, CanNormalFrame[] buff, CanDeviceIndex cannum){
			CanRxFrame msg = new CanRxFrame();
			for(int i=0;i<bufflength;i++){
				if(buff[i].getIDE()==1){
					msg.IDE = J1939_FrameType.CAN_FRAMTYPE_EXT;
					msg.ExtId =  buff[i].getExtId();
				}else{
					msg.IDE = J1939_FrameType.CAN_FRAMTYPE_STD;
					msg.StdId = buff[i].getStdId();
				}
				msg.Can_DLC = buff[i].getCan_DLC();
				msg.FMI = 0;
				msg.mHardNum = cannum;
				if(buff[i].getRTR() ==0){//数据帧
					msg.RTR = J1939_DataType.CAN_RTR_DATA;
				}else{
					msg.RTR = J1939_DataType.CAN_RTR_REMOTE;
				}
				msg.mData = buff[i].getmData().clone();
				Can_SetRecvBuffer(msg);
				/*CAN总线数据存储*/
				if(msg.ExtId == DEBUG_CAN_TEST_EXID){
					if(msg.mData[0]==0xaa){
						CAN_Test = true;
					}else{
						CAN_Test = false;
					}
				}
				if(CAN_Test){
					//writeExcel(msg);
				}
			}
		}
	}
}


