/*ver:0.0.1
 *bref:定义J1939的数据帧格式
 */
package com.nushine.nshlbus.com.driverlayer.os_driverJ1939;

import com.nushine.nshlbus.com.driverlayer.os_driverJ1939.J1939ConstFlag.J1939_FrameType;

public class J1939FrameFormat {
	public J1939_FrameType FramType;
	public short PF;
	public short PS;
	public short Addr;
	public short Prior;
	public short Page;
	public short DtaLen;
	public short[] pDta=new short[8];
	public String PrintJ1939FrameFormat(String message)
	{
		message = "PF:"+Integer.toHexString(this.PF)+","+
				"PS:"+Integer.toHexString(this.PS)+","+
				"Addr:"+Integer.toHexString(this.Addr)+","+
				"Prior:"+Integer.toHexString(this.Prior)+","+
				"Page:"+Integer.toHexString(this.Page)+"\n"+"DtaLen:"+this.DtaLen+","+"\n";
		String str="";
		for(int i=0;i<this.pDta.length;i++){
			str+=Integer.toHexString(this.pDta[i]);
			str+=",";
		}
		message+=str+"\n";
		return message;
	}
}
