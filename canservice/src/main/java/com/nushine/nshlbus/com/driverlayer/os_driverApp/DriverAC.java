/*ver:0.0.1
 *auth:hpp
 *data:15.4.23
 *bref:AC应用解析及控制车身信息
 *Revision record:0.0.1 初版实现《康迪K12汽车空调通讯规约V1.0》
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

public class DriverAC {
	public boolean ACDeBug=false;	 //AC调试开关
	public boolean ACCommStatus=true;//false表示超时
	/*AC_Change变量记录AC控制指令变化类型，每个bit代表一个类型
	 * bit0:AC;bit1:ptc;bit2:内外循环；bit3:出风模式；bit4:温度；bit5:风量；bit6-bit7:保留
	 */
	private int AC_Change=0;
	private boolean AC_UserControl=false;	//true用户操作事件发生,false用户操作事件解除
	public AC_Info m_ACInfo = new AC_Info();
	private DriverACTask m_ACtcb; //AC控制块

	public DriverAC(Global_Cfg cfg){
		m_ACtcb = new DriverACTask(cfg.m_stack);
	}

	public enum AC_POWER_CONTROL{
		POWER_INVALIDE,
		POWER_ON,
		POWER_OFF,
		POWER_TOTAL
	}

	public enum AC_MODE_CONTROL{
		MODE_INVALIDE,
		MODE_REFRIGERATE,
		MODE_HEAT,
		MODE_REFRIGERATEANDHEAT,
		MODE_TOTAL
	}

	public enum AC_AIRCYCLEMODE_CONTROL{
		AIRCYCLEMODE_INVALIDE,
		AIRCYCLEMODE_INSIDE,
		AIRCYCLEMODE_OUTSIDE,
		AIRCYCLEMODE_TOTAL
	}

	public enum AC_AIRMODE_CONTROL{
		AIRMODE_INVALIDE,
		AIRMODE_FACE,
		AIRMODE_FOOT,
		AIRMODE_FACEANDFOOT,
		AIRMODE_DEFROST,
		AIRMODE_FOOTANDDEFROST,
		AIRMODE_TOTAL
	}

	public enum AC_STATUS{
		AIRSTATUS_INVALIDE,
		AIRSTATUS_NORMAL,
		AIRSTATUS_ERR,
		AIRSTATUS_TOTAL
	}

	public class AC_Info{//汽车空调基本信息
		//基本信息
		public byte m_vender=(byte) 0xff;
		public short m_softver=0;
		public short m_hardver=0;

		public short m_easvoltrtd = 0;//额定电压
		public short m_refrigeratepowrtd = 0;//额定制冷功率
		public short m_heatpowrtd = 0;//额定制热功率
		public byte[] m_easvincode = new byte[6];

		public int   m_iserror=0;
		public AC_STATUS m_ACstatus=AC_STATUS.AIRSTATUS_NORMAL;
		public AC_STATUS m_bakACstatus=AC_STATUS.AIRSTATUS_NORMAL;

		public AC_CONTROL m_setACcontrol=new AC_CONTROL();
		public AC_CONTROL m_getACcontrol=new AC_CONTROL();
		public AC_CONTROL m_BckgetACcontrol=new AC_CONTROL();
		public boolean AC_Change_Flag=false;	//空调状态发生变化标志
	}

	public class AC_CONTROL{
		public AC_POWER_CONTROL m_ACopen=AC_POWER_CONTROL.POWER_INVALIDE;
		public AC_MODE_CONTROL m_ACmode=AC_MODE_CONTROL.MODE_INVALIDE;
		public AC_AIRCYCLEMODE_CONTROL m_aircyclemode=AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INVALIDE;
		public AC_AIRMODE_CONTROL m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FACE;
		public short m_airtemp=0;
		public short m_airspeed=9;
		public short m_innairtemp=0;

		//压缩机故障;PTC故障;硬件故障;通讯故障
		public AC_STATUS[] m_err = new AC_STATUS[4];


	}

	public void AC_CheckChange(){
		if((m_ACInfo.m_BckgetACcontrol.m_ACmode!= m_ACInfo.m_getACcontrol.m_ACmode||
				m_ACInfo.m_BckgetACcontrol.m_ACopen!= m_ACInfo.m_getACcontrol.m_ACopen||
				m_ACInfo.m_BckgetACcontrol.m_aircyclemode!= m_ACInfo.m_getACcontrol.m_aircyclemode||
				m_ACInfo.m_BckgetACcontrol.m_airmode!= m_ACInfo.m_getACcontrol.m_airmode||
				m_ACInfo.m_BckgetACcontrol.m_airspeed!= m_ACInfo.m_getACcontrol.m_airspeed||
				m_ACInfo.m_BckgetACcontrol.m_airtemp!=m_ACInfo.m_getACcontrol.m_airtemp ||
//		   m_ACInfo.m_BckgetACcontrol.m_innairtemp!=m_ACInfo.m_getACcontrol.m_innairtemp||
				m_ACInfo.m_bakACstatus!=m_ACInfo.m_ACstatus)&&!m_ACInfo.AC_Change_Flag&&!AC_UserControl){
			m_ACInfo.m_BckgetACcontrol.m_ACmode = m_ACInfo.m_getACcontrol.m_ACmode;
			m_ACInfo.m_BckgetACcontrol.m_ACopen = m_ACInfo.m_getACcontrol.m_ACopen;
			m_ACInfo.m_BckgetACcontrol.m_aircyclemode = m_ACInfo.m_getACcontrol.m_aircyclemode;
			m_ACInfo.m_BckgetACcontrol.m_airmode = m_ACInfo.m_getACcontrol.m_airmode;
			m_ACInfo.m_BckgetACcontrol.m_airspeed = m_ACInfo.m_getACcontrol.m_airspeed;
			m_ACInfo.m_BckgetACcontrol.m_airtemp =m_ACInfo.m_getACcontrol.m_airtemp;
			m_ACInfo.m_BckgetACcontrol.m_innairtemp=m_ACInfo.m_getACcontrol.m_innairtemp;
			m_ACInfo.m_bakACstatus=m_ACInfo.m_ACstatus;
			m_ACInfo.AC_Change_Flag=true;
		}
	}

	public void Change_ACControlSendCycle(int changtype){
		AC_Change = changtype;
		m_ACtcb.Port_ACImmediatelySend();
	}

	//BCU协议栈
	class Driver_AC_DataBuf{
		//协议栈
		final ACConst_PARA[] Recv_PngData = {
				new ACConst_PARA(0xA2,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//AC基本信息
				new ACConst_PARA(0xA2,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,3000),//AC状态信息
				new ACConst_PARA(0xA2,0x08,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//AC额定
				new ACConst_PARA(0xA2,0xFB,0x02,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,5000),//设备唯一码
		};
		final ACConst_PARA[] Send_PngData = {
				new ACConst_PARA(0xF0,0x50,0xA2,4,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//AC控制
		};
		//收发原始数据缓存定义
		final  byte CONST_AC_DATALEN=8;//数据长度
		final  short MAX_RECVBUFF_SIZE=512;//每个png可以存储的报文数据量
		int[] AC_RecvGetAddr = new int[AC_RX_PNG.AC_RX_PNG_TOTAL.ordinal()];//接收缓存读指针
		int[] AC_RecvSetAddr = new int[AC_RX_PNG.AC_RX_PNG_TOTAL.ordinal()];//接收缓存写指针
		short[][][] m_ACDataRcev_Buff = new short[AC_RX_PNG.AC_RX_PNG_TOTAL.ordinal()][MAX_RECVBUFF_SIZE][CONST_AC_DATALEN];
		short[][] m_ACDataSend_Buff = new short[AC_TX_PNG.AC_TX_PNG_TOTAL.ordinal()][CONST_AC_DATALEN];//每帧数据的发送缓存只需要分配一个空间

		Driver_AC_DataBuf(){
			//数据收发缓存初始化，收发指针初始化
			for(int i=0;i<AC_RecvGetAddr.length;i++){
				AC_RecvGetAddr[i]=0;
			}
			for(int i=0;i<AC_RecvSetAddr.length;i++){
				AC_RecvSetAddr[i]=0;
			}
			for(int i=0;i<m_ACDataRcev_Buff.length;i++)
				for(int j=0;j<m_ACDataRcev_Buff[i].length;j++)
					for(int k=0;k<m_ACDataRcev_Buff[i][j].length;k++){
						m_ACDataRcev_Buff[i][j][k]=0;
					}
			for(int i=0;i<m_ACDataSend_Buff.length;i++)
				for(int j=0;j<m_ACDataSend_Buff[i].length;j++){
					m_ACDataSend_Buff[i][j] = 0;
				}
		}

		//从接收缓存中读取一个数据
		public  short[]  AC_GetRecvBuffer(int png){
			short[] data;
			if(AC_RecvGetAddr[png]==AC_RecvSetAddr[png]){
				return null;
			}else{
				synchronized(this){
					data = m_ACDataRcev_Buff[png][AC_RecvGetAddr[png]].clone();
					AC_RecvGetAddr[png] = (short) ((AC_RecvGetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return data;
		}
		//接收数据写入缓存
		public  boolean Can_SetRecvBuffer(short[] data,int png)
		{
			if(((AC_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE)==AC_RecvGetAddr[png]){
				return false;
			}else{
				synchronized(this){
					m_ACDataRcev_Buff[png][AC_RecvSetAddr[png]] = null;
					m_ACDataRcev_Buff[png][AC_RecvSetAddr[png]] = data.clone();
					AC_RecvSetAddr[png] = (short) ((AC_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return true;
		}
	}

	//协议解析类
	class DriverACTask extends DriverCallPro {
		final short ACApp_Addr_Start = 0xFF;	//BCU默认地址
		final short ACApp_Addr_Code = 0x02;	//code接收默认
		final short AC_Num = 1;				//BCU默认数量
		Logger log = Logger.getLogger(DriverACTask.class);
		final Driver_AC_DataBuf m_ACapp = new Driver_AC_DataBuf();//协议栈定义
		DriverProtocolStack m_protAC; //定义一个应用堆栈

		DriverACTask(cfgPortStack stack) {
			//协议栈初始化
			InitAppData();
			m_protAC.m_callpro = this;
			if(stack.m_port.crateProtApp(m_protAC)){
				log.debug("DriverACTask is create success!");
			}else{
				log.debug("DriverACTask is create failed!");
			}
		}

		void InitAppData(){
			m_protAC = new DriverProtocolStack(AC_Num,(short) m_ACapp.Send_PngData.length,(short) m_ACapp.Recv_PngData.length);
			m_protAC.m_appdata.mHardType = DriverHardType.DH_CAN;//can设备
			for(int i=0;i<m_protAC.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protAC.m_appdata.TotalRecvPara;j++){
					m_protAC.m_appdata.Recv[i][j].mDataMaxLen = m_ACapp.CONST_AC_DATALEN;
					m_protAC.m_appdata.Recv[i][j].mDataLen = m_ACapp.CONST_AC_DATALEN;
					m_protAC.m_appdata.Recv[i][j].mSource = (byte) m_ACapp.Recv_PngData[j].sa;
					m_protAC.m_appdata.Recv[i][j].mTransRate = (int) m_ACapp.Recv_PngData[j].rate;
					m_protAC.m_appdata.Recv[i][j].mTimer = 0;
					m_protAC.m_appdata.Recv[i][j].mEnable = true;
					m_protAC.m_appdata.Recv[i][j].mPGState = DriverPgStatus.PG_RX_FREE;
					m_protAC.m_appdata.Recv[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protAC.m_appdata.Recv[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
			for(int i=0;i<m_protAC.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protAC.m_appdata.TotalSendPara;j++){
					m_protAC.m_appdata.Send[i][j].mDataMaxLen = m_ACapp.CONST_AC_DATALEN;
					m_protAC.m_appdata.Send[i][j].mDataLen = m_ACapp.CONST_AC_DATALEN;
					m_protAC.m_appdata.Send[i][j].mSource = (short) m_ACapp.Send_PngData[j].sa;
					m_protAC.m_appdata.Send[i][j].mTransRate = (int) m_ACapp.Send_PngData[j].rate;
					m_protAC.m_appdata.Send[i][j].mTimer = 0;
					m_protAC.m_appdata.Send[i][j].mEnable = true;
					m_protAC.m_appdata.Send[i][j].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protAC.m_appdata.Send[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protAC.m_appdata.Send[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
		}
		//接收回调接口
		public boolean RxSourceData(J1939FrameFormat j1939){
			int m_rxnum = AC_RX_PNG.AC_RX_PNG_TOTAL.ordinal();
			//判断报文是否属于BCU
			for(int i = 0; i <m_rxnum;i++){
				if(j1939.PS == m_ACapp.Recv_PngData[i].ps
						&& j1939.PF == m_ACapp.Recv_PngData[i].pf
						&& j1939.FramType == m_ACapp.Recv_PngData[i].framtype){
					if(j1939.Addr >= m_ACapp.Recv_PngData[i].sa){
						short addr = (short) (j1939.Addr - m_ACapp.Recv_PngData[i].sa);
						if(addr >= AC_Num || addr <0){
							return false;
						}
						if(m_protAC.m_appdata.Recv[addr][i].mEnable){
							if(j1939.DtaLen <= m_protAC.m_appdata.Recv[addr][i].mDataMaxLen){
								m_ACapp.Can_SetRecvBuffer(j1939.pDta,(byte)i);
								m_protAC.m_appdata.Recv[addr][i].mDataLen = (byte) j1939.DtaLen;
								m_protAC.m_appdata.Recv[addr][i].mPGState = DriverPgStatus.PG_RX_FULL;
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
			if(wpara == AC_TX_PNG.AC_TX_ACCONTROL.ordinal()){
				Tx_ACControl(lpara);
			}
		}

		@Override
		public boolean rxFrame(int lpara, int wpara) {
			// TODO Auto-generated method stub
			short[] m_data = new short[8];
			m_data = m_ACapp.AC_GetRecvBuffer(wpara);
			if(m_data==null || ACDeBug){	//调试模式下不接受CAN总线数据
				return false;
			}

			if(wpara == AC_RX_PNG.AC_RX_BASEINFO.ordinal()){
				Rx_ACBaseInfo(m_data,lpara);
			}else if(wpara == AC_RX_PNG.AC_RX_STATUS.ordinal()){
				Rx_ACStatus(m_data,lpara);
				timeoutnum = 0;
			}else if(wpara == AC_RX_PNG.AC_RX_RATEDINFO.ordinal()){
				Rx_ACRatedInfo(m_data,lpara);
			}else if(wpara == AC_RX_PNG.AC_RX_VINCODE.ordinal()){
				Rx_ACVincode(m_data,lpara);
			}else{
				return false;
			}
			return true;
		}

		@Override
		public void rxTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub
			if(wpara == AC_RX_PNG.AC_RX_STATUS.ordinal()){
				Rx_ACTimeOut(lpara);

			}
		}

		@Override
		public void txTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub

		}

		@Override
		public J1939FrameFormat SendFrame(int lpara, int wpara) {//CAN数据发送接口
			// TODO Auto-generated method stub
			if(wpara>AC_TX_PNG.AC_TX_PNG_TOTAL.ordinal()){
				return null;
			}

			J1939FrameFormat m_send = new J1939FrameFormat();
			m_send.FramType = m_ACapp.Send_PngData[wpara].framtype;//DriverBcuTask.m_protbcu.J1939_FrameType;
			m_send.PF = m_ACapp.Send_PngData[wpara].pf;
			m_send.PS = m_ACapp.Send_PngData[wpara].ps;
			m_send.Addr = m_protAC.m_appdata.Send[lpara][wpara].mSource;
			m_send.Prior = m_ACapp.Send_PngData[wpara].prior;
			m_send.Page = m_ACapp.Send_PngData[wpara].page;
			m_send.DtaLen = m_ACapp.CONST_AC_DATALEN;
			m_send.pDta = m_ACapp.m_ACDataSend_Buff[wpara].clone();
			for(int i=0;i<m_ACapp.m_ACDataSend_Buff[wpara].length;i++){
				m_ACapp.m_ACDataSend_Buff[wpara][i]=0;
			}
			return m_send;
		}

		/****************数据接收处理****************/
		private void Rx_ACBaseInfo(short[] pdta,int Addr){
			m_ACInfo.m_vender = (byte) pdta[0];
			m_ACInfo.m_hardver = (short) ((pdta[2]&0xff)<<8|(pdta[3]&0xff));
			m_ACInfo.m_softver = (short) ((pdta[4]&0xff)<<8|(pdta[5]&0xff));
		}
		private void Rx_ACRatedInfo(short[] pdta,int Addr){
			m_ACInfo.m_easvoltrtd = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_ACInfo.m_refrigeratepowrtd = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
			m_ACInfo.m_heatpowrtd = (short) ((pdta[5]&0xff)<<8|(pdta[4]&0xff));
		}
		private void Rx_ACVincode(short[] pdta,int Addr){
			for(int i=0;i<m_ACInfo.m_easvincode.length;i++){
				m_ACInfo.m_easvincode[i] = (byte) pdta[i+1];
			}
		}
		//BCU工作工状态信息
		private void Rx_ACStatus(short[] pdta,int Addr){
			if(pdta[0]==0x55){
				m_ACInfo.m_getACcontrol.m_ACopen = AC_POWER_CONTROL.POWER_ON;
			}
			else if(pdta[0]==0xaa){
				m_ACInfo.m_getACcontrol.m_ACopen = AC_POWER_CONTROL.POWER_OFF;
			}
			else{
				m_ACInfo.m_getACcontrol.m_ACopen = AC_POWER_CONTROL.POWER_INVALIDE;
			}

			if((pdta[1]&0x05)==0x05){
				m_ACInfo.m_getACcontrol.m_ACmode = AC_MODE_CONTROL.MODE_REFRIGERATEANDHEAT;
			}else if((pdta[1]&0x04)==0x04){
				m_ACInfo.m_getACcontrol.m_ACmode = AC_MODE_CONTROL.MODE_HEAT;
			}
			else if((pdta[1]&0x01)==0x01){
				m_ACInfo.m_getACcontrol.m_ACmode = AC_MODE_CONTROL.MODE_REFRIGERATE;
			}else{
				m_ACInfo.m_getACcontrol.m_ACmode = AC_MODE_CONTROL.MODE_INVALIDE;
			}

			if((pdta[1]&0x10)==0x00){
				m_ACInfo.m_getACcontrol.m_aircyclemode = AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INSIDE;
			}
			else if((pdta[1]&0x10)==0x10){
				m_ACInfo.m_getACcontrol.m_aircyclemode = AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_OUTSIDE;
			}
			else{
				m_ACInfo.m_getACcontrol.m_aircyclemode = AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INVALIDE;
			}

			if(pdta[2]==1){
				m_ACInfo.m_getACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FACE;
			}
			else if(pdta[2]==2){
				m_ACInfo.m_getACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FACEANDFOOT;
			}
			else if(pdta[2]==3){
				m_ACInfo.m_getACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FOOT;
			}
			else if(pdta[2]==4){
				m_ACInfo.m_getACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_FOOTANDDEFROST;
			}
			else if(pdta[2]==5){
				m_ACInfo.m_getACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_DEFROST;
			}
			else{
				m_ACInfo.m_getACcontrol.m_airmode=AC_AIRMODE_CONTROL.AIRMODE_INVALIDE;
			}

			if(pdta[5]==0){
				m_ACInfo.m_ACstatus = AC_STATUS.AIRSTATUS_NORMAL;
			}else{
				m_ACInfo.m_ACstatus = AC_STATUS.AIRSTATUS_ERR;
			}

			m_ACInfo.m_getACcontrol.m_airtemp = pdta[3];
			m_ACInfo.m_getACcontrol.m_airspeed = pdta[4];
			if((pdta[6]&0x80)==0x80){
				m_ACInfo.m_getACcontrol.m_innairtemp = (short)((pdta[6]&0x7f)*(-1));
			}else{
				m_ACInfo.m_getACcontrol.m_innairtemp = (short)(pdta[6]);
			}
			AC_CheckChange();
		}

		//空调控制接口
		private void Tx_ACControl(int Addr){
			short tempACmode=0;
			short tempACpower=0;
			short tempAIRmode=0;

			if(m_ACInfo.m_setACcontrol.m_ACopen==AC_POWER_CONTROL.POWER_ON){	//打开
				tempACpower = 0x55;
			}
			else if(m_ACInfo.m_setACcontrol.m_ACopen==AC_POWER_CONTROL.POWER_OFF){ //关闭
				tempACpower = 0xaa;
			}

			if(m_ACInfo.m_setACcontrol.m_ACmode==AC_MODE_CONTROL.MODE_REFRIGERATE){
				tempACmode |= 0x01;
			}
			else if(m_ACInfo.m_setACcontrol.m_ACmode==AC_MODE_CONTROL.MODE_HEAT){
				tempACmode |= 0x04;
			}
			else if(m_ACInfo.m_setACcontrol.m_ACmode==AC_MODE_CONTROL.MODE_REFRIGERATEANDHEAT){
				tempACmode = 0x00;
			}

			if(m_ACInfo.m_setACcontrol.m_aircyclemode==AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_INSIDE){
				tempACmode |= 0x00;
			}
			else if(m_ACInfo.m_setACcontrol.m_aircyclemode==AC_AIRCYCLEMODE_CONTROL.AIRCYCLEMODE_OUTSIDE){
				tempACmode |= 0x10;
			}

			if(m_ACInfo.m_setACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FACE){
				tempAIRmode = 0x01;
			}
			else if(m_ACInfo.m_setACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FACEANDFOOT){
				tempAIRmode = 0x02;
			}
			else if(m_ACInfo.m_setACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FOOT){
				tempAIRmode = 0x03;
			}
			else if(m_ACInfo.m_setACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_FOOTANDDEFROST){
				tempAIRmode = 0x04;
			}
			else if(m_ACInfo.m_setACcontrol.m_airmode==AC_AIRMODE_CONTROL.AIRMODE_DEFROST){
				tempAIRmode = 0x05;
			}
			else{
				tempAIRmode = 0x00;
			}

			m_ACapp.m_ACDataSend_Buff[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()][0] = tempACpower;
			m_ACapp.m_ACDataSend_Buff[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()][1] = tempACmode;
			m_ACapp.m_ACDataSend_Buff[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()][2] = tempAIRmode;
			m_ACapp.m_ACDataSend_Buff[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()][3] = m_ACInfo.m_setACcontrol.m_airtemp;
			m_ACapp.m_ACDataSend_Buff[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()][4] = m_ACInfo.m_setACcontrol.m_airspeed;
			m_ACapp.m_ACDataSend_Buff[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()][5] = (short) ((short) AC_Change&0x00ff);

			synchronized(this){
				int rate = m_protAC.m_appdata.Send[Addr][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mTransRate;
				if(rate==0){
					m_protAC.m_appdata.Send[Addr][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mTransRate=1;
				}
				else if(rate==1){
					m_protAC.m_appdata.Send[Addr][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protAC.m_appdata.Send[Addr][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mTransRate=21;
				}else{	//关闭数据发送
					AC_Change = 0;
					m_protAC.m_appdata.Send[Addr][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protAC.m_appdata.Send[Addr][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mTransRate = (int) m_ACapp.Send_PngData[AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].rate;
					AC_UserControl = false;
				}
			}
		}

		byte timeoutnum=0;
		private void Rx_ACTimeOut(int Addr){
			final byte AC_TIMEOUT_MAX = 3;	//bcu通讯3次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>AC_TIMEOUT_MAX){
				timeoutnum = 0;
				m_ACInfo.m_ACstatus = AC_STATUS.AIRSTATUS_ERR;
				AC_CheckChange();
				//	ACCommStatus = false;
				log.warn("AC timeout!");
			}
		}

		//定义几个立刻发送执行的接口
		void Port_ACImmediatelySend(){
			for(int i=0;i<m_protAC.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protAC.m_appdata.Send[i][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
					m_protAC.m_appdata.Send[i][AC_TX_PNG.AC_TX_ACCONTROL.ordinal()].mTransRate =0;
					AC_UserControl = true;
				}
			}
		}
	}
}

class ACConst_PARA{
	short pf;
	short ps;
	short sa;
	short prior;
	short page;
	J1939_FrameType framtype;
	DriverTransType transtype;
	int  rate;
	ACConst_PARA(	int i,
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

enum AC_RX_PNG{//接收参数组
	AC_RX_BASEINFO,
	AC_RX_STATUS,
	AC_RX_RATEDINFO,
	AC_RX_VINCODE,
	AC_RX_PNG_TOTAL
}

enum AC_TX_PNG{//发送参数组
	AC_TX_ACCONTROL,
	AC_TX_PNG_TOTAL;
}