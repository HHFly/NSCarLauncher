/*ver:0.0.1
 *bref:J1939协议的实现
 */
package com.nushine.nshlbus.com.driverlayer.os_driverJ1939;

import org.apache.log4j.Logger;

import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.Can;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanDeviceIndex;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanRxFrame;
import com.nushine.nshlbus.com.driverlayer.os_driverHard.Can.CanTxFrame;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_DataType;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_Error;
import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_FrameType;
import com.nushine.nshlbus.com.driverlayer.os_driverTime.DriverTimeEngine;
public class DriverJ1939 extends J1939ValueDefine {
	Logger log = Logger.getLogger(DriverJ1939.class);
	public  Can m_can;	//用于启动CAN设备
	public DriverJ1939(){//初始化J1939协议栈
		m_can = new Can();
		J1939_TransPG= new j1939TransPg[J1939_TRANS_MAX];
		for(int i=0;i<J1939_TRANS_MAX;i++){
			J1939_TransPG[i] = new j1939TransPg();
		}
		J1939_Trans_Init();
	}
	public void DriverJ1939Close(){
		m_can.canClose();
	}
	void J1939_Trans_Init(){ //传输初始化
		for(byte i = 0; i < J1939_TRANS_MAX; i++){
			J1939_TransPG[i].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
		}
	}

	boolean J1939_Trans_SendRTS(short pg){
		CanTxFrame  m_msg= new CanTxFrame();
		m_msg.IDE = J1939_FrameType.CAN_FRAMTYPE_EXT;
		m_msg.RTR = J1939_DataType.CAN_RTR_DATA;
		m_msg.ExtId = 0x18EC0000 | (((int)J1939_TransPG[pg].PGN[0])<<8) | J1939_ADDR;
		m_msg.Can_DLC = J1939_TRANS_PKG_LEN;
		m_msg.mData[0] = J1939_TPCMD_RTS;
		m_msg.mData[1] = (short)(J1939_TransPG[pg].TotalLen >>8);
		m_msg.mData[2] = (short)(J1939_TransPG[pg].TotalLen);
		m_msg.mData[3] = J1939_TransPG[pg].TotalFramNum;
		m_msg.mData[4] = 0xFF;
		m_msg.mData[5] = J1939_TransPG[pg].PGN[2];
		m_msg.mData[6] = J1939_TransPG[pg].PGN[1];
		m_msg.mData[7] = J1939_TransPG[pg].PGN[0];
		return m_can.can_SendMsg(J1939_TransPG[pg].CanNum, m_msg);
	}

	boolean J1939_Trans_Abort(short pg)
	{
		CanTxFrame msg=new CanTxFrame();
		msg.IDE = J1939_FrameType.CAN_FRAMTYPE_EXT;
		msg.RTR = J1939_DataType.CAN_RTR_DATA;
		msg.ExtId = 0x18EC0000 | (((int)J1939_TransPG[pg].PGN[0])<<8) | J1939_ADDR;
		msg.Can_DLC = J1939_TRANS_PKG_LEN;
		msg.mData[0] = J1939_TPCMD_ABORT;
		msg.mData[1] = 0xFF;
		msg.mData[2] = 0xFF;
		msg.mData[3] = 0xFF;
		msg.mData[4] = 0xFF;
		msg.mData[5] = J1939_TransPG[pg].PGN[2];
		msg.mData[6] = J1939_TransPG[pg].PGN[1];
		msg.mData[7] = J1939_TransPG[pg].PGN[0];
		if(m_can.can_SendMsg(J1939_TransPG[pg].CanNum, msg)){
			J1939_TransPG[pg].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
			return true;
		}else{
			return false;
		}
	}

