/*ver:0.0.1
 *auth:hpp
 *data:15.4.23
 *bref:BCM应用解析及控制车身信息
 *Revision record:0.0.1 初版实现《康迪汽车BCM控制通讯规约V1.1》
 */

package com.nushine.nshlbus.com.driverlayer.os_driverApp;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanDeviceIndex;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.DriverHard.DriverHardType;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_FrameType;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939FrameFormat;
import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverConstValue.DriverPgStatus;
import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverConstValue.DriverTransType;
import com.nushine.nshlbus.com.driverlayer.os_driverProtocol.DriverProtocolStack;
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.Global_Cfg;
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.cfgPortStack;

public class DriverBcm {
	public boolean BcmDeBug=false;	 //Bcu调试开关
	public BCM_STATUS m_BCMStatus=BCM_STATUS.BCMSTATUS_NORMAL;	//BCM工作状态信息
	public Bcm_Info m_BcmInfo=new Bcm_Info();
	public DriverBcmTask     m_bcmtcb; //BCU控制块

	public DriverBcm(Global_Cfg cfg){
		m_bcmtcb = new DriverBcmTask(cfg.m_stack);
	}

	public enum BCM_STATUS{
		BCMSTATUS_INVALIDE,
		BCMSTATUS_NORMAL,
		BCMSTATUS_ERR,
		BCMSTATUS_TOTAL
	}

	public enum BCM_WINDOWS_CONTROL{
		WINDOWS_INVALIDE,
		WINDOWS_UP,
		WINDOWS_DOWN,
		WINDOWS_PAUSE,
		WINDOWS_TOTAL
	}
	//车门状态
	public enum BCM_DOOR_CONTROL{
		DOOR_INVALIDE,
		DOOR_LOCK,
		DOOR_UNLOCK,
		DOOR_TOTAL
	}
	public enum BCM_CAR_EXLIGTH_CONTROL{
		LIGHT_INVALIDE,
		LIGHT_FAR,
		LIGHT_NEAR,
		LIGTH_CLOSE,
		WINDOWS_TOTAL
	}

	public enum BCM_BOOLEAN{
		BOOLEAN_INVALIDE,
		BOOLEAN_TRUE,
		BOOLEAN_FALSE,
		BOOLEAN_TOTAL
	}

	public class Bcm_Info{
		//基本信息
		public byte m_vender=(byte) 0xff;
		public short m_softver=0;
		public short m_hardver=0;

		public short m_BcmVolt_Rtd = 0;
		public short m_BcmCur_Rtd = 0;
		public byte[] m_bcmvincode = new byte[6];

		public Bcm_CarControl m_setcarcontrolbak=new Bcm_CarControl();
		public Bcm_CarControl m_setcarcontrol=new Bcm_CarControl();
		public Bcm_CarControl m_getcarcontrol=new Bcm_CarControl();
	}

	public class Bcm_CarControl{//车身控制信息
		public BCM_DOOR_CONTROL[] m_cardoor_lockstatus=new BCM_DOOR_CONTROL[3];//1个中控锁,后备箱+充电盖
		public BCM_WINDOWS_CONTROL[] m_carwindow_seting = new BCM_WINDOWS_CONTROL[4];
		public BCM_DOOR_CONTROL[] m_cardoor_status=new BCM_DOOR_CONTROL[4];//左前门，右前门，左后门，右后门
		public BCM_CAR_EXLIGTH_CONTROL m_ex_carlamp=BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE;//汽车大灯控制
		public BCM_BOOLEAN m_doublelamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//双跳
		public BCM_BOOLEAN m_frontfoglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//前雾灯
		public BCM_BOOLEAN m_rearfoglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//后雾灯
		public BCM_BOOLEAN m_readlamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//小灯
		public BCM_BOOLEAN m_backlamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//倒车灯
		public BCM_BOOLEAN m_leftturnninglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//左转向灯
		public BCM_BOOLEAN m_rightturnninglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;//左转向灯
		//	public BCU_CAR_ASSISTEDS m_addwayforce=BCU_CAR_ASSISTEDS.FORCE_MIDDLE;//方向助力
		public BCM_BOOLEAN m_horn=BCM_BOOLEAN.BOOLEAN_INVALIDE;//喇叭
		public byte m_antitheft=0;//防盗
		public short m_epsesclpowena = 0;//0：禁止；1：使能；2：故障
		public byte m_vwcsforce = 0;//0xAA：强制标志；其他无效
		public BCM_WINDOWS_CONTROL m_BatDoor=BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;;//电池仓门
		public boolean OneKeyOpenWindow=false;	//一键开窗
		Bcm_CarControl(){
			for(int i=0;i<m_carwindow_seting.length;i++){
				m_carwindow_seting[i] = BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;
			}

			for(int i=0;i<m_cardoor_lockstatus.length;i++){
				m_cardoor_lockstatus[i] = BCM_DOOR_CONTROL.DOOR_INVALIDE;
			}

			for(int i=0;i<m_cardoor_status.length;i++){
				m_cardoor_status[i] = BCM_DOOR_CONTROL.DOOR_INVALIDE;
			}
		}
	}

