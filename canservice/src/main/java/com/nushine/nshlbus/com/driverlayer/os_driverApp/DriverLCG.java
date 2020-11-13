/*ver:0.0.1
 *auth:hpp
 *data:15.4.23
 *bref:LCG应用解析及控制车身信息
 *Revision record:0.0.1 初版实现《康迪汽车LCG通讯规约V1.0》
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
public class DriverLCG {
	public boolean LCGDeBug=false;	 //Bcu调试开关
	public  LCG_Info m_LCGinfo=new LCG_Info();//BCU信息
	public  boolean LCGCommStatus=true;//false表示超时
	private Driver_LCG_DataBuf m_LCGPortStack = new Driver_LCG_DataBuf();
	private DriverLCGTask     m_LCGtcb; //BCU控制块

	public DriverLCG(Global_Cfg cfg){
		m_LCGtcb = new DriverLCGTask(cfg.m_stack);
	}

	public class LCG_Info{
		//基本信息
		public byte m_vender=(byte) 0xff;
		public short m_softver=0;
		public short m_hardver=0;

		public short m_LCGVolt_Rtd = 0;
		public short m_LCGCur_Rtd = 0;
		public byte[] m_LCGvincode = new byte[6];

		public int m_PosInsulationValue = 0;//总正绝缘阻值
		public int m_NegInsulationValue = 0;//总负绝缘阻值
		//绝缘阻值低一级报警;绝缘阻值低二级报警;继电器粘连故障;高压异常;基准异常
		public byte[] m_warn_error = new byte[5];//0：报警/故障未发生；1：报警/故障发生

		public short m_ChgGunCableCap_Rad = 0;//电缆额定容量
		public short m_ChgPileMaxOutputCur = 0;//充电桩最大供电电流//默认为32A
		public byte m_ChgGunConnect = 0;//充电枪接入确认  0：拔出；1：插入
		public byte m_ChgGunValid = 0;//充电枪有效性 0：充电枪无效；1：充电枪有效

		LCG_Info(){
			for(int i=0;i<m_warn_error.length;i++){
				m_warn_error[i] = 0;
			}
		}
	}

	//BCU协议栈
	class Driver_LCG_DataBuf{
		//协议栈
		final LCGConst_PARA[] Recv_PngData = {
				new LCGConst_PARA(0x0A,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//设备信息BCU_RX_BASEINFO
				new LCGConst_PARA(0x0A,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//BCU总状态BCU_RX_STATUS
				new LCGConst_PARA(0x0A,0x08,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//额定电压、电流
				new LCGConst_PARA(0x0A,0x11,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//额定电压、电流
				new LCGConst_PARA(0x0A,0xFB,0x02,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,5000),//设备唯一码
		};
		final LCGConst_PARA[] Send_PngData = {
		};
		//收发原始数据缓存定义
		final  byte CONST_LCG_DATALEN=8;//数据长度
		final  short MAX_RECVBUFF_SIZE=512;//每个png可以存储的报文数据量
		int[] LCG_RecvGetAddr = new int[LCG_RX_PNG.LCG_RX_PNG_TOTAL.ordinal()];//接收缓存读指针
		int[] LCG_RecvSetAddr = new int[LCG_RX_PNG.LCG_RX_PNG_TOTAL.ordinal()];//接收缓存写指针
		short[][][] m_LCGDataRcev_Buff = new short[LCG_RX_PNG.LCG_RX_PNG_TOTAL.ordinal()][MAX_RECVBUFF_SIZE][CONST_LCG_DATALEN];
		short[][] m_LCGDataSend_Buff = new short[LCG_TX_PNG.LCG_TX_PNG_TOTAL.ordinal()][CONST_LCG_DATALEN];//每帧数据的发送缓存只需要分配一个空间

		Driver_LCG_DataBuf(){
			//数据收发缓存初始化，收发指针初始化
			for(int i=0;i<LCG_RecvGetAddr.length;i++){
				LCG_RecvGetAddr[i]=0;
			}
			for(int i=0;i<LCG_RecvSetAddr.length;i++){
				LCG_RecvSetAddr[i]=0;
			}
			for(int i=0;i<m_LCGDataRcev_Buff.length;i++)
				for(int j=0;j<m_LCGDataRcev_Buff[i].length;j++)
					for(int k=0;k<m_LCGDataRcev_Buff[i][j].length;k++){
						m_LCGDataRcev_Buff[i][j][k]=0;
					}
			for(int i=0;i<m_LCGDataSend_Buff.length;i++)
				for(int j=0;j<m_LCGDataSend_Buff[i].length;j++){
					m_LCGDataSend_Buff[i][j] = 0;
				}
		}

		//从接收缓存中读取一个数据
		public  short[]  LCG_GetRecvBuffer(int png){
			short[] data;
			if(LCG_RecvGetAddr[png]==LCG_RecvSetAddr[png]){
				return null;
			}else{
				synchronized(this){
					data = m_LCGDataRcev_Buff[png][LCG_RecvGetAddr[png]].clone();
					LCG_RecvGetAddr[png] = (short) ((LCG_RecvGetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return data;
		}
		//接收数据写入缓存
		public  boolean Can_SetRecvBuffer(short[] data,int png)
		{
			if(((LCG_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE)==LCG_RecvGetAddr[png]){
				return false;
			}else{
				synchronized(this){
					m_LCGDataRcev_Buff[png][LCG_RecvSetAddr[png]] = null;
					m_LCGDataRcev_Buff[png][LCG_RecvSetAddr[png]] = data.clone();
					LCG_RecvSetAddr[png] = (short) ((LCG_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return true;
		}
	}

	//协议解析类
	class DriverLCGTask extends DriverCallPro{
		final short LCGApp_Addr_Start = 0xFF;	//BCU默认地址
		final short LCGApp_Addr_Code = 0x02;	//code接收默认
		final short LCG_Num = 1;				//BCU默认数量
		Logger log = Logger.getLogger(DriverLCGTask.class);
		final Driver_LCG_DataBuf m_LCGapp = new Driver_LCG_DataBuf();//协议栈定义
		DriverProtocolStack m_protLCG; //定义一个应用堆栈

		DriverLCGTask(cfgPortStack stack) {
			//协议栈初始化
			InitAppData();
			m_protLCG.m_callpro = this;
			if(stack.m_port.crateProtApp(m_protLCG)){
				log.debug("DriverLCGTask is create success!");
			}else{
				log.debug("DriverLCGTask is create failed!");
			}
		}

		void InitAppData(){
			m_protLCG = new DriverProtocolStack(LCG_Num,(short) m_LCGPortStack.Send_PngData.length,(short) m_LCGPortStack.Recv_PngData.length);
			m_protLCG.m_appdata.mHardType = DriverHardType.DH_CAN;//can设备
			for(int i=0;i<m_protLCG.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protLCG.m_appdata.TotalRecvPara;j++){
					m_protLCG.m_appdata.Recv[i][j].mDataMaxLen = m_LCGPortStack.CONST_LCG_DATALEN;
					m_protLCG.m_appdata.Recv[i][j].mDataLen = m_LCGPortStack.CONST_LCG_DATALEN;
					m_protLCG.m_appdata.Recv[i][j].mSource = (byte) m_LCGPortStack.Recv_PngData[j].sa;
					m_protLCG.m_appdata.Recv[i][j].mTransRate = (int) m_LCGPortStack.Recv_PngData[j].rate;
					m_protLCG.m_appdata.Recv[i][j].mTimer = 0;
					m_protLCG.m_appdata.Recv[i][j].mEnable = true;
					m_protLCG.m_appdata.Recv[i][j].mPGState = DriverPgStatus.PG_RX_FREE;
					m_protLCG.m_appdata.Recv[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protLCG.m_appdata.Recv[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
			for(int i=0;i<m_protLCG.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protLCG.m_appdata.TotalSendPara;j++){
					m_protLCG.m_appdata.Send[i][j].mDataMaxLen = m_LCGPortStack.CONST_LCG_DATALEN;
					m_protLCG.m_appdata.Send[i][j].mSource = (short) m_LCGPortStack.Send_PngData[j].sa;
					m_protLCG.m_appdata.Send[i][j].mDataLen = m_LCGPortStack.CONST_LCG_DATALEN;
					m_protLCG.m_appdata.Send[i][j].mTransRate = (int) m_LCGPortStack.Send_PngData[j].rate;
					m_protLCG.m_appdata.Send[i][j].mTimer = 0;
					m_protLCG.m_appdata.Send[i][j].mEnable = true;
					m_protLCG.m_appdata.Send[i][j].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protLCG.m_appdata.Send[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protLCG.m_appdata.Send[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
		}
		//接收回调接口
		public boolean RxSourceData(J1939FrameFormat j1939){
			int m_rxnum = LCG_RX_PNG.LCG_RX_PNG_TOTAL.ordinal();
			//判断报文是否属于BCU
			for(int i = 0; i <m_rxnum;i++){
				if(j1939.PS == m_LCGPortStack.Recv_PngData[i].ps
						&& j1939.PF == m_LCGPortStack.Recv_PngData[i].pf
						&& j1939.FramType == m_LCGPortStack.Recv_PngData[i].framtype){
					if(j1939.Addr >= m_LCGPortStack.Recv_PngData[i].sa){
						short addr = (short) (j1939.Addr - m_LCGPortStack.Recv_PngData[i].sa);
						if(addr >= LCG_Num || addr <0){
							return false;
						}
						if(m_protLCG.m_appdata.Recv[addr][i].mEnable){
							if(j1939.DtaLen <= m_protLCG.m_appdata.Recv[addr][i].mDataMaxLen){
								m_LCGPortStack.Can_SetRecvBuffer(j1939.pDta,(byte)i);
								m_protLCG.m_appdata.Recv[addr][i].mDataLen = (byte) j1939.DtaLen;
								m_protLCG.m_appdata.Recv[addr][i].mPGState = DriverPgStatus.PG_RX_FULL;
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
		}

		@Override
		public boolean rxFrame(int lpara, int wpara) {
			// TODO Auto-generated method stub
			short[] m_data = new short[8];
			m_data = m_LCGPortStack.LCG_GetRecvBuffer(wpara);
			if(m_data==null || LCGDeBug){	//调试模式下不接受CAN总线数据
				return false;
			}

			if(wpara == LCG_RX_PNG.LCG_RX_BASEINFO.ordinal()){
				Rx_LCGBaseInfo(m_data,lpara);
			}else if(wpara == LCG_RX_PNG.LCG_RX_RATEDINFO.ordinal()){
				Rx_LCGRatedInfo(m_data,lpara);
			}else if(wpara == LCG_RX_PNG.LCG_RX_STATUS.ordinal()){
				Rx_LCGStatus(m_data,lpara);
				timeoutnum = 0;
			}else if(wpara == LCG_RX_PNG.LCG_RX_CHGINFO.ordinal()){
				Rx_LCGChgInfo(m_data,lpara);
			}else if(wpara == LCG_RX_PNG.LCG_RX_VINCODE.ordinal()){
				Rx_LCGVincode(m_data,lpara);
			}else{
				return false;
			}
			return true;
		}

		@Override
		public void rxTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub
			if(wpara == LCG_RX_PNG.LCG_RX_BASEINFO.ordinal()){
				Rx_LCGTimeOut(lpara);

			}
		}

		@Override
		public void txTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub

		}

		@Override
		public J1939FrameFormat SendFrame(int lpara, int wpara) {//CAN数据发送接口
			// TODO Auto-generated method stub
			if(wpara>LCG_TX_PNG.LCG_TX_PNG_TOTAL.ordinal()){
				return null;
			}

			J1939FrameFormat m_send = new J1939FrameFormat();
			m_send.FramType = m_LCGPortStack.Send_PngData[wpara].framtype;//DriverBcuTask.m_protbcu.J1939_FrameType;
			m_send.PF = m_LCGPortStack.Send_PngData[wpara].pf;
			m_send.PS = m_LCGPortStack.Send_PngData[wpara].ps;
			m_send.Addr = m_protLCG.m_appdata.Send[lpara][wpara].mSource;
			m_send.Prior = m_LCGPortStack.Send_PngData[wpara].prior;
			m_send.Page = m_LCGPortStack.Send_PngData[wpara].page;
			m_send.DtaLen = m_LCGPortStack.CONST_LCG_DATALEN;
			m_send.pDta = m_LCGPortStack.m_LCGDataSend_Buff[wpara].clone();
			for(int i=0;i<m_LCGPortStack.m_LCGDataSend_Buff[wpara].length;i++){
				m_LCGPortStack.m_LCGDataSend_Buff[wpara][i]=0;
			}
			return m_send;
		}

		/****************数据接收处理****************/
		private void Rx_LCGBaseInfo(short[] pdta,int Addr){
			m_LCGinfo.m_vender = (byte) pdta[0];
			m_LCGinfo.m_hardver = (short) ((pdta[2]&0xff)<<8|(pdta[3]&0xff));
			m_LCGinfo.m_softver = (short) ((pdta[4]&0xff)<<8|(pdta[5]&0xff));
		}
		private void Rx_LCGRatedInfo(short[] pdta,int Addr){
			m_LCGinfo.m_LCGVolt_Rtd = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_LCGinfo.m_LCGCur_Rtd = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
		}
		private void Rx_LCGVincode(short[] pdta,int Addr){
			for(int i=0;i<m_LCGinfo.m_LCGvincode.length;i++){
				m_LCGinfo.m_LCGvincode[i] = (byte) pdta[i+1];
			}
		}
		private void Rx_LCGChgInfo(short[] pdta,int Addr){
			m_LCGinfo.m_ChgGunCableCap_Rad = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_LCGinfo.m_ChgPileMaxOutputCur = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
			m_LCGinfo.m_ChgGunConnect = (byte) (pdta[4]&0x01);
			m_LCGinfo.m_ChgGunValid = (byte) ((pdta[4]>>1)&0x01);
		}
		//BCU工作工状态信息
		private void Rx_LCGStatus(short[] pdta,int Addr){
			m_LCGinfo.m_PosInsulationValue = (int) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_LCGinfo.m_NegInsulationValue = (int) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
			for(int i=0;i<5;i++){
				m_LCGinfo.m_warn_error[i] = (byte) ((pdta[4]>>i)&0x01);
			}
		}

		byte timeoutnum=0;
		private void Rx_LCGTimeOut(int Addr){
			final byte LCG_TIMEOUT_MAX = 3;	//bcu通讯3次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>LCG_TIMEOUT_MAX){
				timeoutnum = 0;
				LCGCommStatus = false;
				log.warn("LCG timeout!");
			}
		}

	}
}



class LCGConst_PARA{
	short pf;
	short ps;
	short sa;
	short prior;
	short page;
	J1939_FrameType framtype;
	DriverTransType transtype;
	int  rate;

	LCGConst_PARA(	int i,
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

enum LCG_RX_PNG{//接收参数组
	LCG_RX_BASEINFO,
	LCG_RX_STATUS,
	LCG_RX_RATEDINFO,
	LCG_RX_CHGINFO,
	LCG_RX_VINCODE,
	LCG_RX_PNG_TOTAL
}

enum LCG_TX_PNG{//发送参数组
	LCG_TX_PNG_TOTAL;
}