	J1939_Error J1939_Trans_SendDT(short pg)
	{
		CanTxFrame msg=new CanTxFrame();
		short CurSendP;
		@SuppressWarnings("unused")
		short CurSendLen;

		msg.IDE = J1939_FrameType.CAN_FRAMTYPE_EXT;
		msg.RTR = J1939_DataType.CAN_RTR_DATA;
		msg.ExtId = (((int)J1939_TransPG[pg].PGN[2])<<24) | (((int)J1939_TransPG[pg].PGN[1])<<16)
				| (((int)J1939_TransPG[pg].PGN[0])<<8) | J1939_ADDR;
		for(int i=0;i<msg.mData.length;i++){
			msg.mData[i]=0;
		}
		msg.mData[0] = J1939_TransPG[pg].NextFramNum;
		CurSendP = (short)(7*(J1939_TransPG[pg].NextFramNum-1));
		CurSendLen = (short)((J1939_TransPG[pg].TotalLen-CurSendP) > 7 ? 7 : (J1939_TransPG[pg].TotalLen-CurSendP));
		msg.mData = J1939_TransPG[pg].Data.clone();
		msg.Can_DLC = J1939_TRANS_PKG_LEN;

		if(m_can.can_SendMsg(J1939_TransPG[pg].CanNum, msg)){
			J1939_TransPG[pg].NextFramNum++;
			if(J1939_TransPG[pg].NextFramNum > J1939_TransPG[pg].TotalFramNum){
				J1939_TransPG[pg].ConnectStatus = J1939_Connect_Status.J1939_CONNECT_WAITENDACK;
			}
			if(J1939_TransPG[pg].NextFramNum > J1939_TransPG[pg].FramStart_CTS+J1939_TransPG[pg].FramTotal_CTS){
				J1939_TransPG[pg].ConnectStatus = J1939_Connect_Status.J1939_CONNECT_WAITCTS;
			}
			return J1939_Error.J1939_ERR_OK;
		}else{
			return J1939_Error.J1939_ERR_SEND;
		}
	}

	boolean J1939_Trans_RecvFram(CanDeviceIndex CanNum, CanRxFrame pRecvFram)
	{
		short pg;
		short pf;

		pf = (short)((pRecvFram.ExtId >> 16)&0xff);
		if(pf == J1939_TP_PF){//控制
			//查找对应的PG
			for(pg=0; pg < J1939_TRANS_MAX; pg++){
				if(J1939_TransPG[pg].PgStatus != J1939_Pg_Status.J1939_PG_IDLE && J1939_TransPG[pg].PGN[0] == pRecvFram.mData[7]
						&& J1939_TransPG[pg].PGN[1] == pRecvFram.mData[6] && J1939_TransPG[pg].PGN[2] == pRecvFram.mData[5]){
					break;
				}
			}
			if(pg == J1939_TRANS_MAX){//未找到
				return false;
			}
			switch(pRecvFram.mData[0]){
				case J1939_TPCMD_CTS:
					if(J1939_TransPG[pg].PgStatus == J1939_Pg_Status.J1939_PG_SEND){
						J1939_TransPG[pg].FramStart_CTS = pRecvFram.mData[2];
						J1939_TransPG[pg].NextFramNum = pRecvFram.mData[2];
						J1939_TransPG[pg].FramTotal_CTS = pRecvFram.mData[1];
						if(J1939_TransPG[pg].FramTotal_CTS != 0){
							J1939_TransPG[pg].ConnectStatus = J1939_Connect_Status.J1939_CONNECT_SENDDT;
						}
						J1939_TransPG[pg].LastTime = (int)DriverTimeEngine.GetSysTem_ms();
					}
					break;
				case J1939_TPCMD_ENDACK:
					J1939_TransPG[pg].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
					break;
				case J1939_TPCMD_ABORT:
					J1939_TransPG[pg].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
					break;
			}
			return true;
		}else{//数据，暂不处理接收
			return false;

		}
	}