	public void Change_BCMControlSendCycle(){
		m_bcmtcb.Port_BcmImmediatelySend();
	}
	//BCU协议栈
	class Driver_Bcm_DataBuf{
		//协议栈
		final BcmConst_PARA[] Recv_PngData = {
				new BcmConst_PARA(0xa4,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//BCM设备信息
				new BcmConst_PARA(0xa4,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,2000), //车身状态控制
				new BcmConst_PARA(0xa4,0x08,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//额定电压、电流
				new BcmConst_PARA(0xa4,0xFB,0x02,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,5000),//设备唯一码
		};
		final BcmConst_PARA[] Send_PngData = {
				new BcmConst_PARA(0xF0,0x50,0xA4,4,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500), //车身控制
		};
		//收发原始数据缓存定义
		final  byte CONST_BCM_DATALEN=8;//数据长度
		final  short MAX_RECVBUFF_SIZE=512;//每个png可以存储的报文数据量
		int[] Bcm_RecvGetAddr = new int[BCM_RX_PNG.BCM_RX_PNG_TOTAL.ordinal()];//接收缓存读指针
		int[] Bcm_RecvSetAddr = new int[BCM_RX_PNG.BCM_RX_PNG_TOTAL.ordinal()];//接收缓存写指针
		short[][][] m_BcmDataRcev_Buff = new short[BCM_RX_PNG.BCM_RX_PNG_TOTAL.ordinal()][MAX_RECVBUFF_SIZE][CONST_BCM_DATALEN];
		short[][] m_BcmDataSend_Buff = new short[BCM_TX_PNG.BCM_TX_PNG_TOTAL.ordinal()][CONST_BCM_DATALEN];//每帧数据的发送缓存只需要分配一个空间

		Driver_Bcm_DataBuf(){
			//数据收发缓存初始化，收发指针初始化
			for(int i=0;i<Bcm_RecvGetAddr.length;i++){
				Bcm_RecvGetAddr[i]=0;
			}
			for(int i=0;i<Bcm_RecvSetAddr.length;i++){
				Bcm_RecvSetAddr[i]=0;
			}
			for(int i=0;i<m_BcmDataRcev_Buff.length;i++)
				for(int j=0;j<m_BcmDataRcev_Buff[i].length;j++)
					for(int k=0;k<m_BcmDataRcev_Buff[i][j].length;k++){
						m_BcmDataRcev_Buff[i][j][k]=0;
					}
			for(int i=0;i<m_BcmDataSend_Buff.length;i++)
				for(int j=0;j<m_BcmDataSend_Buff[i].length;j++){
					m_BcmDataSend_Buff[i][j] = 0;
				}
		}

		//从接收缓存中读取一个数据
		public  short[]  Bcm_GetRecvBuffer(int png){
			short[] data;
			if(Bcm_RecvGetAddr[png]==Bcm_RecvSetAddr[png]){
				return null;
			}else{
				synchronized(this){
					data = m_BcmDataRcev_Buff[png][Bcm_RecvGetAddr[png]].clone();
					Bcm_RecvGetAddr[png] = (short) ((Bcm_RecvGetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return data;
		}
		//接收数据写入缓存
		public  boolean Can_SetRecvBuffer(short[] data,int png)
		{
			if(((Bcm_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE)==Bcm_RecvGetAddr[png]){
				return false;
			}else{
				synchronized(this){
					m_BcmDataRcev_Buff[png][Bcm_RecvSetAddr[png]] = null;
					m_BcmDataRcev_Buff[png][Bcm_RecvSetAddr[png]] = data.clone();
					Bcm_RecvSetAddr[png] = (short) ((Bcm_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return true;
		}
	}

	//协议解析类
	class DriverBcmTask extends DriverCallPro {
		final short BcmApp_Addr_Start = 0xFF;	//BCM默认地址
		final short BcmApp_Addr_Code = 0x02;	//code接收默认
		final short Bcm_Num = 1;				//BCM默认数量
		Logger log = Logger.getLogger(DriverBcmTask.class);
		final Driver_Bcm_DataBuf m_bcmapp = new Driver_Bcm_DataBuf();//协议栈定义
		DriverProtocolStack m_protbcm; //定义一个应用堆栈

		DriverBcmTask(cfgPortStack stack) {
			//协议栈初始化
			InitAppData();
			m_protbcm.m_callpro = this;
			if(stack.m_port.crateProtApp(m_protbcm)){
				log.debug("DriverBcmTask is create success!");
			}else{
				log.debug("DriverBcmTask is create failed!");
			}
		}

		void InitAppData(){
			m_protbcm = new DriverProtocolStack(Bcm_Num,(short) m_bcmapp.Send_PngData.length,(short) m_bcmapp.Recv_PngData.length);
			m_protbcm.m_appdata.mHardType = DriverHardType.DH_CAN;//can设备
			for(int i=0;i<m_protbcm.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protbcm.m_appdata.TotalRecvPara;j++){
					m_protbcm.m_appdata.Recv[i][j].mDataMaxLen = m_bcmapp.CONST_BCM_DATALEN;
					m_protbcm.m_appdata.Recv[i][j].mDataLen = m_bcmapp.CONST_BCM_DATALEN;
					m_protbcm.m_appdata.Recv[i][j].mSource = (byte) m_bcmapp.Recv_PngData[j].sa;
					m_protbcm.m_appdata.Recv[i][j].mTransRate = (int) m_bcmapp.Recv_PngData[j].rate;
					m_protbcm.m_appdata.Recv[i][j].mTimer = 0;
					m_protbcm.m_appdata.Recv[i][j].mEnable = true;
					m_protbcm.m_appdata.Recv[i][j].mPGState = DriverPgStatus.PG_RX_FREE;
					m_protbcm.m_appdata.Recv[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protbcm.m_appdata.Recv[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
			for(int i=0;i<m_protbcm.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protbcm.m_appdata.TotalSendPara;j++){
					m_protbcm.m_appdata.Send[i][j].mDataMaxLen = m_bcmapp.CONST_BCM_DATALEN;
					m_protbcm.m_appdata.Send[i][j].mDataLen = m_bcmapp.CONST_BCM_DATALEN;
					m_protbcm.m_appdata.Send[i][j].mSource = (short) m_bcmapp.Send_PngData[j].sa;
					m_protbcm.m_appdata.Send[i][j].mTransRate = (int) m_bcmapp.Send_PngData[j].rate;
					m_protbcm.m_appdata.Send[i][j].mTimer = 0;
					if(j == BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()){
						m_protbcm.m_appdata.Send[i][j].mEnable = false;
					}else{
						m_protbcm.m_appdata.Send[i][j].mEnable = true;
					}
					m_protbcm.m_appdata.Send[i][j].mPGState = DriverPgStatus.PG_TX_TX;
					m_protbcm.m_appdata.Send[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protbcm.m_appdata.Send[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
		}
		//接收回调接口
		public boolean RxSourceData(J1939FrameFormat j1939){
			int m_rxnum = BCM_RX_PNG.BCM_RX_PNG_TOTAL.ordinal();
			//判断报文是否属于BCU
			for(int i = 0; i <m_rxnum;i++){
				if(j1939.PS == m_bcmapp.Recv_PngData[i].ps
						&& j1939.PF == m_bcmapp.Recv_PngData[i].pf
						&& j1939.FramType == m_bcmapp.Recv_PngData[i].framtype){
					if(j1939.Addr >= m_bcmapp.Recv_PngData[i].sa){
						short addr = (short) (j1939.Addr - m_bcmapp.Recv_PngData[i].sa);
						if(addr >= Bcm_Num || addr <0){
							return false;
						}
						if(m_protbcm.m_appdata.Recv[addr][i].mEnable){
							if(j1939.DtaLen <= m_protbcm.m_appdata.Recv[addr][i].mDataMaxLen){
								m_bcmapp.Can_SetRecvBuffer(j1939.pDta,(byte)i);
								m_protbcm.m_appdata.Recv[addr][i].mDataLen = (byte) j1939.DtaLen;
								m_protbcm.m_appdata.Recv[addr][i].mPGState = DriverPgStatus.PG_RX_FULL;
							}
						}
						return true;
					}else{
						return false;
					}
				}
			}
			return false;
		}

		@Override
		public void txFrame(int lpara, int wpara) {

			// TODO Auto-generated method stub
			if(wpara == BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()){
				Tx_BcmControl(lpara);
			}
		}

		@Override
		public boolean rxFrame(int lpara, int wpara) {
			// TODO Auto-generated method stub
			short[] m_data = new short[8];
			m_data = m_bcmapp.Bcm_GetRecvBuffer(wpara);
			if(m_data==null || BcmDeBug){	//调试模式下不接受CAN总线数据
				return false;
			}

			if(wpara == BCM_RX_PNG.BCM_RX_BASEINFO.ordinal()){
				Rx_BcmBaseInfo(m_data,lpara);
			}else if(wpara == BCM_RX_PNG.BCM_RX_RATEDINFO.ordinal()){
				Rx_BcmRatedInfo(m_data,lpara);
			}else if(wpara == BCM_RX_PNG.BCM_RX_STATUS.ordinal()){
				Rx_BcmStatus(m_data,lpara);
				timeoutnum = 0;
			}else if(wpara == BCM_RX_PNG.BCM_RX_VINCODE.ordinal()){
				Rx_BcmVincode(m_data,lpara);
			}else{
				return false;
			}
			return true;
		}

		@Override
		public void rxTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub
			if(wpara == BCM_RX_PNG.BCM_RX_STATUS.ordinal()){
				Rx_BcmTimeOut(lpara);
			}
		}

		@Override
		public void txTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub

		}

		@Override
		public J1939FrameFormat SendFrame(int lpara, int wpara) {//CAN数据发送接口
			// TODO Auto-generated method stub
			if(wpara>BCM_TX_PNG.BCM_TX_PNG_TOTAL.ordinal()){
				return null;
			}

			J1939FrameFormat m_send = new J1939FrameFormat();
			m_send.FramType = m_bcmapp.Send_PngData[wpara].framtype;//DriverBcuTask.m_protbcu.J1939_FrameType;
			m_send.PF = m_bcmapp.Send_PngData[wpara].pf;
			m_send.PS = m_bcmapp.Send_PngData[wpara].ps;
			m_send.Addr = m_protbcm.m_appdata.Send[lpara][wpara].mSource;
			m_send.Prior = m_bcmapp.Send_PngData[wpara].prior;
			m_send.Page = m_bcmapp.Send_PngData[wpara].page;
			m_send.DtaLen = m_bcmapp.CONST_BCM_DATALEN;
			m_send.pDta = m_bcmapp.m_BcmDataSend_Buff[wpara].clone();
			for(int i=0;i<m_bcmapp.m_BcmDataSend_Buff[wpara].length;i++){
				m_bcmapp.m_BcmDataSend_Buff[wpara][i]=0;
			}
			return m_send;
		}

		/****************数据接收处理****************/
		private void Rx_BcmBaseInfo(short[] pdta,int Addr){
			m_BcmInfo.m_vender = (byte) pdta[0];
			m_BcmInfo.m_hardver = (short) ((pdta[2]&0xff)<<8|(pdta[3]&0xff));
			m_BcmInfo.m_softver = (short) ((pdta[4]&0xff)<<8|(pdta[5]&0xff));
		}
		private void Rx_BcmRatedInfo(short[] pdta,int Addr){
			m_BcmInfo.m_BcmVolt_Rtd = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_BcmInfo.m_BcmCur_Rtd = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
		}
		private void Rx_BcmVincode(short[] pdta,int Addr){
			for(int i=0;i<m_BcmInfo.m_bcmvincode.length;i++){
				m_BcmInfo.m_bcmvincode[i] = (byte) pdta[i+1];
			}
		}
		//BCU工作工状态信息
		private void Rx_BcmStatus(short[] pdta,int Addr){
			m_BCMStatus = BCM_STATUS.BCMSTATUS_NORMAL;
			for(int i=0;i<m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus.length;i++){
				if(((pdta[0]>>i)&0x01)==0x01){
					m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus[i]=BCM_DOOR_CONTROL.DOOR_UNLOCK;
				}
				else{
					m_BcmInfo.m_getcarcontrol.m_cardoor_lockstatus[i]=BCM_DOOR_CONTROL.DOOR_LOCK;
				}
			}

			if(((pdta[0]>>3)&0x03)==0x01){
				m_BcmInfo.m_getcarcontrol.m_BatDoor = BCM_WINDOWS_CONTROL.WINDOWS_UP;
			}
			else if(((pdta[0]>>3)&0x03)==0x02){
				m_BcmInfo.m_getcarcontrol.m_BatDoor = BCM_WINDOWS_CONTROL.WINDOWS_DOWN;
			}
			else if(((pdta[0]>>3)&0x03)==0x03){
				m_BcmInfo.m_getcarcontrol.m_BatDoor = BCM_WINDOWS_CONTROL.WINDOWS_PAUSE;
			}
			else{
				m_BcmInfo.m_getcarcontrol.m_BatDoor = BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;
			}

			short m_value=pdta[1];
			for(int i=0;i<m_BcmInfo.m_getcarcontrol.m_carwindow_seting.length;i++){
				if(((m_value>>(i*2))&0x03)==0x01){
					m_BcmInfo.m_getcarcontrol.m_carwindow_seting[i] = BCM_WINDOWS_CONTROL.WINDOWS_UP;
				}
				else if(((m_value>>(i*2))&0x03)==0x02){
					m_BcmInfo.m_getcarcontrol.m_carwindow_seting[i] = BCM_WINDOWS_CONTROL.WINDOWS_DOWN;
				}
				else if(((m_value>>(i*2))&0x03)==0x03){
					m_BcmInfo.m_getcarcontrol.m_carwindow_seting[i] = BCM_WINDOWS_CONTROL.WINDOWS_PAUSE;
				}
			}

			for(int i=0;i<m_BcmInfo.m_getcarcontrol.m_cardoor_status.length;i++){//添加4个门的状态
				if(((pdta[2]>>i)&0x01)==0x01){
					m_BcmInfo.m_getcarcontrol.m_cardoor_status[i]=BCM_DOOR_CONTROL.DOOR_UNLOCK;
				}else{
					m_BcmInfo.m_getcarcontrol.m_cardoor_status[i]=BCM_DOOR_CONTROL.DOOR_LOCK;
				}
			}

			if((pdta[3]&0x03)==0x01){
				m_BcmInfo.m_getcarcontrol.m_ex_carlamp = BCM_CAR_EXLIGTH_CONTROL.LIGHT_FAR;
			}
			else if((pdta[3]&0x03)==0x02){
				m_BcmInfo.m_getcarcontrol.m_ex_carlamp = BCM_CAR_EXLIGTH_CONTROL.LIGHT_NEAR;
			}
			else if((pdta[3]&0x03)==0x03){
				m_BcmInfo.m_getcarcontrol.m_ex_carlamp = BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE;
			}

			if((pdta[3]&0x04)==0x04){
				m_BcmInfo.m_getcarcontrol.m_doublelamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}
			else{
				m_BcmInfo.m_getcarcontrol.m_doublelamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[3]&0x08)==0x08){
				m_BcmInfo.m_getcarcontrol.m_frontfoglamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}
			else{
				m_BcmInfo.m_getcarcontrol.m_frontfoglamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[3]&0x10)==0x10){
				m_BcmInfo.m_getcarcontrol.m_rearfoglamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}
			else{
				m_BcmInfo.m_getcarcontrol.m_rearfoglamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[3]&0x20)==0x20){
				m_BcmInfo.m_getcarcontrol.m_readlamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}
			else{
				m_BcmInfo.m_getcarcontrol.m_readlamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[3]&0x40)==0x40){
				m_BcmInfo.m_getcarcontrol.m_backlamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}
			else{
				m_BcmInfo.m_getcarcontrol.m_backlamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[4]&0x03)==0x02){
				m_BcmInfo.m_getcarcontrol.m_leftturnninglamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}
			else if((pdta[4]&0x03)==0x01){
				m_BcmInfo.m_getcarcontrol.m_leftturnninglamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[4]&0x0c)==0x08){
				m_BcmInfo.m_getcarcontrol.m_rightturnninglamp =BCM_BOOLEAN.BOOLEAN_TRUE;
			}else if((pdta[4]&0x0c)==0x04){
				m_BcmInfo.m_getcarcontrol.m_rightturnninglamp =BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if((pdta[6]&0x01) == 0x01){
				m_BcmInfo.m_getcarcontrol.m_horn = BCM_BOOLEAN.BOOLEAN_TRUE;
			}else{
				m_BcmInfo.m_getcarcontrol.m_horn = BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			m_BcmInfo.m_getcarcontrol.m_antitheft = (byte) ((pdta[6]>>1)&0x03);

			m_BcmInfo.m_getcarcontrol.m_epsesclpowena = (short) ((pdta[6]>>3)&0x03);
		}

		byte timeoutnum=0;
		private void Rx_BcmTimeOut(int Addr){
			final byte BCM_TIMEOUT_MAX = 3;	//bcm通讯3次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>BCM_TIMEOUT_MAX){
				timeoutnum = 0;
				m_BCMStatus=BCM_STATUS.BCMSTATUS_ERR;
				log.warn("BCM timeout!");
			}
		}

		//整车控制接口
		long time ,end;
		boolean flag = false;
		private void Tx_BcmControl(int Addr){
//			/**闪灯鸣笛逻辑处理*/
//			boolean isSearch = CrashApplication.getInstance().isSearch();
//			if(isSearch){
//				isSearchbuf = true;
//			}
			//m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mPGState = DriverPgStatus.PG_TX_FREE;
			short temp = m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0];
			for(int i=0;i<m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus.length;i++){
				if(m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[i] == BCM_DOOR_CONTROL.DOOR_UNLOCK){
					temp &=(short) (~(3<<2*i)&0xff);
					m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] = (short) (temp|(2<<2*i));
					temp = m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0];
				}else if(m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[i] == BCM_DOOR_CONTROL.DOOR_LOCK){
					temp &=(short) (~(3<<2*i)&0xff);
					m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] = (short) (temp|(1<<2*i));
					temp = m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0];
				}else{
					temp &=(short) (~(3<<2*i)&0xff);
					m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] = temp;
				}
			}

			if(m_BcmInfo.m_setcarcontrol.m_BatDoor == BCM_WINDOWS_CONTROL.WINDOWS_UP){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] |= 0x40;
			}else if(m_BcmInfo.m_setcarcontrol.m_BatDoor == BCM_WINDOWS_CONTROL.WINDOWS_DOWN){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] |= 0x80;
			}else if(m_BcmInfo.m_setcarcontrol.m_BatDoor == BCM_WINDOWS_CONTROL.WINDOWS_PAUSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] |= 0xc0;
			}
			else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] &= (~0xc0);
			}

