/*ver:0.0.1
 *bref:驱动层时间相关的操作都在这个类中完成
 */

package com.nushine.nshlbus.com.driverlayer.os_driverTime;

public class DriverTimeEngine {
	public static boolean CheckTimeOut(long oldtime,long newtime,int timeout){
		boolean flag=false;
		long  wActivateCounter=((long)(timeout+oldtime));
		if(timeout == 0){
			flag = true;
		}
		if(wActivateCounter > oldtime){
			if((newtime >= wActivateCounter) ||
					(newtime < oldtime)){
				flag = true;
			}
		}else if((newtime >= wActivateCounter) &&
				(newtime < oldtime)){
			flag = true;
		}
		return flag;
	}
	public static long GetSysTem_ms(){
		long systime = System.currentTimeMillis();
		return systime;
	}
}
