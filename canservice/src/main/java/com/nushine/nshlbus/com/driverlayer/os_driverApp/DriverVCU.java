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
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.cfgBmu;
import com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverCfg.cfgPortStack;

public class DriverVCU {
	public boolean VCUDeBug=false;
	public boolean VCUCommStatus=true;//false表示超时
	private int VCU_Change=0;
	public VCU_Info m_VCUInfo;
	private DriverVCUTask m_VCUtcb;

	public DriverVCU(Global_Cfg cfg){
		m_VCUInfo = new VCU_Info(cfg.m_bmu);
		m_VCUtcb = new DriverVCUTask(cfg.m_stack);
	}

	public enum VCU_DNR_CONTROL{
		DNR_P,
		DNR_R,
		DNR_N,
		DNR_D,
		DNR_P_INVALIDE,
		DNR_R_INVALIDE,
		DNR_N_INVALIDE,
		DNR_D_INVALIDE,
		DNR_INVALIDE
	}

	public enum VCU_ENERGY_CONTROL{
		ENERGY_L,
		ENERGY_M,
		ENERGY_H,
		ENERGY_INVALIDE
	}

	public enum VCU_KEYINDEX_CONTROL{
		KEY_OFF,
		KEY_ACC,
		KEY_ON,
		KEY_START,
		KEY_INVALIDE
	}

	public enum VCU_DRIVERMODE_CONTROL{
		DRIVEMODE_ENERGYSAVE,
		DRIVEMODE_SPORT,
		DRIVEMODE_HOTSPORT,//暴躁模式
		DRIVEMODE_NEDC,
		DNR_TOTAL
	}

	public enum VCU_STATUS{
		VCUSTATUS_INVALIDE,
		VCUSTATUS_NORMAL,
		VCUSTATUS_ERR,
		VCUSTATUS_TOTAL
	}

	public enum VCU_WARN_STATUS{
		VCUSTATUS_INVALIDE,
		VCUSTATUS_NORMAL,
		VCUSTATUS_WARN,
		VCUSTATUS_TOTAL
	}

	public enum VCU_BATMOTOR_STATUS{
		VCUSTATUS_INVALIDE,
		VCUSTATUS_NORMAL,
		VCUSTATUS_ONELEVELWARN,
		VCUSTATUS_TWOLEVELWARN,
		VCUSTATUS_THREELEVELWARN,
		VCUSTATUS_TOTAL
	}

	public enum VCU_EPBWARN_STATUS{
		VCUSTATUS_OFF,
		VCUSTATUS_ON,
		VCUSTATUS_FLASH,
		VCUSTATUS_TOTAL
	}

	public enum VCU_BATON_STATUS{
		VCUSTATUS_INVALIDE,
		VCUSTATUS_NOINPLACE,
		VCUSTATUS_INPLACE,
		VCUSTATUS_TOTAL
	}

	public class VCU_Info{//汽车空调基本信息
		//基本信息
		public short m_vender=0;
		public short m_softver=0;
		public short m_hardver=0;
		public short m_VCUVolt_Rtd = 0;
		public short m_VCUCur_Rtd = 0;

		public int m_vcuvincode = 0;

		public int   m_iserror=0;
		public VCU_STATUS m_VCUstatus=VCU_STATUS.VCUSTATUS_NORMAL;
		public VCU_STATUS m_bakVCUstatus=VCU_STATUS.VCUSTATUS_NORMAL;

		public VCU_CONTROL m_setVCUControl;
		public VCU_CONTROL m_getVCUControl;
		public VCU_CONTROL m_BckgetVCUControl;
		public boolean VCU_Change_Flag=false;

		public VCU_StatusPara m_status;
		public VCU_MeterStatusPara m_meterstatus;
		public VCU_MotorStatusPara m_motorstatus;
		public VCU_SocStatusPara m_socstatus;
		public VCU_WarnInfoPara m_warninfo;
		public VCU_BatInPlacePara m_batinplace;
		public VCU_MaxMinCodePara m_maxmincode;
		public VCU_LampStatusPara m_lampstatus;

		public byte[] m_VIN;

		public VCU_Info(cfgBmu cfg) {
			m_setVCUControl=new VCU_CONTROL();
			m_getVCUControl=new VCU_CONTROL();
			m_BckgetVCUControl=new VCU_CONTROL();
			m_status = new VCU_StatusPara();
			m_meterstatus = new VCU_MeterStatusPara();
			m_motorstatus = new VCU_MotorStatusPara();
			m_socstatus = new VCU_SocStatusPara();
			m_warninfo = new VCU_WarnInfoPara();
			m_batinplace = new VCU_BatInPlacePara();
			m_maxmincode = new VCU_MaxMinCodePara();
			m_lampstatus = new VCU_LampStatusPara();
			m_VIN = new byte[cfg.carVIN_length];
		}

	}