			int m_value=0;
			BCM_WINDOWS_CONTROL m_control;
			for(int i=0;i<m_BcmInfo.m_setcarcontrol.m_carwindow_seting.length;i++){
				m_control = m_BcmInfo.m_setcarcontrol.m_carwindow_seting[i];
				if(m_control == BCM_WINDOWS_CONTROL.WINDOWS_UP){
					m_value |= (1<<(i*2))&0x3fff;
				}else if(m_control == BCM_WINDOWS_CONTROL.WINDOWS_DOWN){
					m_value |= (2<<(i*2))&0x3fff;
				}else if(m_control == BCM_WINDOWS_CONTROL.WINDOWS_PAUSE){
					m_value |= (3<<(i*2))&0x3fff;
				}
				else{
					m_value &= ~((3<<(i*2))&0x3fff);
				}
			}
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][1] = (short) (m_value&0x00ff);
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][2] = (short) ((m_value&0xff00)>>8);

			if(m_BcmInfo.m_setcarcontrol.m_ex_carlamp == BCM_CAR_EXLIGTH_CONTROL.LIGHT_FAR){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] = 0x01;
			}else if(m_BcmInfo.m_setcarcontrol.m_ex_carlamp == BCM_CAR_EXLIGTH_CONTROL.LIGHT_NEAR){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] = 0x02;
			}else if(m_BcmInfo.m_setcarcontrol.m_ex_carlamp == BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] = 0x03;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] = 0x00;
			}

			if(m_BcmInfo.m_setcarcontrol.m_readlamp == BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] |= 0x08;
			}else if(m_BcmInfo.m_setcarcontrol.m_readlamp == BCM_BOOLEAN.BOOLEAN_FALSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] |= 0x04;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] &= ((~(3<<2))&0xff);
			}

			if(m_BcmInfo.m_setcarcontrol.m_frontfoglamp == BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] |= 0x20;
			}else if(m_BcmInfo.m_setcarcontrol.m_frontfoglamp == BCM_BOOLEAN.BOOLEAN_FALSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] |= 0x10;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] &= ((~(3<<4))&0xff);
			}

			if(m_BcmInfo.m_setcarcontrol.m_rearfoglamp == BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] |= 0x80;
			}else if(m_BcmInfo.m_setcarcontrol.m_rearfoglamp == BCM_BOOLEAN.BOOLEAN_FALSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] |= 0x40;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] &= ((~(3<<6))&0xff);
			}

			//双跳
			if(m_BcmInfo.m_setcarcontrol.m_doublelamp==BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] = 0x02;
			}else if(m_BcmInfo.m_setcarcontrol.m_doublelamp==BCM_BOOLEAN.BOOLEAN_FALSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] = 0x01;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] = 0x00;
			}

			if(m_BcmInfo.m_setcarcontrol.m_backlamp==BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] |= 0x08;
			}else if(m_BcmInfo.m_setcarcontrol.m_backlamp==BCM_BOOLEAN.BOOLEAN_FALSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] |= 0x04;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] &= ((~(3<<2))&0xff);
			}

			if(m_BcmInfo.m_setcarcontrol.m_leftturnninglamp==BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] = 0x02;
			}else if(m_BcmInfo.m_setcarcontrol.m_leftturnninglamp==BCM_BOOLEAN.BOOLEAN_FALSE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] = 0x01;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] = 0x00;
			}

			if(m_BcmInfo.m_setcarcontrol.m_rightturnninglamp==BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] |= 0x08;
			}else if(m_BcmInfo.m_setcarcontrol.m_rightturnninglamp==BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] |= 0x04;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] &= ((~(3<<2))&0xff);
			}
			if(m_BcmInfo.m_setcarcontrol.m_horn==BCM_BOOLEAN.BOOLEAN_TRUE){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] = 0x01;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] = 0x00;
			}

			if(m_BcmInfo.m_setcarcontrol.m_antitheft==1){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] |= 0x02;
			}else if(m_BcmInfo.m_setcarcontrol.m_antitheft==2){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] |= 0x04;
			}else{
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] &= ((~(3<<1))&0xff);
			}

			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][7] = m_BcmInfo.m_setcarcontrol.m_vwcsforce;

			synchronized(this){
				int rate = m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate;
				if(rate==0){
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=1;
				}
				else if(rate==1){
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=21;
				}else if(rate==21){	//发送3帧无效数据
					Invalide(Addr);
//					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=22;
//					if(isSearchbuf == true && isSearchbuf != isSearch){
//						m_BcmInfo.m_setcarcontrol.m_doublelamp = BCM_BOOLEAN.BOOLEAN_FALSE;
//						m_BcmInfo.m_setcarcontrol.m_ex_carlamp = BCM_CAR_EXLIGTH_CONTROL.LIGTH_CLOSE;
//						m_BcmInfo.m_setcarcontrol.m_readlamp = BCM_BOOLEAN.BOOLEAN_FALSE;
//						isSearchbuf = false;
//						Change_BCMControlSendCycle();
//					}
//				}else if(rate==22){
//					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=23;
//				}else if(rate==23){
//					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=24;
//				}else{
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mEnable=false; //无效数据3帧发送结束后关闭数据发送
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate = (int) m_bcmapp.Send_PngData[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].rate;
				}
			}
		}