	J1939_Error J1939_Trans_SendFram(CanDeviceIndex CanNum, J1939FrameFormat pSendFram)
	{
		short	pg;

		if(pSendFram.PF >= J1939_PF_PDU2){//pdu2不支持传输协议，bam暂不支持
			return J1939_Error.J1939_ERR_SEND;
		}
		//分配pg
		//需要考虑互斥
		//QTOS_Lock(null);
		for(pg = 0; pg < J1939_TRANS_MAX; pg++){
			if(J1939_TransPG[pg].PgStatus == J1939_Pg_Status.J1939_PG_IDLE){
				break;
			}
		}
		if(pg == J1939_TRANS_MAX){
			//QTOS_Unlock(NULL);
			return J1939_Error.J1939_ERR_SEND;
		}

		//已分配pg
		//将数据填充到pg
		if(pSendFram.DtaLen > J1939_TRANS_LEN_MAX){
			//QTOS_Unlock(NULL);
			return J1939_Error.J1939_ERR_SEND;
		}
		for(int i=0;i<pSendFram.DtaLen;i++){
			J1939_TransPG[pg].Data[i] = pSendFram.pDta[i];
		}
		J1939_TransPG[pg].TotalLen = pSendFram.DtaLen;
		J1939_TransPG[pg].TotalFramNum = (short)((pSendFram.DtaLen + J1939_TRANS_DTDATA_LEN -1)/J1939_TRANS_DTDATA_LEN);
		J1939_TransPG[pg].NextFramNum = 1;
		J1939_TransPG[pg].ConnectStatus = J1939_Connect_Status.J1939_CONNECT_SENDRTS;
		J1939_TransPG[pg].PgStatus = J1939_Pg_Status.J1939_PG_SEND;
		J1939_TransPG[pg].PGN[0] = pSendFram.PS;
		J1939_TransPG[pg].PGN[1] = pSendFram.PF;
		J1939_TransPG[pg].PGN[2] = (short)((pSendFram.Prior)<<2);
		J1939_TransPG[pg].CanNum = CanNum;
		//QTOS_Unlock(NULL);

		//发送RTS
		if(J1939_Trans_SendRTS(pg)){//RTS发送成功
			J1939_TransPG[pg].ConnectStatus = J1939_Connect_Status.J1939_CONNECT_WAITCTS;
			J1939_TransPG[pg].LastTime = (int)DriverTimeEngine.GetSysTem_ms();
			return J1939_Error.J1939_ERR_OK;
		}else{
			//释放pg
			J1939_TransPG[pg].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
			return J1939_Error.J1939_ERR_SEND;
		}
	}

	public void J1939_Trans_Main()
	{
		int curtime = (int)DriverTimeEngine.GetSysTem_ms();

		//循环对每个pg进行处理
		for(short i = 0; i < J1939_TRANS_MAX; i++){
			if(J1939_TransPG[i].PgStatus == J1939_Pg_Status.J1939_PG_SEND){
				//根据连接状态进行处理
				switch(J1939_TransPG[i].ConnectStatus){
					case J1939_CONNECT_WAITCTS:
						if(DriverTimeEngine.CheckTimeOut(J1939_TransPG[i].LastTime, curtime, J1939_OVERTIME_CTS)){
							//放弃连接
							J1939_Trans_Abort(i);
						}
						break;
					case J1939_CONNECT_SENDDT:
						if(DriverTimeEngine.CheckTimeOut(J1939_TransPG[i].LastTime, curtime, J1939_OVERTIME_DT)){
							//发送数据
							if(J1939_Trans_SendDT(i)== J1939_Error.J1939_ERR_OK){
								J1939_TransPG[i].LastTime = curtime;
							}
						}
						break;
					case J1939_CONNECT_WAITENDACK:
						if(DriverTimeEngine.CheckTimeOut(J1939_TransPG[i].LastTime, curtime, J1939_OVERTIME_ENDACK)){
							//放弃连接
							J1939_Trans_Abort(i);
						}
						break;
					default:
						break;
				}
			}else if(J1939_TransPG[i].PgStatus == J1939_Pg_Status.J1939_PG_RECV){
				J1939_TransPG[i].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
				continue;//暂不支持接收
			}else{
				J1939_TransPG[i].PgStatus = J1939_Pg_Status.J1939_PG_IDLE;
				continue;
			}
		}
	}