	public class VCU_CONTROL{
		public VCU_DNR_CONTROL m_dnr=VCU_DNR_CONTROL.DNR_N;
		public VCU_ENERGY_CONTROL m_ecoenergy=VCU_ENERGY_CONTROL.ENERGY_L;
		public VCU_ENERGY_CONTROL m_sportenergy=VCU_ENERGY_CONTROL.ENERGY_L;
		public VCU_KEYINDEX_CONTROL m_keyindex = VCU_KEYINDEX_CONTROL.KEY_OFF;
		public VCU_DRIVERMODE_CONTROL m_mode=VCU_DRIVERMODE_CONTROL.DRIVEMODE_SPORT;
		public short m_energymode = 0;//所有需要发送的数据(车辆模式能量模式)
	}

	public class VCU_StatusPara{
		public short m_Brk_Anlg = 0;// 刹车踏板开度
		public byte m_Brk_Dgtl = 0;// 刹车踏板开关
		public short m_Acc_Anlg = 0;// 油门踏板开度
		public byte m_Acc_Dgtl = 0;// 油门踏板开关
		public byte m_PowStatus = 0;// 车辆是否READY
		public byte m_PosSafAlarm = 0;// 主动安全报警
		public byte m_ChgGun = 0;// 充点枪是否插入
		public byte m_Chging = 0;// 车辆是否在充电中
		public byte m_VehicleCollision = 0;//碰撞信号
//		public byte m_HiVoltStatus;// 车辆高压状态
//		public short m_VCUFunc;// VCU功能状态
	}

	public class VCU_MeterStatusPara{
		public short m_VehicleSpd = 0;//车速
		public short m_RemaingMileage = 0;//剩余里程
		public short m_Odometer = 0;//累计行驶里程
	}

	public class VCU_MotorStatusPara{
		public int m_MaxAllowChgCur = 0;//最大允许充电电流
		public int m_MaxAllowDischgCur = 0;//最大允许放电电流
		public short m_BatVolt = 0;//电池输出电压
		public short m_BatCur = 0;//电池输出电流
	}

	public class VCU_SocStatusPara{
		public short m_BatSOC = 0;//电池SOC
		public short m_MaxPointTemp = 0;//最高采样点温度
		public short m_MinPointTemp = 0;//最低采样点温度
		public short m_MaxCellVolt = 0;//最高单体电压
		public short m_MinCellVolt = 0;//最低单体电压
	}

	public class VCU_WarnInfoPara {
		// 电池单体电压低一级报警;电池单体电压低二级报警;电池采样点温度高一级报警;电池采样点温度低一级报警
		public VCU_WARN_STATUS[] m_baterr = new VCU_WARN_STATUS[4];
		// 电机报警信息;电池报警信息
		public VCU_BATMOTOR_STATUS[] m_batandmotor = new VCU_BATMOTOR_STATUS[2];
		public VCU_BATMOTOR_STATUS m_ResAlarm = VCU_BATMOTOR_STATUS.VCUSTATUS_INVALIDE;
		// 充电机报警信息;空调报警信息;车身控制器报警信息;ABS报警信息;DCDC报警信息;助力转向报警信息
		public VCU_WARN_STATUS[] m_baterr2 = new VCU_WARN_STATUS[4];
		public VCU_EPBWARN_STATUS m_EpbErr = VCU_EPBWARN_STATUS.VCUSTATUS_ON;
		// 车速报警信息;加速踏板报警信息;刹车踏板报警信息;档位报警信息;蓄电池电压低报警;VCU系统报警;
		// 高压断开报警;电池就位数不足报警;放电电流过大报警;电机或者控制器过温报警;电芯厂家不同报警
		public VCU_WARN_STATUS[] m_baterr3 = new VCU_WARN_STATUS[8];
		public VCU_WARN_STATUS[] m_baterr4 = new VCU_WARN_STATUS[3];

		VCU_WarnInfoPara(){
			for(int i=0;i<m_baterr.length;i++){
				m_baterr[i] = VCU_WARN_STATUS.VCUSTATUS_INVALIDE;
			}
			for(int i=0;i<m_baterr2.length;i++){
				m_baterr2[i] = VCU_WARN_STATUS.VCUSTATUS_INVALIDE;
			}
			for(int i=0;i<m_baterr3.length;i++){
				m_baterr3[i] = VCU_WARN_STATUS.VCUSTATUS_INVALIDE;
			}
			for(int i=0;i<m_batandmotor.length;i++){
				m_batandmotor[i] = VCU_BATMOTOR_STATUS.VCUSTATUS_INVALIDE;
			}
		}
	}

	public class VCU_BatInPlacePara{
		public short m_BatSOH = 0;//电池SOH
		public short m_BatOnNum = 0;//电池就位数
		public short m_FBBatOn = 0;
		public short m_RemainingCpct = 0;//剩余电量
		public short m_VcuChgCpct = 0;//本次充电电量
	}