//		boolean isSearchbuf = false;
//		int count = 0;
		/*
		 * bef:控制报文3帧发送完毕后需要设置为无效状态
		 */
		void Invalide(int Addr){
			//电池舱门
			if(m_BcmInfo.m_setcarcontrol.m_BatDoor==BCM_WINDOWS_CONTROL.WINDOWS_PAUSE){
				m_BcmInfo.m_setcarcontrol.m_BatDoor=BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;
			}
			//大灯状态发送完毕后状态设为无效
			if(m_BcmInfo.m_setcarcontrol.m_ex_carlamp!=BCM_CAR_EXLIGTH_CONTROL.LIGHT_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_ex_carlamp=BCM_CAR_EXLIGTH_CONTROL.LIGHT_INVALIDE;
			}
			//小灯状态置为无效
			if(m_BcmInfo.m_setcarcontrol.m_readlamp!=BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_readlamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			//前雾灯状态置为无效
			if(m_BcmInfo.m_setcarcontrol.m_frontfoglamp!=BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_frontfoglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			//后雾灯状态置为无效
			if(m_BcmInfo.m_setcarcontrol.m_rearfoglamp!=BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_rearfoglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			//后雾灯状态置为无效
			if(m_BcmInfo.m_setcarcontrol.m_rearfoglamp!=BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_rearfoglamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			//双跳灯状态置为无效
			if(m_BcmInfo.m_setcarcontrol.m_doublelamp!=BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_doublelamp=BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			//发送完毕后设置车窗状态为无效
			if(m_BcmInfo.m_setcarcontrol.OneKeyOpenWindow == true){
				for(int i=0;i<m_BcmInfo.m_setcarcontrol.m_carwindow_seting.length;i++){
					if(m_BcmInfo.m_setcarcontrol.m_carwindow_seting[i]!=BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE){
						m_BcmInfo.m_setcarcontrol.m_carwindow_seting[i]=BCM_WINDOWS_CONTROL.WINDOWS_INVALIDE;
					}
				}
				m_BcmInfo.m_setcarcontrol.OneKeyOpenWindow = false;
			}

			if(m_BcmInfo.m_setcarcontrol.m_backlamp != BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_backlamp = BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			if(m_BcmInfo.m_setcarcontrol.m_leftturnninglamp != BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_leftturnninglamp = BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			if(m_BcmInfo.m_setcarcontrol.m_rightturnninglamp != BCM_BOOLEAN.BOOLEAN_INVALIDE){
				m_BcmInfo.m_setcarcontrol.m_rightturnninglamp = BCM_BOOLEAN.BOOLEAN_INVALIDE;
			}

			if(m_BcmInfo.m_setcarcontrol.m_horn == BCM_BOOLEAN.BOOLEAN_TRUE){
				m_BcmInfo.m_setcarcontrol.m_horn = BCM_BOOLEAN.BOOLEAN_FALSE;
			}

			if(m_BcmInfo.m_setcarcontrol.m_antitheft != 0){
				m_BcmInfo.m_setcarcontrol.m_antitheft = 0;
			}

			//3帧数据发送完毕后车门状态置为无效
			for(int i=0;i<m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus.length;i++){
				if(m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[i]!=BCM_DOOR_CONTROL.DOOR_INVALIDE){
					m_BcmInfo.m_setcarcontrol.m_cardoor_lockstatus[i] = BCM_DOOR_CONTROL.DOOR_INVALIDE;
				}
			}

			for(int i=0;i<m_BcmInfo.m_setcarcontrol.m_cardoor_status.length;i++){
				if(m_BcmInfo.m_setcarcontrol.m_cardoor_status[i]!=BCM_DOOR_CONTROL.DOOR_INVALIDE){
					m_BcmInfo.m_setcarcontrol.m_cardoor_status[i] = BCM_DOOR_CONTROL.DOOR_INVALIDE;
				}
			}

			//数据全清
			for(int i=0;i<m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].length;i++){
				if(m_BcmInfo.m_setcarcontrol.OneKeyOpenWindow != true){
					if(i==1 || i==2){
						continue;
					}
				}
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][i] = 0;
			}
		}

		//定义几个立刻发送执行的接口
		void Port_BcmImmediatelySend(){
			for(int i=0;i<m_protbcm.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protbcm.m_appdata.Send[i][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mEnable = true;
					m_protbcm.m_appdata.Send[i][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate =0;
					m_protbcm.m_appdata.Send[i][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mPGState =DriverPgStatus.PG_TX_REQ;
				}
			}
		}
	}
}

class BcmConst_PARA{
	short pf;
	short ps;
	short sa;
	short prior;
	short page;
	J1939_FrameType framtype;
	DriverTransType transtype;
	int  rate;
	BcmConst_PARA(	int i,
					  int j,
					  int m,
					  int k,
					  int l,
					  J1939_FrameType canFramtypeExt,
					  DriverTransType ttCycle,
					  int  rate){
		this.pf = (short) i;
		this.ps = (short) j;
		this.sa = (short) m;
		this.prior = (short) k;
		this.page = (short) l;
		this.framtype = canFramtypeExt;
		this.transtype = ttCycle;
		this.rate = rate;
	}
}

enum BCM_RX_PNG{//接收参数组
	BCM_RX_BASEINFO,
	BCM_RX_STATUS,
	BCM_RX_RATEDINFO,
	BCM_RX_VINCODE,
	BCM_RX_PNG_TOTAL
}

enum BCM_TX_PNG{//发送参数组
	BCM_TX_BCMCONTROL,
	BCM_TX_PNG_TOTAL;
}