	public J1939FrameFormat J1939_RecvFram(CanDeviceIndex CanNum, CanRxFrame pRecvFram)
	{
		J1939FrameFormat recv = new J1939FrameFormat();
		//简单接口封装
		pRecvFram.mHardNum = CanNum;
		if(pRecvFram.IDE == J1939_FrameType.CAN_FRAMTYPE_EXT){
			if(J1939_Trans_RecvFram(CanNum, pRecvFram) ){
				return null;
			}else{
				recv.PF = (short)((pRecvFram.ExtId >>16)&0xff);
				recv.FramType = J1939_FrameType.CAN_FRAMTYPE_EXT;
				recv.Prior = (short)((short)(pRecvFram.ExtId >>26)&0x07);
				recv.Page  = 0x00;
				recv.Addr = (short)(pRecvFram.ExtId&0xff);
				recv.DtaLen = pRecvFram.Can_DLC;
				recv.PS = (short)((pRecvFram.ExtId>>8)&0xff);
				recv.pDta = pRecvFram.mData;
			}
		}else if(pRecvFram.IDE == J1939_FrameType.CAN_FRAMTYPE_STD){
			recv.FramType = J1939_FrameType.CAN_FRAMTYPE_STD;
			recv.Addr = (short)((pRecvFram.StdId)&0xff);
			recv.DtaLen = pRecvFram.Can_DLC;
			recv.PS = (short)((pRecvFram.StdId >>8)&0xff);
			recv.PF = 0;
			recv.pDta = pRecvFram.mData;
		}else{
			return null;
		}
		return recv;   //处理CAN网络接收报文，并且将接收数据 放入对应接收缓存中
	}
	//数据发送
	public J1939_Error J1939_SendFram(CanDeviceIndex CanNum, J1939FrameFormat pSendFram)
	{
		CanTxFrame msg=new CanTxFrame();

		if(pSendFram.FramType == J1939_FrameType.CAN_FRAMTYPE_EXT){
			if(pSendFram.DtaLen >= 9){
				return J1939_Trans_SendFram(CanNum, pSendFram);
			}else{
				msg.IDE = J1939_FrameType.CAN_FRAMTYPE_EXT;
				msg.RTR = J1939_DataType.CAN_RTR_DATA;
				msg.ExtId = ((((int)pSendFram.Prior)<<26)
						| (((int)pSendFram.PF)<<16)
						| (((int)pSendFram.PS)<<8)
						| ((int)pSendFram.Addr));
				msg.Can_DLC = (byte)(pSendFram.DtaLen);
				msg.mData = pSendFram.pDta.clone();
			}
		}else if(pSendFram.FramType == J1939_FrameType.CAN_FRAMTYPE_STD){
			msg.IDE = J1939_FrameType.CAN_FRAMTYPE_STD;
			msg.RTR = J1939_DataType.CAN_RTR_DATA;
			msg.StdId = (((int)pSendFram.PF)<<8)
					| (((int)pSendFram.PS)<<0);
			msg.Can_DLC = (byte)(pSendFram.DtaLen);
			msg.mData = pSendFram.pDta.clone();
			//memcpy((char*)msg.mData,(char*)pSendFram->pDta,pSendFram->DtaLen);
		}else{
			return J1939_Error.J1939_ERR_SEND;
		}

		if(m_can.can_SendMsg(CanNum, msg) ){  /* send message */
			return J1939_Error.J1939_ERR_OK;
		}else{
			return J1939_Error.J1939_ERR_SEND;
		}

	} /* lCB_GenMessage */
}
