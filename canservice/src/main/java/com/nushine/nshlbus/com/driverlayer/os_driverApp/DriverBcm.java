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
	public Bcm_Info m_BcmInfo=new Bcm_Info();
	public DriverBcmTask     m_bcmtcb; //BCU控制块

	public DriverBcm(Global_Cfg cfg){
		m_bcmtcb = new DriverBcmTask(cfg.m_stack);
	}

	public class Bcm_Info{
		public Bcm_CarControl m_setcarcontrol=new Bcm_CarControl();
		public Bcm_CarControl m_getcarcontrol=new Bcm_CarControl();
	}

	public class Bcm_CarControl{//车身控制信息
		public byte m_baggage_light = 0;//行李仓灯开关
		public byte m_read_light = 0;//阅读灯开关
		public byte m_mainchange_light = 0;//主照明灯调节开关
		public byte m_main_light1 = 0;//主照明灯开关1
		public byte m_midtv_light = 0;//中TV开关
		public byte m_main_light2 = 0;//主照明灯开关2
		public byte m_fronttv_light = 0;//前TV开关
		public byte m_horn_transfer = 0;//喇叭转换开关
		public byte m_ecas_reset = 0;//ECAS 复位开关
		public byte m_rearfog_light = 0;//后雾灯开关
		public byte m_veneer_light = 0;//装饰板灯开关
		public byte m_atmosphere_light2 = 0;//氛围灯2开关
		public byte m_atmosphere_light1 = 0;//氛围灯1开关
		public byte m_glass_defrost = 0;//玻璃除霜开关
		public byte m_drivertop_light = 0;//司机顶灯开关
		public byte m_aisle_light = 0;//过道灯开关
		public byte m_frontfog_light = 0;//前雾灯开关
		public byte m_driver_fan = 0;//司机风扇开关
		public byte m_scuttle_in = 0;//天窗进气
		public byte m_scuttle_out = 0;//天窗排气
		public byte m_ecas_down = 0;//ECAS 下降开关
		public byte m_ecas_up = 0;//ECAS 上升开关
		public byte m_ecas_kneelside = 0;//ECAS 侧跪开关
		public byte m_heightII = 0;//高度 II 开关
		public int m_mainchange_light_per = 0;//主照明灯调节分辨率
		public byte m_frontdecorative_water = 0;//前装饰流水开关
		public byte m_signboard = 0;//路牌开关
		public byte m_radiator2 = 0;//散热器开关 2
		public byte m_radiator1 = 0;//散热器开关 1
		public int m_byte6 = 0;
		public int m_byte7 = 0;
	}

	public void Change_BCMControlSendCycle(){
		m_bcmtcb.Port_BcmImmediatelySend();
	}
	//BCU协议栈
	class Driver_Bcm_DataBuf{
		//协议栈
		final BcmConst_PARA[] Recv_PngData = {
				new BcmConst_PARA(0xFE,0xD0,0x17,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500)//各电器状态
		};
		final BcmConst_PARA[] Send_PngData = {
				new BcmConst_PARA(0xFF,0x19,0xAE,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500)
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

			if(wpara == BCM_RX_PNG.BCM_RX_STATUS.ordinal()){
				Rx_BcmStatus(m_data,lpara);
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
		//BCU工作工状态信息
		private void Rx_BcmStatus(short[] pdta,int Addr){
			m_BcmInfo.m_getcarcontrol.m_baggage_light = (byte)(pdta[0]>>5 & 0x01);//行李仓灯开关
			m_BcmInfo.m_getcarcontrol.m_read_light = (byte)(pdta[0]>>4 & 0x01);//阅读灯开关
			m_BcmInfo.m_getcarcontrol.m_mainchange_light = (byte)(pdta[1]>>7 & 0x01);//主照明灯调节开关
			m_BcmInfo.m_getcarcontrol.m_main_light1 = (byte)(pdta[0]>>3 & 0x01);//主照明灯开关1
			m_BcmInfo.m_getcarcontrol.m_midtv_light = (byte)(pdta[0]>>2 & 0x01);//中TV开关
			m_BcmInfo.m_getcarcontrol.m_main_light2 = (byte)(pdta[0]>>6 & 0x01);//主照明灯开关2
			m_BcmInfo.m_getcarcontrol.m_fronttv_light = (byte)(pdta[0]>>1 & 0x01);//前TV开关
			m_BcmInfo.m_getcarcontrol.m_horn_transfer = (byte)(pdta[0]>>7 & 0x01);//喇叭转换开关
			m_BcmInfo.m_getcarcontrol.m_ecas_reset = (byte)(pdta[2]>>6 & 0x01);//ECAS 复位开关
			m_BcmInfo.m_getcarcontrol.m_rearfog_light = (byte)(pdta[1]>>6 & 0x01);//后雾灯开关
			m_BcmInfo.m_getcarcontrol.m_veneer_light = (byte)(pdta[1]>>4 & 0x01);//装饰板灯开关
			m_BcmInfo.m_getcarcontrol.m_atmosphere_light2 = (byte)(pdta[1]>>3 & 0x01);//氛围灯2开关
			m_BcmInfo.m_getcarcontrol.m_atmosphere_light1 = (byte)(pdta[1]>>2 & 0x01);//氛围灯1开关
			m_BcmInfo.m_getcarcontrol.m_glass_defrost = (byte)(pdta[4]>>5 & 0x01);//玻璃除霜开关
			m_BcmInfo.m_getcarcontrol.m_drivertop_light = (byte)(pdta[1]>>1 & 0x01);//司机顶灯开关
			m_BcmInfo.m_getcarcontrol.m_aisle_light = (byte)(pdta[1] & 0x01);//过道灯开关
			m_BcmInfo.m_getcarcontrol.m_frontfog_light = (byte)(pdta[2]>>7 & 0x01);//前雾灯开关
			m_BcmInfo.m_getcarcontrol.m_driver_fan = (byte)(pdta[4]>>6 & 0x01);//司机风扇开关
			m_BcmInfo.m_getcarcontrol.m_scuttle_in = (byte)(pdta[2]>>1 & 0x01);//天窗进气
			m_BcmInfo.m_getcarcontrol.m_scuttle_out = (byte)(pdta[2] & 0x01);//天窗排气
			m_BcmInfo.m_getcarcontrol.m_ecas_down = (byte)(pdta[2]>>5 & 0x01);//ECAS 下降开关
			m_BcmInfo.m_getcarcontrol.m_ecas_up = (byte)(pdta[2]>>4 & 0x01);//ECAS 上升开关
			m_BcmInfo.m_getcarcontrol.m_ecas_kneelside = (byte)(pdta[2]>>3 & 0x01);//ECAS 侧跪开关
			m_BcmInfo.m_getcarcontrol.m_heightII = (byte)(pdta[2]>>2 & 0x01);//高度 II 开关
			m_BcmInfo.m_getcarcontrol.m_mainchange_light_per = pdta[3] & 0xff;//主照明灯调节分辨率
			m_BcmInfo.m_getcarcontrol.m_frontdecorative_water = (byte)(pdta[1]>>5 & 0x01);//前装饰流水开关
			m_BcmInfo.m_getcarcontrol.m_signboard = (byte)(pdta[4]>>4 & 0x01);//路牌开关
			m_BcmInfo.m_getcarcontrol.m_radiator2 = (byte)(pdta[4]>>3 & 0x01);//散热器开关 2
			m_BcmInfo.m_getcarcontrol.m_radiator1 = (byte)(pdta[4]>>2 & 0x01);//散热器开关 1
			m_BcmInfo.m_getcarcontrol.m_byte6 = pdta[6] & 0xff;
			m_BcmInfo.m_getcarcontrol.m_byte7 = pdta[7] & 0xff;
		}

		byte timeoutnum=0;
		private void Rx_BcmTimeOut(int Addr){
			final byte BCM_TIMEOUT_MAX = 3;	//bcm通讯3次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>BCM_TIMEOUT_MAX){
				timeoutnum = 0;
				log.warn("BCM timeout!");
			}
		}

		//整车控制接口
		private void Tx_BcmControl(int Addr){//m_rearfog_light、m_main_light1、m_main_light2需要数据
			for(int i=0;i<m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].length;i++){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][i] = 0;
			}
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] = (short)(
					m_BcmInfo.m_getcarcontrol.m_baggage_light << 7 |
					m_BcmInfo.m_getcarcontrol.m_read_light << 6 |
					m_BcmInfo.m_getcarcontrol.m_mainchange_light << 5 |
//					m_BcmInfo.m_setcarcontrol.m_main_light1 << 4 |
					m_BcmInfo.m_getcarcontrol.m_midtv_light << 3 |
//					m_BcmInfo.m_setcarcontrol.m_main_light2 << 5 |
					m_BcmInfo.m_getcarcontrol.m_fronttv_light << 1 |
					m_BcmInfo.m_getcarcontrol.m_horn_transfer);
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][1] = (short)(
					m_BcmInfo.m_getcarcontrol.m_ecas_reset << 7 |
//					m_BcmInfo.m_setcarcontrol.m_rearfog_light << 6 |
					m_BcmInfo.m_getcarcontrol.m_veneer_light << 5 |
					m_BcmInfo.m_getcarcontrol.m_atmosphere_light2 << 4 |
					m_BcmInfo.m_getcarcontrol.m_atmosphere_light1 << 3 |
					m_BcmInfo.m_getcarcontrol.m_glass_defrost << 2 |
					m_BcmInfo.m_getcarcontrol.m_drivertop_light << 1 |
					m_BcmInfo.m_getcarcontrol.m_aisle_light);
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][2] = (short)(
					m_BcmInfo.m_getcarcontrol.m_frontfog_light << 7 |
					m_BcmInfo.m_getcarcontrol.m_driver_fan << 6 |
					m_BcmInfo.m_getcarcontrol.m_scuttle_in << 5 |
					m_BcmInfo.m_getcarcontrol.m_scuttle_out << 4 |
					m_BcmInfo.m_getcarcontrol.m_ecas_down << 3 |
					m_BcmInfo.m_getcarcontrol.m_ecas_up << 2 |
					m_BcmInfo.m_getcarcontrol.m_ecas_kneelside << 1 |
					m_BcmInfo.m_getcarcontrol.m_heightII);
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][3] = (short) m_BcmInfo.m_getcarcontrol.m_mainchange_light_per;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][4] = (short)(
					m_BcmInfo.m_getcarcontrol.m_frontdecorative_water << 7 |
					m_BcmInfo.m_getcarcontrol.m_signboard << 6 |
					m_BcmInfo.m_getcarcontrol.m_radiator2 << 5 |
					m_BcmInfo.m_getcarcontrol.m_radiator1 << 4);
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] |=  m_BcmInfo.m_setcarcontrol.m_main_light1 << 4 | m_BcmInfo.m_setcarcontrol.m_main_light2 << 5 ;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][1] |=  m_BcmInfo.m_setcarcontrol.m_rearfog_light << 6;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] = (short) m_BcmInfo.m_setcarcontrol.m_byte6;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] = (short) m_BcmInfo.m_setcarcontrol.m_byte7;

			synchronized(this){
				int rate = m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate;
				if(rate==0){
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate=500;
				}/*else{	//发送3帧无效数据
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mEnable=false; //无效数据3帧发送结束后关闭数据发送
					m_protbcm.m_appdata.Send[Addr][BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].mTransRate = (int) m_bcmapp.Send_PngData[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].rate;
				}*/
			}
		}

		//定义几个立刻发送执行的接口
		void Port_BcmImmediatelySend(){
			for(int i=0;i<m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()].length;i++){
				m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][i] = 0;
			}
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][0] |=  m_BcmInfo.m_setcarcontrol.m_main_light1 << 4 | m_BcmInfo.m_setcarcontrol.m_main_light2 << 5 ;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][1] |=  m_BcmInfo.m_setcarcontrol.m_rearfog_light << 6;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][5] = (short) m_BcmInfo.m_setcarcontrol.m_byte6;
			m_bcmapp.m_BcmDataSend_Buff[BCM_TX_PNG.BCM_TX_BCMCONTROL.ordinal()][6] = (short) m_BcmInfo.m_setcarcontrol.m_byte7;
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
	BCM_RX_STATUS,
	BCM_RX_PNG_TOTAL
}

enum BCM_TX_PNG{//发送参数组
	BCM_TX_BCMCONTROL,
	BCM_TX_PNG_TOTAL;
}