	public class VCU_MaxMinCodePara{
		public short m_MaxVoltInBatNum = 0;
		public short m_MaxVoltInCellNum = 0;
		public short m_MinVoltInBatNum = 0;
		public short m_MinVoltInCellNum = 0;
		public short m_MaxTempInBatNum = 0;
		public short m_MaxTempInCellNum = 0;
		public short m_MinTempInBatNum = 0;
		public short m_MinTempInCellNum = 0;
	}

	public class VCU_LampStatusPara{
		public short m_LeftHeadLightTemp = 0;//左大灯检测点温度
		public short m_RightHeadLightTemp = 0;//右大灯检测点温度
		public short m_FrontFuseBoxTemp = 0;//前保险盒检测点温度
		public short m_SlowChgSocketTemp = 0;//慢充插座温度
		//左大灯过温一级报警;左大灯过温二级报警;右大灯过温一级报警;右大灯过温二级报警;
		//前保险丝盒过温一级报警;前保险丝盒过温二级报警;慢充插座过温一级报警;慢充插座过温二级报警
		public byte[] m_lamperr = new byte[8];

		VCU_LampStatusPara(){
			for(int i=0;i<m_lamperr.length;i++){
				m_lamperr[i] = 0;
			}
		}
	}

	public void Change_VCUControlSendCycle(){
		m_VCUtcb.Port_VCUImmediatelySend();
	}

