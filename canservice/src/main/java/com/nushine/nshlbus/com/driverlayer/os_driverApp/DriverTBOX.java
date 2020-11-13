/*ver:0.0.1
 *auth:xuxb
 *data:16.8.10
 *bref:TBOX
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

public class DriverTBOX {
	public boolean TBOXDeBug=false;	 //TBOX调试开关
	public boolean TBOXCommStatus=true;//false表示超时
	public boolean TBOXTxStatus = true;//false表示天线异常
	public boolean TBOXTxAllow = false;//false表示未授权
	public boolean TBOXTxAllowTimeOut = true;//false表示授权超时
	private Driver_TBOX_DataBuf m_TBOXPortStack = new Driver_TBOX_DataBuf();
	private DriverTBOXTask m_TBOXtcb; //TBOX控制块

	public DriverTBOX(Global_Cfg cfg){
		m_TBOXtcb = new DriverTBOXTask(cfg.m_stack);
	}

	public void Change_TBOXControlSendCycle(){
		m_TBOXtcb.Port_TBOXBaseInfoImmediatelySend();
		m_TBOXtcb.Tx_TBOXBaseInfo(TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal());
		m_TBOXtcb.Port_TBOXStatusImmediatelySend();
		m_TBOXtcb.Tx_TBOXStatus(TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal());
	}

	public void Change_TBOXControlStop(){
		m_TBOXtcb.Port_TBOXBaseInfoImmediatelyStop();
		m_TBOXtcb.Port_TBOXStatusImmediatelyStop();
	}

	//BCU协议栈
	class Driver_TBOX_DataBuf{
		//协议栈
		final TBOXConst_PARA[] Recv_PngData = {
				new TBOXConst_PARA(0xF1,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,1000),
				new TBOXConst_PARA(0xF1,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,2000),
				new TBOXConst_PARA(0xF1,0x08,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,2000)
		};
		final TBOXConst_PARA[] Send_PngData = {
				new TBOXConst_PARA(0xF0,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,600),
				new TBOXConst_PARA(0xF0,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,600)
		};
		//收发原始数据缓存定义
		final  byte CONST_TBOX_DATALEN=8;//数据长度
		final  short MAX_RECVBUFF_SIZE=512;//每个png可以存储的报文数据量
		int[] TBOX_RecvGetAddr = new int[TBOX_RX_PNG.TBOX_RX_PNG_TOTAL.ordinal()];//接收缓存读指针
		int[] TBOX_RecvSetAddr = new int[TBOX_RX_PNG.TBOX_RX_PNG_TOTAL.ordinal()];//接收缓存写指针
		short[][][] m_TBOXDataRcev_Buff = new short[TBOX_RX_PNG.TBOX_RX_PNG_TOTAL.ordinal()][MAX_RECVBUFF_SIZE][CONST_TBOX_DATALEN];
		short[][] m_TBOXDataSend_Buff = new short[TBOX_TX_PNG.TBOX_TX_PNG_TOTAL.ordinal()][CONST_TBOX_DATALEN];//每帧数据的发送缓存只需要分配一个空间

		Driver_TBOX_DataBuf(){
			//数据收发缓存初始化，收发指针初始化
			for(int i=0;i<TBOX_RecvGetAddr.length;i++){
				TBOX_RecvGetAddr[i]=0;
			}
			for(int i=0;i<TBOX_RecvSetAddr.length;i++){
				TBOX_RecvSetAddr[i]=0;
			}
			for(int i=0;i<m_TBOXDataRcev_Buff.length;i++)
				for(int j=0;j<m_TBOXDataRcev_Buff[i].length;j++)
					for(int k=0;k<m_TBOXDataRcev_Buff[i][j].length;k++){
						m_TBOXDataRcev_Buff[i][j][k]=0;
					}
			for(int i=0;i<m_TBOXDataSend_Buff.length;i++)
				for(int j=0;j<m_TBOXDataSend_Buff[i].length;j++){
					m_TBOXDataSend_Buff[i][j] = 0;
				}
		}

		//从接收缓存中读取一个数据
		public  short[]  TBOX_GetRecvBuffer(int png){
			short[] data;
			if(TBOX_RecvGetAddr[png]==TBOX_RecvSetAddr[png]){
				return null;
			}else{
				synchronized(this){
					data = m_TBOXDataRcev_Buff[png][TBOX_RecvGetAddr[png]].clone();
					TBOX_RecvGetAddr[png] = (short) ((TBOX_RecvGetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return data;
		}
		//接收数据写入缓存
		public  boolean Can_SetRecvBuffer(short[] data,int png)
		{
			if(((TBOX_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE)==TBOX_RecvGetAddr[png]){
				return false;
			}else{
				synchronized(this){
					m_TBOXDataRcev_Buff[png][TBOX_RecvSetAddr[png]] = null;
					m_TBOXDataRcev_Buff[png][TBOX_RecvSetAddr[png]] = data.clone();
					TBOX_RecvSetAddr[png] = (short) ((TBOX_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return true;
		}
	}

	//协议解析类
	class DriverTBOXTask extends DriverCallPro {
		final short TBOX_Num = 1;				//BCU默认数量
		Logger log = Logger.getLogger(DriverTBOXTask.class);
		final Driver_TBOX_DataBuf m_TBOXapp = new Driver_TBOX_DataBuf();//协议栈定义
		DriverProtocolStack m_protTBOX; //定义一个应用堆栈

		DriverTBOXTask(cfgPortStack stack) {
			//协议栈初始化
			InitAppData();
			m_protTBOX.m_callpro = this;
			if(stack.m_port.crateProtApp(m_protTBOX)){
				log.debug("DriverTBOXTask is create success!");
			}else{
				log.debug("DriverTBOXTask is create failed!");
			}
		}

		void InitAppData(){
			m_protTBOX = new DriverProtocolStack(TBOX_Num,(short) m_TBOXPortStack.Send_PngData.length,(short) m_TBOXPortStack.Recv_PngData.length);
			m_protTBOX.m_appdata.mHardType = DriverHardType.DH_CAN;//can设备
			for(int i=0;i<m_protTBOX.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protTBOX.m_appdata.TotalRecvPara;j++){
					m_protTBOX.m_appdata.Recv[i][j].mDataMaxLen = m_TBOXPortStack.CONST_TBOX_DATALEN;
					m_protTBOX.m_appdata.Recv[i][j].mDataLen = m_TBOXPortStack.CONST_TBOX_DATALEN;
					m_protTBOX.m_appdata.Recv[i][j].mSource = (byte) m_TBOXPortStack.Recv_PngData[j].sa;
					m_protTBOX.m_appdata.Recv[i][j].mTransRate = (int) m_TBOXPortStack.Recv_PngData[j].rate;
					m_protTBOX.m_appdata.Recv[i][j].mTimer = 0;
					m_protTBOX.m_appdata.Recv[i][j].mEnable = true;
					m_protTBOX.m_appdata.Recv[i][j].mPGState = DriverPgStatus.PG_RX_FREE;
					m_protTBOX.m_appdata.Recv[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protTBOX.m_appdata.Recv[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
			for(int i=0;i<m_protTBOX.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protTBOX.m_appdata.TotalSendPara;j++){
					m_protTBOX.m_appdata.Send[i][j].mDataMaxLen = m_TBOXPortStack.CONST_TBOX_DATALEN;
					m_protTBOX.m_appdata.Send[i][j].mDataLen = m_TBOXPortStack.CONST_TBOX_DATALEN;
					m_protTBOX.m_appdata.Send[i][j].mSource = (short) m_TBOXPortStack.Send_PngData[j].sa;
					m_protTBOX.m_appdata.Send[i][j].mTransRate = (int) m_TBOXPortStack.Send_PngData[j].rate;
					m_protTBOX.m_appdata.Send[i][j].mTimer = 0;
					m_protTBOX.m_appdata.Send[i][j].mEnable = true;
					m_protTBOX.m_appdata.Send[i][j].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protTBOX.m_appdata.Send[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protTBOX.m_appdata.Send[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
		}
		//接收回调接口
		public boolean RxSourceData(J1939FrameFormat j1939){
			int m_rxnum = TBOX_RX_PNG.TBOX_RX_PNG_TOTAL.ordinal();
			//判断报文是否属于BCU
			for(int i = 0; i <m_rxnum;i++){
				if(j1939.PS == m_TBOXPortStack.Recv_PngData[i].ps
						&& j1939.PF == m_TBOXPortStack.Recv_PngData[i].pf
						&& j1939.FramType == m_TBOXPortStack.Recv_PngData[i].framtype){
					if(j1939.Addr >= m_TBOXPortStack.Recv_PngData[i].sa){
						short addr = (short) (j1939.Addr - m_TBOXPortStack.Recv_PngData[i].sa);
						if(addr >= TBOX_Num || addr <0){
							return false;
						}
						if(m_protTBOX.m_appdata.Recv[addr][i].mEnable){
							if(j1939.DtaLen <= m_protTBOX.m_appdata.Recv[addr][i].mDataMaxLen){
								m_TBOXPortStack.Can_SetRecvBuffer(j1939.pDta,(byte)i);
								m_protTBOX.m_appdata.Recv[addr][i].mDataLen = (byte) j1939.DtaLen;
								m_protTBOX.m_appdata.Recv[addr][i].mPGState = DriverPgStatus.PG_RX_FULL;
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
			if(wpara == TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()){
				Tx_TBOXBaseInfo(lpara);
			}else if(wpara == TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()){
				Tx_TBOXStatus(lpara);
			}
		}

		@Override
		public boolean rxFrame(int lpara, int wpara) {
			// TODO Auto-generated method stub
			short[] m_data = new short[8];
			m_data = m_TBOXPortStack.TBOX_GetRecvBuffer(wpara);
			if(m_data==null || TBOXDeBug){	//调试模式下不接受CAN总线数据
				return false;
			}
			if(wpara == TBOX_RX_PNG.TBOX_RX_TBOXBASEINFO.ordinal()){
				Rx_TBOXBaseInfo(m_data,lpara);
				timeoutnum = 0;
			}else if(wpara == TBOX_RX_PNG.TBOX_RX_TBOXSTATUS.ordinal()){
				Rx_TBOXStatus(m_data,lpara);
			}else if(wpara == TBOX_RX_PNG.TBOX_RX_TBOXALLOW.ordinal()){
				Rx_TBOXAllow(m_data,lpara);
				allowtimeoutnum = 0;
			}else{
				return false;
			}
			return true;
		}

		@Override
		public void rxTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub
			if(wpara == TBOX_RX_PNG.TBOX_RX_TBOXBASEINFO.ordinal()){
				Rx_TBOXTimeOut(lpara);
			}else if(wpara == TBOX_RX_PNG.TBOX_RX_TBOXALLOW.ordinal()){
				Rx_TBOXAllowTimeOut(lpara);
			}
		}

		@Override
		public void txTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub

		}

		private void Rx_TBOXBaseInfo(short[] pdta,int Addr){
			TBOXCommStatus = true;
		}

		private void Rx_TBOXStatus(short[] pdta,int Addr){
			if(((pdta[0]>>2)&0x03) == 0x01){
				TBOXTxStatus = false;
			}else if(((pdta[0]>>2)&0x03) == 0x00){
				TBOXTxStatus = true;
			}
		}


		private void Rx_TBOXAllow(short[] pdta,int Addr){
			if(pdta[7]==0xa5){//授权
				TBOXTxAllow = true;
			}else if(pdta[7]==0xb5){//未授权
				TBOXTxAllow = false;
			}
			TBOXTxAllowTimeOut = true;
		}

		@Override
		public J1939FrameFormat SendFrame(int lpara, int wpara) {//CAN数据发送接口
			// TODO Auto-generated method stub
			if(wpara>TBOX_TX_PNG.TBOX_TX_PNG_TOTAL.ordinal()){
				return null;
			}

			J1939FrameFormat m_send = new J1939FrameFormat();
			m_send.FramType = m_TBOXPortStack.Send_PngData[wpara].framtype;//DriverBcuTask.m_protbcu.J1939_FrameType;
			m_send.PF = m_TBOXPortStack.Send_PngData[wpara].pf;
			m_send.PS = m_TBOXPortStack.Send_PngData[wpara].ps;
			m_send.Addr = m_protTBOX.m_appdata.Send[lpara][wpara].mSource;
			m_send.Prior = m_TBOXPortStack.Send_PngData[wpara].prior;
			m_send.Page = m_TBOXPortStack.Send_PngData[wpara].page;
			m_send.DtaLen = m_TBOXPortStack.CONST_TBOX_DATALEN;
			m_send.pDta = m_TBOXPortStack.m_TBOXDataSend_Buff[wpara].clone();
			for(int i=0;i<m_TBOXPortStack.m_TBOXDataSend_Buff[wpara].length;i++){
				m_TBOXPortStack.m_TBOXDataSend_Buff[wpara][i]=0;
			}
			return m_send;
		}


		private void Tx_TBOXBaseInfo(int Addr){
			m_TBOXPortStack.m_TBOXDataSend_Buff[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()][0] = 0x00;
			m_TBOXPortStack.m_TBOXDataSend_Buff[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()][2] = 0x32;
			m_TBOXPortStack.m_TBOXDataSend_Buff[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()][3] = 0xA0;
			m_TBOXPortStack.m_TBOXDataSend_Buff[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()][4] = 0x12;
			m_TBOXPortStack.m_TBOXDataSend_Buff[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()][5] = 0xA0;

			synchronized(this){
				int rate = m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate;
				if(rate==0){
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate=1;
				}else if(rate==1){
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate=21;
				}else{	//周期性发送
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate = (int) m_TBOXapp.Send_PngData[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].rate;
				}
			}
		}

		private void Tx_TBOXStatus(int Addr){
			m_TBOXPortStack.m_TBOXDataSend_Buff[TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()][0] = 0x02;

			synchronized(this){
				int rate = m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate;
				if(rate==0){
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate=1;
				}else if(rate==1){
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate=21;
				}else{	//周期性发送
					m_protTBOX.m_appdata.Send[Addr][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate = (int) m_TBOXapp.Send_PngData[TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].rate;
				}
			}
		}

		byte timeoutnum=0;
		private void Rx_TBOXTimeOut(int Addr){
			final byte TBOX_TIMEOUT_MAX = 31;	//bcu通讯31次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>TBOX_TIMEOUT_MAX){
				timeoutnum = 0;
				TBOXCommStatus = false;
				log.warn("TBOX timeout!");
			}
		}
		byte allowtimeoutnum = 0;
		private void Rx_TBOXAllowTimeOut(int Addr){
			final byte TBOX_TIMEOUT_MAX = 30;	//bcu通讯3次超时，判定为通讯超时
			allowtimeoutnum++;
			if(allowtimeoutnum>TBOX_TIMEOUT_MAX){
				allowtimeoutnum = 0;
				TBOXTxAllowTimeOut = false;
			}
		}
		//定义几个立刻发送执行的接口
		void Port_TBOXBaseInfoImmediatelySend(){
			for(int i=0;i<m_protTBOX.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate =0;
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
				}
			}
		}
		//定义几个立刻发送执行的接口
		void Port_TBOXStatusImmediatelySend(){
			for(int i=0;i<m_protTBOX.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate =0;
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
				}
			}
		}
		void Port_TBOXBaseInfoImmediatelyStop(){
			for(int i=0;i<m_protTBOX.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].mTransRate = (int) m_TBOXapp.Send_PngData[TBOX_TX_PNG.TBOX_TX_TBOXBASEINFO.ordinal()].rate;
				}
			}
		}
		void Port_TBOXStatusImmediatelyStop(){
			for(int i=0;i<m_protTBOX.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protTBOX.m_appdata.Send[i][TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].mTransRate = (int) m_TBOXapp.Send_PngData[TBOX_TX_PNG.TBOX_TX_TBOXSTATUS.ordinal()].rate;
				}
			}
		}
	}
}



class TBOXConst_PARA{
	short pf;
	short ps;
	short sa;
	short prior;
	short page;
	J1939_FrameType framtype;
	DriverTransType transtype;
	int  rate;

	TBOXConst_PARA(	int i,
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

enum TBOX_RX_PNG{//接收参数组
	TBOX_RX_TBOXBASEINFO,
	TBOX_RX_TBOXSTATUS,
	TBOX_RX_TBOXALLOW,
	TBOX_RX_PNG_TOTAL
}

enum TBOX_TX_PNG{//发送参数组
	TBOX_TX_TBOXBASEINFO,
	TBOX_TX_TBOXSTATUS,
	TBOX_TX_PNG_TOTAL;
}