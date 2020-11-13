/*ver:0.0.1
 *auth:xuxb
 *data:16.5.9
 *bref:MFL应用解析及多功能方向盘信息
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

public class DriverMFL {
	public boolean MFLDeBug=false;	 //Mfl调试开关
	public  MFL_Info m_MFLinfo=new MFL_Info();//MFL信息
	public  boolean MFLCommStatus=true;//false表示超时
	private Driver_MFL_DataBuf m_MFLPortStack = new Driver_MFL_DataBuf();
	private DriverMFLTask     m_MFLtcb; //MFL控制块

	public DriverMFL(Global_Cfg cfg){
		m_MFLtcb = new DriverMFLTask(cfg.m_stack);
//		Change_MFLControlSendCycle();
	}

	public void Change_MFLControlSendCycle(){
		m_MFLtcb.Port_MFLImmediatelySend();
		m_MFLtcb.Tx_MFLControl(0);
		m_MFLtcb.Port_CarVinCodeImmediatelySend();
		m_MFLtcb.Tx_MFLVinCode(0);
		m_MFLtcb.Port_BatConfigImmediatelySend();
		m_MFLtcb.Tx_BatConfig(0);
	}

	public void Change_MFLVinCodeSend(){
		m_MFLtcb.Port_CarVinCodeImmediatelySend();
		m_MFLtcb.Tx_MFLVinCode(0);
		m_MFLtcb.Port_BatConfigImmediatelySend();
		m_MFLtcb.Tx_BatConfig(0);
	}

	public void Change_MFLCarVinCodeSend(){
		m_MFLtcb.Port_CarVinCodeImmediatelySend();
		m_MFLtcb.Tx_MFLVinCode(0);
	}

	public enum MFL_ASSISTANCE_CONTROL{//多功能方向盘
		FORCE_INVALIDE,
		FORCE_PRESS,
		FORCE_CALL,
		FORCE_HANGUP,
		FORCE_VOLUMEREDUCE,
		FORCE_VOLUMEADD,
		FORCE_MUSICNEXT,
		FORCE_MUSICPREV,
		FORCE_MODE
	}

	public enum MFL_STATUS{
		MFLSTATUS_INVALIDE,
		MFLSTATUS_NORMAL,
		MFLSTATUS_ERR,
		MFLSTATUS_TOTAL
	}

	public class MFL_Info{//汽车空调基本信息
		//基本信息
		public byte m_vender=(byte) 0xff;
		public short m_softver=0;
		public short m_hardver=0;

		public short m_MFLVolt_Rtd = 0;
		public short m_MFLCur_Rtd = 0;
		public byte[] m_mflvincode = new byte[6];

		public MFL_STATUS m_MFLstatus=MFL_STATUS.MFLSTATUS_NORMAL;
		public MFL_STATUS m_Canstatus=MFL_STATUS.MFLSTATUS_NORMAL;
		public MFL_CONTROL m_setMFLcontrol=new MFL_CONTROL();
		public MFL_CONTROL m_getMFLcontrol=new MFL_CONTROL();
	}

	public class MFL_CONTROL{
		public MFL_ASSISTANCE_CONTROL[] m_mfl_status= new MFL_ASSISTANCE_CONTROL[8];
		MFL_CONTROL(){
			for(int i=0;i<m_mfl_status.length;i++){
				m_mfl_status[i] = MFL_ASSISTANCE_CONTROL.FORCE_INVALIDE;
			}
		}
	}

	//BCU协议栈
	class Driver_MFL_DataBuf{
		//协议栈
		final MFLConst_PARA[] Recv_PngData = {
				new MFLConst_PARA(0x0D,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,5000),//设备信息MFL_RX_BASEINFO
				new MFLConst_PARA(0x0D,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//MFL总状态MFL_RX_STATUS
				new MFLConst_PARA(0x0D,0x08,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//额定电压、电流
				new MFLConst_PARA(0x0D,0xFB,0x02,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,5000),//设备唯一码
		};
		final MFLConst_PARA[] Send_PngData = {
				new MFLConst_PARA(0x02,0xFC,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,15000),
				new MFLConst_PARA(0x02,0xFC,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,3000),
				new MFLConst_PARA(0x02,0xFC,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,2000),
		};
		//收发原始数据缓存定义
		final  byte CONST_MFL_DATALEN=8;//数据长度
		final  short MAX_RECVBUFF_SIZE=512;//每个png可以存储的报文数据量
		int[] MFL_RecvGetAddr = new int[MFL_RX_PNG.MFL_RX_PNG_TOTAL.ordinal()];//接收缓存读指针
		int[] MFL_RecvSetAddr = new int[MFL_RX_PNG.MFL_RX_PNG_TOTAL.ordinal()];//接收缓存写指针
		short[][][] m_MFLDataRcev_Buff = new short[MFL_RX_PNG.MFL_RX_PNG_TOTAL.ordinal()][MAX_RECVBUFF_SIZE][CONST_MFL_DATALEN];
		short[][] m_MFLDataSend_Buff = new short[MFL_TX_PNG.MFL_TX_PNG_TOTAL.ordinal()][CONST_MFL_DATALEN];//每帧数据的发送缓存只需要分配一个空间

		Driver_MFL_DataBuf(){
			//数据收发缓存初始化，收发指针初始化
			for(int i=0;i<MFL_RecvGetAddr.length;i++){
				MFL_RecvGetAddr[i]=0;
			}
			for(int i=0;i<MFL_RecvSetAddr.length;i++){
				MFL_RecvSetAddr[i]=0;
			}
			for(int i=0;i<m_MFLDataRcev_Buff.length;i++)
				for(int j=0;j<m_MFLDataRcev_Buff[i].length;j++)
					for(int k=0;k<m_MFLDataRcev_Buff[i][j].length;k++){
						m_MFLDataRcev_Buff[i][j][k]=0;
					}
			for(int i=0;i<m_MFLDataSend_Buff.length;i++)
				for(int j=0;j<m_MFLDataSend_Buff[i].length;j++){
					m_MFLDataSend_Buff[i][j] = 0;
				}
		}

		//从接收缓存中读取一个数据
		public  short[]  MFL_GetRecvBuffer(int png){
			short[] data;
			if(MFL_RecvGetAddr[png]==MFL_RecvSetAddr[png]){
				return null;
			}else{
				synchronized(this){
					data = m_MFLDataRcev_Buff[png][MFL_RecvGetAddr[png]].clone();
					MFL_RecvGetAddr[png] = (short) ((MFL_RecvGetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return data;
		}
		//接收数据写入缓存
		public  boolean Can_SetRecvBuffer(short[] data,int png)
		{
			if(((MFL_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE)==MFL_RecvGetAddr[png]){
				return false;
			}else{
				synchronized(this){
					m_MFLDataRcev_Buff[png][MFL_RecvSetAddr[png]] = null;
					m_MFLDataRcev_Buff[png][MFL_RecvSetAddr[png]] = data.clone();
					MFL_RecvSetAddr[png] = (short) ((MFL_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return true;
		}
	}

	//协议解析类
	class DriverMFLTask extends DriverCallPro {
		final short MFLApp_Addr_Start = 0xAA;	//BCU默认地址
		final short MFLApp_Addr_Code = 0x02;	//code接收默认
		final short MFL_Num = 1;				//BCU默认数量
		Logger log = Logger.getLogger(DriverMFLTask.class);
		final Driver_MFL_DataBuf m_MFLapp = new Driver_MFL_DataBuf();//协议栈定义
		DriverProtocolStack m_protMFL; //定义一个应用堆栈

		DriverMFLTask(cfgPortStack stack) {
			//协议栈初始化
			InitAppData();
			m_protMFL.m_callpro = this;
			if(stack.m_port.crateProtApp(m_protMFL)){
				log.debug("DriverMFLTask is create success!");
			}else{
				log.debug("DriverMFLTask is create failed!");
			}
		}

		void InitAppData(){
			m_protMFL = new DriverProtocolStack(MFL_Num,(short) m_MFLPortStack.Send_PngData.length,(short) m_MFLPortStack.Recv_PngData.length);
			m_protMFL.m_appdata.mHardType = DriverHardType.DH_CAN;//can设备
			for(int i=0;i<m_protMFL.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protMFL.m_appdata.TotalRecvPara;j++){
					m_protMFL.m_appdata.Recv[i][j].mDataMaxLen = m_MFLPortStack.CONST_MFL_DATALEN;
					m_protMFL.m_appdata.Recv[i][j].mDataLen = m_MFLPortStack.CONST_MFL_DATALEN;
					m_protMFL.m_appdata.Recv[i][j].mSource = (byte) m_MFLPortStack.Recv_PngData[j].sa;
					m_protMFL.m_appdata.Recv[i][j].mTransRate = (int) m_MFLPortStack.Recv_PngData[j].rate;
					m_protMFL.m_appdata.Recv[i][j].mTimer = 0;
					m_protMFL.m_appdata.Recv[i][j].mEnable = true;
					m_protMFL.m_appdata.Recv[i][j].mPGState = DriverPgStatus.PG_RX_FREE;
					m_protMFL.m_appdata.Recv[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protMFL.m_appdata.Recv[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
			for(int i=0;i<m_protMFL.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protMFL.m_appdata.TotalSendPara;j++){
					m_protMFL.m_appdata.Send[i][j].mDataMaxLen = m_MFLPortStack.CONST_MFL_DATALEN;
					m_protMFL.m_appdata.Send[i][j].mDataLen = m_MFLPortStack.CONST_MFL_DATALEN;
					m_protMFL.m_appdata.Send[i][j].mSource = (short) m_MFLPortStack.Send_PngData[j].sa;
					m_protMFL.m_appdata.Send[i][j].mTransRate = (int) m_MFLPortStack.Send_PngData[j].rate;
					m_protMFL.m_appdata.Send[i][j].mTimer = 0;
					m_protMFL.m_appdata.Send[i][j].mEnable = true;
					m_protMFL.m_appdata.Send[i][j].mPGState = DriverPgStatus.PG_TX_TX;
					m_protMFL.m_appdata.Send[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protMFL.m_appdata.Send[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
		}
		//接收回调接口
		public boolean RxSourceData(J1939FrameFormat j1939){
			int m_rxnum = MFL_RX_PNG.MFL_RX_PNG_TOTAL.ordinal();
			//判断报文是否属于BCU
			for(int i = 0; i <m_rxnum;i++){
				if(j1939.PS == m_MFLPortStack.Recv_PngData[i].ps
						&& j1939.PF == m_MFLPortStack.Recv_PngData[i].pf
						&& j1939.FramType == m_MFLPortStack.Recv_PngData[i].framtype){
					if(j1939.Addr >= m_MFLPortStack.Recv_PngData[i].sa){
						short addr = (short) (j1939.Addr - m_MFLPortStack.Recv_PngData[i].sa);
						if(addr >= MFL_Num || addr <0){
							return false;
						}
						if(m_protMFL.m_appdata.Recv[addr][i].mEnable){
							if(j1939.DtaLen <= m_protMFL.m_appdata.Recv[addr][i].mDataMaxLen){
								m_MFLPortStack.Can_SetRecvBuffer(j1939.pDta,(byte)i);
								m_protMFL.m_appdata.Recv[addr][i].mDataLen = (byte) j1939.DtaLen;
								m_protMFL.m_appdata.Recv[addr][i].mPGState = DriverPgStatus.PG_RX_FULL;
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
			if(wpara == MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()){
				Tx_MFLControl(lpara);
			}else if(wpara == MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()){
				Tx_MFLVinCode(lpara);
			}else if(wpara == MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()){
				Tx_BatConfig(lpara);
			}
		}

		@Override
		public boolean rxFrame(int lpara, int wpara) {
			// TODO Auto-generated method stub
			short[] m_data = new short[8];
			m_data = m_MFLPortStack.MFL_GetRecvBuffer(wpara);
			if(m_data==null || MFLDeBug){	//调试模式下不接受CAN总线数据
				return false;
			}

			if(wpara == MFL_RX_PNG.MFL_RX_BASEINFO.ordinal()){
				Rx_MFLBaseInfo(m_data,lpara);
			}else if(wpara == MFL_RX_PNG.MFL_RX_RATEDINFO.ordinal()){
				Rx_MFLRatedInfo(m_data,lpara);
			}else if(wpara == MFL_RX_PNG.MFL_RX_STATUS.ordinal()){
				Rx_MFLStatus(m_data,lpara);
				timeoutnum = 0;
			}else if(wpara == MFL_RX_PNG.MFL_RX_VINCODE.ordinal()){
				Rx_MFLVincode(m_data,lpara);
			}else{
				return false;
			}
			return true;
		}

		@Override
		public void rxTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub
			if(wpara == MFL_RX_PNG.MFL_RX_BASEINFO.ordinal()){
				Rx_MFLTimeOut(lpara);

			}
		}

		@Override
		public void txTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub

		}

		@Override
		public J1939FrameFormat SendFrame(int lpara, int wpara) {//CAN数据发送接口
			// TODO Auto-generated method stub
			if(wpara>MFL_TX_PNG.MFL_TX_PNG_TOTAL.ordinal()){
				return null;
			}

			J1939FrameFormat m_send = new J1939FrameFormat();
			m_send.FramType = m_MFLPortStack.Send_PngData[wpara].framtype;//DriverBcuTask.m_protbcu.J1939_FrameType;
			m_send.PF = m_MFLPortStack.Send_PngData[wpara].pf;
			m_send.PS = m_MFLPortStack.Send_PngData[wpara].ps;
			m_send.Addr = m_protMFL.m_appdata.Send[lpara][wpara].mSource;
			m_send.Prior = m_MFLPortStack.Send_PngData[wpara].prior;
			m_send.Page = m_MFLPortStack.Send_PngData[wpara].page;
			m_send.DtaLen = m_MFLPortStack.CONST_MFL_DATALEN;
			m_send.pDta = m_MFLPortStack.m_MFLDataSend_Buff[wpara].clone();
			for(int i=0;i<m_MFLPortStack.m_MFLDataSend_Buff[wpara].length;i++){
				m_MFLPortStack.m_MFLDataSend_Buff[wpara][i]=0;
			}
			return m_send;
		}

		/****************数据接收处理****************/
		private void Rx_MFLBaseInfo(short[] pdta,int Addr){
			m_MFLinfo.m_vender = (byte) pdta[0];
			m_MFLinfo.m_hardver = (short) ((pdta[2]&0xff)<<8|(pdta[3]&0xff));
			m_MFLinfo.m_softver = (short) ((pdta[4]&0xff)<<8|(pdta[5]&0xff));
		}
		private void Rx_MFLRatedInfo(short[] pdta,int Addr){
			m_MFLinfo.m_MFLVolt_Rtd = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_MFLinfo.m_MFLCur_Rtd = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
		}
		private void Rx_MFLVincode(short[] pdta,int Addr){
			for(int i=0;i<m_MFLinfo.m_mflvincode.length;i++){
				m_MFLinfo.m_mflvincode[i] = (byte) pdta[i+1];
			}
		}
		//BCU工作工状态信息
		private void Rx_MFLStatus(short[] pdta,int Addr){
			MFLCommStatus = true;
			for(int i=0;i<m_MFLinfo.m_getMFLcontrol.m_mfl_status.length;i++){
				if(i<=3){
					if(((pdta[0]&0xff)>>i*2)%4==0x02){
						m_MFLinfo.m_getMFLcontrol.m_mfl_status[i] = MFL_ASSISTANCE_CONTROL.FORCE_PRESS;
					}else{
						m_MFLinfo.m_getMFLcontrol.m_mfl_status[i] = MFL_ASSISTANCE_CONTROL.FORCE_INVALIDE;
					}
				}else{
					if(((pdta[1]&0xff)>>(i-4)*2)%4==0x02){
						m_MFLinfo.m_getMFLcontrol.m_mfl_status[i] = MFL_ASSISTANCE_CONTROL.FORCE_PRESS;
					}else{
						m_MFLinfo.m_getMFLcontrol.m_mfl_status[i] = MFL_ASSISTANCE_CONTROL.FORCE_INVALIDE;
					}
				}
			}

			if((pdta[2]&0x01)==0x00){//0x01基准故障，0x00正常
				m_MFLinfo.m_MFLstatus = MFL_STATUS.MFLSTATUS_NORMAL;
			}
			else{
				m_MFLinfo.m_MFLstatus = MFL_STATUS.MFLSTATUS_ERR;
			}

			if(((pdta[2]>>1)&0x01)==0x00){//Can通信：0x01通信异常，0x00正常
				m_MFLinfo.m_Canstatus = MFL_STATUS.MFLSTATUS_NORMAL;
			}
			else{
				m_MFLinfo.m_Canstatus = MFL_STATUS.MFLSTATUS_ERR;
			}

		}

		private void Tx_MFLControl(int Addr){
			m_MFLPortStack.m_MFLDataSend_Buff[MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()][0] = 0xFF;
			m_MFLPortStack.m_MFLDataSend_Buff[MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()][1] = 0x01;

			synchronized(this){
				int rate = m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mTransRate;
				if(rate==0){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mTransRate=1;
				}else if(rate==1){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mTransRate=21;
				}else{	//关闭数据发送
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mTransRate = (int) m_MFLapp.Send_PngData[MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].rate;
				}
			}
		}

		private void Tx_MFLVinCode(int Addr){
			m_MFLPortStack.m_MFLDataSend_Buff[MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()][0] = 0xAA;
			m_MFLPortStack.m_MFLDataSend_Buff[MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()][1] = 0x02;

			synchronized(this){
				int rate = m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate;
				if(rate==0){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate=1;
				}else if(rate==1){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate=21;
				}else if(rate==3000){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate=4000;
				}else if(rate==4000){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate=5000;
				}else{	//关闭数据发送
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate = 15000;
				}
			}
		}

		private void Tx_BatConfig(int Addr){
			m_MFLPortStack.m_MFLDataSend_Buff[MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()][0] = 0xAA;
			m_MFLPortStack.m_MFLDataSend_Buff[MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()][1] = 0x05;

			synchronized(this){
				int rate = m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate;
				if(rate==0){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate=1;
				}else if(rate==1){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate=21;
				}else if(rate==2000){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate=3000;
				}else if(rate==3000){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate=4000;
				}else if(rate==4000){
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate=5000;
				}else{	//关闭数据发送
					m_protMFL.m_appdata.Send[Addr][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate = 15000;
				}
			}
		}

		byte timeoutnum=0;
		private void Rx_MFLTimeOut(int Addr){
			final byte MFL_TIMEOUT_MAX = 3;	//bcu通讯3次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>MFL_TIMEOUT_MAX){
				timeoutnum = 0;
				MFLCommStatus = false;
				log.warn("MFL timeout!");
			}
		}
		//定义几个立刻发送执行的接口
		void Port_MFLImmediatelySend(){
			for(int i=0;i<m_protMFL.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protMFL.m_appdata.Send[i][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mTransRate =0;
					m_protMFL.m_appdata.Send[i][MFL_TX_PNG.MFL_TX_MFLCONTROL.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
				}
			}
		}
		void Port_CarVinCodeImmediatelySend(){
			for(int i=0;i<m_protMFL.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protMFL.m_appdata.Send[i][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mTransRate =0;
					m_protMFL.m_appdata.Send[i][MFL_TX_PNG.MFL_TX_CARVINCODE.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
				}
			}
		}
		void Port_BatConfigImmediatelySend(){
			for(int i=0;i<m_protMFL.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protMFL.m_appdata.Send[i][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mTransRate =0;
					m_protMFL.m_appdata.Send[i][MFL_TX_PNG.MFL_TX_BATCONFIG.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
				}
			}
		}
	}
}



class MFLConst_PARA{
	short pf;
	short ps;
	short sa;
	short prior;
	short page;
	J1939_FrameType framtype;
	DriverTransType transtype;
	int  rate;

	MFLConst_PARA(	int i,
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

enum MFL_RX_PNG{//接收参数组
	MFL_RX_BASEINFO,
	MFL_RX_STATUS,
	MFL_RX_RATEDINFO,
	MFL_RX_VINCODE,
	MFL_RX_PNG_TOTAL
}

enum MFL_TX_PNG{//发送参数组
	MFL_TX_MFLCONTROL,
	MFL_TX_CARVINCODE,
	MFL_TX_BATCONFIG,
	MFL_TX_PNG_TOTAL;
}