	//BCU协议栈
	class Driver_VCU_DataBuf{
		//协议栈
		final VCUConst_PARA[] Recv_PngData = {
				new VCUConst_PARA(0xAA,0x06,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//设备基本信息
				new VCUConst_PARA(0xAA,0x08,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//VCU额定电压电流
				new VCUConst_PARA(0xAA,0x10,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,20),//VCU基本状态信息
				new VCUConst_PARA(0xAA,0x11,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//VCU车速里程
				new VCUConst_PARA(0xAA,0x12,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,20),//VCU电机状态
				new VCUConst_PARA(0xAA,0x13,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//VCUsoc状态
				new VCUConst_PARA(0xAA,0x15,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//VCU电池就位
				new VCUConst_PARA(0xAA,0x16,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,500),//VCU单体编号
				new VCUConst_PARA(0xAA,0x18,0xFF,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,20),//lampstatus
				new VCUConst_PARA(0xAA,0xFB,0x02,6,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,5000),//设备唯一码
		};
		final VCUConst_PARA[] Send_PngData = {
				new VCUConst_PARA(0xF0,0x50,0xAA,4,0,J1939_FrameType.CAN_FRAMTYPE_EXT,DriverTransType.TT_CYCLE,100),//电机控制器控制信息
		};
		//收发原始数据缓存定义
		final  byte CONST_VCU_DATALEN=8;//数据长度
		final  short MAX_RECVBUFF_SIZE=512;//每个png可以存储的报文数据量
		int[] VCU_RecvGetAddr = new int[VCU_RX_PNG.VCU_RX_PNG_TOTAL.ordinal()];//接收缓存读指针
		int[] VCU_RecvSetAddr = new int[VCU_RX_PNG.VCU_RX_PNG_TOTAL.ordinal()];//接收缓存写指针
		short[][][] m_VCUDataRcev_Buff = new short[VCU_RX_PNG.VCU_RX_PNG_TOTAL.ordinal()][MAX_RECVBUFF_SIZE][CONST_VCU_DATALEN];
		short[][] m_VCUDataSend_Buff = new short[VCU_TX_PNG.VCU_TX_PNG_TOTAL.ordinal()][CONST_VCU_DATALEN];//每帧数据的发送缓存只需要分配一个空间

		Driver_VCU_DataBuf(){
			//数据收发缓存初始化，收发指针初始化
			for(int i=0;i<VCU_RecvGetAddr.length;i++){
				VCU_RecvGetAddr[i]=0;
			}
			for(int i=0;i<VCU_RecvSetAddr.length;i++){
				VCU_RecvSetAddr[i]=0;
			}
			for(int i=0;i<m_VCUDataRcev_Buff.length;i++)
				for(int j=0;j<m_VCUDataRcev_Buff[i].length;j++)
					for(int k=0;k<m_VCUDataRcev_Buff[i][j].length;k++){
						m_VCUDataRcev_Buff[i][j][k]=0;
					}
			for(int i=0;i<m_VCUDataSend_Buff.length;i++)
				for(int j=0;j<m_VCUDataSend_Buff[i].length;j++){
					m_VCUDataSend_Buff[i][j] = 0;
				}
		}

		//从接收缓存中读取一个数据
		public  short[]  VCU_GetRecvBuffer(int png){
			short[] data;
			if(VCU_RecvGetAddr[png]==VCU_RecvSetAddr[png]){
				return null;
			}else{
				synchronized(this){
					data = m_VCUDataRcev_Buff[png][VCU_RecvGetAddr[png]].clone();
					VCU_RecvGetAddr[png] = (short) ((VCU_RecvGetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return data;
		}
		//接收数据写入缓存
		public  boolean Can_SetRecvBuffer(short[] data,int png)
		{
			if(((VCU_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE)==VCU_RecvGetAddr[png]){
				return false;
			}else{
				synchronized(this){
					m_VCUDataRcev_Buff[png][VCU_RecvSetAddr[png]] = null;
					m_VCUDataRcev_Buff[png][VCU_RecvSetAddr[png]] = data.clone();
					VCU_RecvSetAddr[png] = (short) ((VCU_RecvSetAddr[png]+1)%MAX_RECVBUFF_SIZE);
				}
			}
			return true;
		}
	}

	//协议解析类
	class DriverVCUTask extends DriverCallPro{
		final short VCUApp_Addr_Start = 0xFF;
		final short VCUApp_Addr_Code = 0x02;	//code接收默认
		final short VCU_Num = 1;
		Logger log = Logger.getLogger(DriverVCUTask.class);
		final Driver_VCU_DataBuf m_VCUapp = new Driver_VCU_DataBuf();//协议栈定义
		DriverProtocolStack m_protVCU; //定义一个应用堆栈

		DriverVCUTask(cfgPortStack stack) {
			//协议栈初始化
			InitAppData();
			m_protVCU.m_callpro = this;
			if(stack.m_port.crateProtApp(m_protVCU)){
				log.debug("DriverVCUTask is create success!");
			}else{
				log.debug("DriverVCUTask is create failed!");
			}
		}

		void InitAppData(){
			m_protVCU = new DriverProtocolStack(VCU_Num,(short) m_VCUapp.Send_PngData.length,(short) m_VCUapp.Recv_PngData.length);
			m_protVCU.m_appdata.mHardType = DriverHardType.DH_CAN;//can设备
			for(int i=0;i<m_protVCU.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protVCU.m_appdata.TotalRecvPara;j++){
					m_protVCU.m_appdata.Recv[i][j].mDataMaxLen = m_VCUapp.CONST_VCU_DATALEN;
					m_protVCU.m_appdata.Recv[i][j].mDataLen = m_VCUapp.CONST_VCU_DATALEN;
					m_protVCU.m_appdata.Recv[i][j].mSource = (byte) m_VCUapp.Recv_PngData[j].sa;
					m_protVCU.m_appdata.Recv[i][j].mTransRate = (int) m_VCUapp.Recv_PngData[j].rate;
					m_protVCU.m_appdata.Recv[i][j].mTimer = 0;
					m_protVCU.m_appdata.Recv[i][j].mEnable = true;
					m_protVCU.m_appdata.Recv[i][j].mPGState = DriverPgStatus.PG_RX_FREE;
					m_protVCU.m_appdata.Recv[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protVCU.m_appdata.Recv[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
			for(int i=0;i<m_protVCU.m_appdata.TotalAddr;i++)
				for(int j=0;j<m_protVCU.m_appdata.TotalSendPara;j++){
					m_protVCU.m_appdata.Send[i][j].mDataMaxLen = m_VCUapp.CONST_VCU_DATALEN;
					m_protVCU.m_appdata.Send[i][j].mDataLen = m_VCUapp.CONST_VCU_DATALEN;
					m_protVCU.m_appdata.Send[i][j].mSource = (short) m_VCUapp.Send_PngData[j].sa;
					m_protVCU.m_appdata.Send[i][j].mTransRate = (int) m_VCUapp.Send_PngData[j].rate;
					m_protVCU.m_appdata.Send[i][j].mTimer = 0;
					m_protVCU.m_appdata.Send[i][j].mEnable = false;
					m_protVCU.m_appdata.Send[i][j].mPGState = DriverPgStatus.PG_TX_TX;
					m_protVCU.m_appdata.Send[i][j].mTransType = DriverTransType.TT_CYCLE;
					m_protVCU.m_appdata.Send[i][j].mHardNum = CanDeviceIndex.CAN_NO1;
				}
		}
		//接收回调接口
		public boolean RxSourceData(J1939FrameFormat j1939){
			int m_rxnum = VCU_RX_PNG.VCU_RX_PNG_TOTAL.ordinal();
			//判断报文是否属于BCU
			for(int i = 0; i <m_rxnum;i++){
				if(j1939.PS == m_VCUapp.Recv_PngData[i].ps
						&& j1939.PF == m_VCUapp.Recv_PngData[i].pf
						&& j1939.FramType == m_VCUapp.Recv_PngData[i].framtype){
					if(j1939.Addr >= m_VCUapp.Recv_PngData[i].sa){
						short addr = (short) (j1939.Addr - m_VCUapp.Recv_PngData[i].sa);
						if(addr >= VCU_Num || addr <0){
							return false;
						}
						if(m_protVCU.m_appdata.Recv[addr][i].mEnable){
							if(j1939.DtaLen <= m_protVCU.m_appdata.Recv[addr][i].mDataMaxLen){
								m_VCUapp.Can_SetRecvBuffer(j1939.pDta,(byte)i);
								m_protVCU.m_appdata.Recv[addr][i].mDataLen = (byte) j1939.DtaLen;
								m_protVCU.m_appdata.Recv[addr][i].mPGState = DriverPgStatus.PG_RX_FULL;
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
			if(wpara == VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()){
				Tx_DNRControl(lpara);
			}
		}

		@Override
		public boolean rxFrame(int lpara, int wpara) {
			// TODO Auto-generated method stub
			short[] m_data = new short[8];
			m_data = m_VCUapp.VCU_GetRecvBuffer(wpara);
			if(m_data==null || VCUDeBug){	//调试模式下不接受CAN总线数据
				return false;
			}

			if(wpara == VCU_RX_PNG.VCU_RX_BASEINFO.ordinal()){
				Rx_VCUBaseInfo(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_RATEDINFO.ordinal()){
				Rx_VCURatedInfo(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_STATUS.ordinal()){
				Rx_VCUStatus(m_data,lpara);
				timeoutnum = 0;
			}else if(wpara == VCU_RX_PNG.VCU_RX_METERSTATUS.ordinal()){
				Rx_VCUMeterStatus(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_MOTORSTATUS.ordinal()){
				Rx_VCUMotorStatus(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_SOCSTATUS.ordinal()){
				Rx_VCUSocStatus(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_BATINPLACE.ordinal()){
				Rx_VCUBatInPlace(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_MAXMINCODE.ordinal()){
				Rx_VCUMaxMinCode(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_LAMPSTATUS.ordinal()){
				Rx_VCULampStatus(m_data,lpara);
			}else if(wpara == VCU_RX_PNG.VCU_RX_VINCODE.ordinal()){
				Rx_VCUVincode(m_data,lpara);
			}else{
				return false;
			}
			return true;
		}

		@Override
		public void rxTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub
			if(wpara == VCU_RX_PNG.VCU_RX_STATUS.ordinal()){
				Rx_VCUTimeOut(lpara);
			}
		}

		@Override
		public void txTimeout(int lpara, int wpara) {
			// TODO Auto-generated method stub

		}

		@Override
		public J1939FrameFormat SendFrame(int lpara, int wpara) {//CAN数据发送接口
			// TODO Auto-generated method stub
			if(wpara>VCU_TX_PNG.VCU_TX_PNG_TOTAL.ordinal()){
				return null;
			}

			J1939FrameFormat m_send = new J1939FrameFormat();
			m_send.FramType = m_VCUapp.Send_PngData[wpara].framtype;//DriverBcuTask.m_protbcu.J1939_FrameType;
			m_send.PF = m_VCUapp.Send_PngData[wpara].pf;
			m_send.PS = m_VCUapp.Send_PngData[wpara].ps;
			m_send.Addr = m_protVCU.m_appdata.Send[lpara][wpara].mSource;
			m_send.Prior = m_VCUapp.Send_PngData[wpara].prior;
			m_send.Page = m_VCUapp.Send_PngData[wpara].page;
			m_send.DtaLen = m_VCUapp.CONST_VCU_DATALEN;
			m_send.pDta = m_VCUapp.m_VCUDataSend_Buff[wpara].clone();
			for(int i=0;i<m_VCUapp.m_VCUDataSend_Buff[wpara].length;i++){
				m_VCUapp.m_VCUDataSend_Buff[wpara][i]=0;
			}
			return m_send;
		}

		/****************数据接收处理****************/
		private void Rx_VCUBaseInfo(short[] pdta, int Addr) {
			m_VCUInfo.m_vender = pdta[0];
			m_VCUInfo.m_hardver = (short) ((pdta[2]&0xff)<<8|(pdta[3]&0xff));
			m_VCUInfo.m_softver = (short) ((pdta[4]&0xff)<<8|(pdta[5]&0xff));
		}

		private void Rx_VCURatedInfo(short[] pdta,int Addr){
			m_VCUInfo.m_VCUVolt_Rtd = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_VCUInfo.m_VCUCur_Rtd = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
		}

		byte[] vincode = new byte[17];
		int vincounter = 2;
		private void Rx_VCUVincode(short[] pdta,int Addr){
			int Framtype = pdta[0];
			if(Framtype == 1){
				m_VCUInfo.m_vcuvincode = (pdta[6]&0xff)<<32|(pdta[5]&0xff)<<16|(pdta[4]&0xff)<<8|(pdta[3]&0xff);
			}else if((Framtype == 2)&&(vincounter==2)){
				vincounter = 3;
				vincode[0]=(byte) pdta[1];
				vincode[1]=(byte) pdta[2];
				vincode[2]=(byte) pdta[3];
				vincode[3]=(byte) pdta[4];
				vincode[4]=(byte) pdta[5];
				vincode[5]=(byte) pdta[6];
				vincode[6]=(byte) pdta[7];
			}else if((Framtype == 3)&&(vincounter==3)){
				vincounter = 4;
				vincode[7]=(byte) pdta[1];
				vincode[8]=(byte) pdta[2];
				vincode[9]=(byte) pdta[3];
				vincode[10]=(byte) pdta[4];
				vincode[11]=(byte) pdta[5];
				vincode[12]=(byte) pdta[6];
				vincode[13]=(byte) pdta[7];
			}else if((Framtype == 4)&&(vincounter==4)){
				vincounter=2;
				vincode[14]=(byte) pdta[1];
				vincode[15]=(byte) pdta[2];
				vincode[16]=(byte) pdta[3];
				System.arraycopy(vincode, 0, m_VCUInfo.m_VIN, 0, m_VCUInfo.m_VIN.length);
			}
		}

		private void Rx_VCUStatus(short[] pdta,int Addr){

			m_VCUInfo.m_status.m_Brk_Anlg = (short) ((((pdta[0]&0xff) << 8) | (pdta[1]&0xff)) & 0x0fff);
			m_VCUInfo.m_status.m_Brk_Dgtl = (byte) ((((pdta[0]&0xff) << 8) | (pdta[1]&0xff))>>12 & 0x01);
			m_VCUInfo.m_status.m_Acc_Anlg = (short) ((((pdta[2]&0xff) << 8) | (pdta[3]&0xff)) & 0x0fff);
			m_VCUInfo.m_status.m_Acc_Dgtl = (byte) ((((pdta[2]&0xff) << 8) | (pdta[3]&0xff))>>12  & 0x01);

			if ((pdta[7]&0x0F) == 0x00) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_P;
			} else if ((pdta[7]&0x0F) == 0x01) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_R;
			} else if ((pdta[7]&0x0F) == 0x02) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_N;
			} else if ((pdta[7]&0x0F) == 0x04) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_D;
			} else if ((pdta[7]&0x0F) == 0x05) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_P_INVALIDE;
			} else if ((pdta[7]&0x0F) == 0x06) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_R_INVALIDE;
			} else if ((pdta[7]&0x0F) == 0x07) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_N_INVALIDE;
			} else if ((pdta[7]&0x0F) == 0x08) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_D_INVALIDE;
			} else if ((pdta[7]&0x0F) == 0x0f) {
				m_VCUInfo.m_getVCUControl.m_dnr = VCU_DNR_CONTROL.DNR_INVALIDE;
			}

			if ((pdta[7]>>4&0x03) == 0x00) {
				m_VCUInfo.m_getVCUControl.m_ecoenergy = VCU_ENERGY_CONTROL.ENERGY_L;
			} else if ((pdta[7]>>4&0x03) == 0x01) {
				m_VCUInfo.m_getVCUControl.m_ecoenergy = VCU_ENERGY_CONTROL.ENERGY_M;
			} else if ((pdta[7]>>4&0x03) == 0x02) {
				m_VCUInfo.m_getVCUControl.m_ecoenergy = VCU_ENERGY_CONTROL.ENERGY_H;
			} else if ((pdta[7]>>4&0x03) == 0x03) {
				m_VCUInfo.m_getVCUControl.m_ecoenergy = VCU_ENERGY_CONTROL.ENERGY_INVALIDE;
			}

			if ((pdta[7]>>6&0x03) == 0x00) {
				m_VCUInfo.m_getVCUControl.m_sportenergy = VCU_ENERGY_CONTROL.ENERGY_L;
			} else if ((pdta[7]>>6&0x03) == 0x01) {
				m_VCUInfo.m_getVCUControl.m_sportenergy = VCU_ENERGY_CONTROL.ENERGY_M;
			} else if ((pdta[7]>>6&0x03) == 0x02) {
				m_VCUInfo.m_getVCUControl.m_sportenergy = VCU_ENERGY_CONTROL.ENERGY_H;
			} else if ((pdta[7]>>6&0x03) == 0x03) {
				m_VCUInfo.m_getVCUControl.m_sportenergy = VCU_ENERGY_CONTROL.ENERGY_INVALIDE;
			}

			if (((pdta[4] & 0x38) >> 3) == 0x00) {
				m_VCUInfo.m_getVCUControl.m_keyindex = VCU_KEYINDEX_CONTROL.KEY_OFF;
			} else if (((pdta[4] & 0x38) >> 3) == 0x01) {
				m_VCUInfo.m_getVCUControl.m_keyindex = VCU_KEYINDEX_CONTROL.KEY_ACC;
			} else if (((pdta[4] & 0x38) >> 3) == 0x02) {
				m_VCUInfo.m_getVCUControl.m_keyindex = VCU_KEYINDEX_CONTROL.KEY_ON;
			} else if (((pdta[4] & 0x38) >> 3) == 0x04) {
				m_VCUInfo.m_getVCUControl.m_keyindex = VCU_KEYINDEX_CONTROL.KEY_START;
			}

			if(((pdta[4]>>6)&0x01) == 0x00){
				m_VCUInfo.m_status.m_PowStatus = 0;
			}else if(((pdta[4]>>6)&0x01) == 0x01){
				m_VCUInfo.m_status.m_PowStatus = 1;
			}

			if((pdta[5]&0x01) == 0x00){
				m_VCUInfo.m_status.m_PosSafAlarm = 0;
			}else if((pdta[5]&0x01) == 0x01){
				m_VCUInfo.m_status.m_PosSafAlarm = 1;
			}

			if(((pdta[5] >> 1) & 0x01) == 0x00){
				m_VCUInfo.m_status.m_ChgGun = 0;
			}else if(((pdta[5] >> 1) & 0x01) == 0x01){
				m_VCUInfo.m_status.m_ChgGun = 1;
			}

			if(((pdta[5] >> 2) & 0x01) == 0x00){
				m_VCUInfo.m_status.m_Chging = 0;
			}else if(((pdta[5] >> 2) & 0x01) == 0x01){
				m_VCUInfo.m_status.m_Chging = 1;
			}

			if(((pdta[5] >> 3) & 0x01) == 0x00){
				m_VCUInfo.m_status.m_VehicleCollision = 0;
			}else{
				m_VCUInfo.m_status.m_VehicleCollision = 1;
			}

			if (((pdta[5] >> 4)&0x03) == 0x00) {
				m_VCUInfo.m_getVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_ENERGYSAVE;
			} else if (((pdta[5] >> 4)&0x03) == 0x01) {
				m_VCUInfo.m_getVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_SPORT;
			} else if (((pdta[5] >> 4)&0x03) == 0x02) {
				m_VCUInfo.m_getVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_HOTSPORT;
			} else if (((pdta[5] >> 4)&0x03) == 0x03) {
				m_VCUInfo.m_getVCUControl.m_mode = VCU_DRIVERMODE_CONTROL.DRIVEMODE_NEDC;
			}

		}

		private void Rx_VCUMeterStatus(short[] pdta,int Addr){
			m_VCUInfo.m_meterstatus.m_VehicleSpd = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			if(m_VCUInfo.m_meterstatus.m_VehicleSpd < 0){
				m_VCUInfo.m_meterstatus.m_VehicleSpd = 0;
			}
			m_VCUInfo.m_meterstatus.m_RemaingMileage = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
			if(m_VCUInfo.m_meterstatus.m_RemaingMileage < 0){
				m_VCUInfo.m_meterstatus.m_RemaingMileage = 0;
			}
			m_VCUInfo.m_meterstatus.m_Odometer = (short) ((pdta[6]&0xff)<<16|(pdta[5]&0xff)<<8|(pdta[4]&0xff));
			if(m_VCUInfo.m_meterstatus.m_Odometer < 0){
				m_VCUInfo.m_meterstatus.m_Odometer = 0;
			}
		}

		private void Rx_VCUMotorStatus(short[] pdta,int Addr){
			m_VCUInfo.m_motorstatus.m_MaxAllowChgCur = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_VCUInfo.m_motorstatus.m_MaxAllowDischgCur = (short) ((pdta[3]&0xff)<<8|(pdta[2]&0xff));
			m_VCUInfo.m_motorstatus.m_BatVolt = (short) ((pdta[5]&0xff)<<8|(pdta[4]&0xff));
			m_VCUInfo.m_motorstatus.m_BatCur = (short) ((pdta[7]&0xff)<<8|(pdta[6]&0xff));
		}

		private void Rx_VCUSocStatus(short[] pdta,int Addr){
			m_VCUInfo.m_socstatus.m_BatSOC = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_VCUInfo.m_socstatus.m_MaxPointTemp = pdta[2];
			m_VCUInfo.m_socstatus.m_MinPointTemp = pdta[3];
			m_VCUInfo.m_socstatus.m_MaxCellVolt = (short) ((pdta[5]&0xff)<<8|(pdta[4]&0xff));
			m_VCUInfo.m_socstatus.m_MinCellVolt = (short) ((pdta[7]&0xff)<<8|(pdta[6]&0xff));
		}

		private void Rx_VCUBatInPlace(short[] pdta,int Addr){
			m_VCUInfo.m_batinplace.m_BatSOH = (short) ((pdta[1]&0xff)<<8|(pdta[0]&0xff));
			m_VCUInfo.m_batinplace.m_BatOnNum = pdta[2];
			m_VCUInfo.m_batinplace.m_FBBatOn = pdta[3];
			m_VCUInfo.m_batinplace.m_RemainingCpct = pdta[4];
			m_VCUInfo.m_batinplace.m_VcuChgCpct = pdta[5];
		}

		private void Rx_VCUMaxMinCode(short[] pdta,int Addr){
			m_VCUInfo.m_maxmincode.m_MaxVoltInBatNum = pdta[0];
			m_VCUInfo.m_maxmincode.m_MaxVoltInCellNum = pdta[1];
			m_VCUInfo.m_maxmincode.m_MinVoltInBatNum = pdta[2];
			m_VCUInfo.m_maxmincode.m_MinVoltInCellNum = pdta[3];
			m_VCUInfo.m_maxmincode.m_MaxTempInBatNum = pdta[4];
			m_VCUInfo.m_maxmincode.m_MaxTempInCellNum = pdta[5];
			m_VCUInfo.m_maxmincode.m_MinTempInBatNum = pdta[6];
			m_VCUInfo.m_maxmincode.m_MinTempInCellNum = pdta[7];
		}

		private void Rx_VCULampStatus(short[] pdta,int Addr){
			m_VCUInfo.m_lampstatus.m_LeftHeadLightTemp = pdta[0];
			m_VCUInfo.m_lampstatus.m_RightHeadLightTemp = pdta[1];
			m_VCUInfo.m_lampstatus.m_FrontFuseBoxTemp = pdta[2];
			m_VCUInfo.m_lampstatus.m_SlowChgSocketTemp = pdta[3];
			int len = m_VCUInfo.m_lampstatus.m_lamperr.length;
			for(int i=0;i<len;i++){
				m_VCUInfo.m_lampstatus.m_lamperr[i] = (byte) ((pdta[4]>>i)&0x01);
			}
		}

		boolean flag = false;
		//档位控制接口
		private void Tx_DNRControl(int Addr){

//			if(m_VCUInfo.m_setVCUControl.m_dnr==VCU_DNR_CONTROL.DNR_D){
//				m_VCUapp.m_VCUDataSend_Buff[VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()][0]=0x55;
//			}else if(m_VCUInfo.m_setVCUControl.m_dnr==VCU_DNR_CONTROL.DNR_N){
//				m_VCUapp.m_VCUDataSend_Buff[VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()][0]=0xff;
//			}else if(m_VCUInfo.m_setVCUControl.m_dnr==VCU_DNR_CONTROL.DNR_R){
//				m_VCUapp.m_VCUDataSend_Buff[VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()][0]=0xaa;
//			}

			m_VCUapp.m_VCUDataSend_Buff[VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()][0]=m_VCUInfo.m_setVCUControl.m_energymode;

			synchronized(this){
				int rate = m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate;
				if(rate==0){
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=1;
				}else if(rate==1){
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=20;
				}else if(rate==20){
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=30;
				}else if(rate==30){
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=31;
					if((m_VCUInfo.m_setVCUControl.m_energymode & 0x07) == 0x03){
						m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=450;
						flag = false;
					}else if((m_VCUInfo.m_setVCUControl.m_energymode & 0x07) == 0x04){
						m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=450;
						flag = false;
					}
				}else if(rate==450){
					if(flag){
						if(m_VCUInfo.m_getVCUControl.m_mode == VCU_DRIVERMODE_CONTROL.DRIVEMODE_ENERGYSAVE){
							m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=21;
						}else if(m_VCUInfo.m_getVCUControl.m_mode == VCU_DRIVERMODE_CONTROL.DRIVEMODE_SPORT){
							m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate=21;
						}
					}else{
						flag = true;
					}
				}else{	//关闭数据发送
					VCU_Change = 0;
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mEnable = false;
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mPGState = DriverPgStatus.PG_TX_FREE;
					m_protVCU.m_appdata.Send[Addr][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate = (int) m_VCUapp.Send_PngData[VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].rate;
				}
			}
		}

		byte timeoutnum=0;
		private void Rx_VCUTimeOut(int Addr){
			final byte VCU_TIMEOUT_MAX = 3;	//bcu通讯3次超时，判定为通讯超时
			timeoutnum++;
			if(timeoutnum>VCU_TIMEOUT_MAX){
				timeoutnum = 0;
				m_VCUInfo.m_VCUstatus = VCU_STATUS.VCUSTATUS_ERR;
				//VCU_CheckChange();
				VCUCommStatus = false;
				log.warn("VCU timeout!");
			}
		}

		//定义几个立刻发送执行的接口
		void Port_VCUImmediatelySend(){
			for(int i=0;i<m_protVCU.m_appdata.TotalAddr;i++){
				synchronized(this){
					m_protVCU.m_appdata.Send[i][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mEnable = true;
					m_protVCU.m_appdata.Send[i][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mTransRate =0;
					m_protVCU.m_appdata.Send[i][VCU_TX_PNG.VCU_TX_DNRCONTROL.ordinal()].mPGState = DriverPgStatus.PG_TX_TX;
				}
			}
		}
	}
}

class VCUConst_PARA{
	short pf;
	short ps;
	short sa;
	short prior;
	short page;
	J1939_FrameType framtype;
	DriverTransType transtype;
	int  rate;
	VCUConst_PARA(	int i,
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

enum VCU_RX_PNG{//接收参数组
	VCU_RX_BASEINFO,
	VCU_RX_RATEDINFO,
	VCU_RX_STATUS,
	VCU_RX_METERSTATUS,
	VCU_RX_MOTORSTATUS,
	VCU_RX_SOCSTATUS,
	VCU_RX_BATINPLACE,
	VCU_RX_MAXMINCODE,
	VCU_RX_LAMPSTATUS,
	VCU_RX_VINCODE,
	VCU_RX_PNG_TOTAL
}

enum VCU_TX_PNG{//发送参数组
	VCU_TX_DNRCONTROL,
	VCU_TX_PNG_TOTAL;
}