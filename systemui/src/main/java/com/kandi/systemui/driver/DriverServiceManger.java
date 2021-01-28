package com.kandi.systemui.driver;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.driverlayer.os_driverServer.IECarDriver;

/**
 * @author ivan.lv
 *
 */
public class DriverServiceManger {

	CarSettingDriver m_carSettingDriver = null;
	AirConditionDriver m_airCondiDriver = null;
	EcocEnergyInfoDriver m_ecocEnergyInfoDriver = null;
	TBoxInfoDriver m_tBoxInfoDriver =null;
	void initDrivers(IECarDriver R_service) {
		m_carSettingDriver = (R_service==null)?null:(new CarSettingDriver(R_service));
		m_airCondiDriver = (R_service==null)?null:(new AirConditionDriver(R_service));
		m_ecocEnergyInfoDriver = (R_service==null)?null:(new EcocEnergyInfoDriver(R_service));
		m_tBoxInfoDriver =(R_service==null)?null:(new TBoxInfoDriver(R_service));
	}
	
	String sDrvSvrVersion = "(后台服务不可用)";
	private static final String ACTION = "com.driverlayer.os_driverServer.RemoteService";

	private static DriverServiceManger instance;
	public static DriverServiceManger getInstance() {
		if (instance == null) {
			instance = new DriverServiceManger();
		}
		return instance;
	}
	
	private IECarDriver R_service = null;
	private Context context;
	private boolean m_binderflag = false;

	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			R_service = IECarDriver.Stub.asInterface(arg1);
			m_binderflag = true;
			try {
				sDrvSvrVersion = R_service.getVersion();
			}catch(RemoteException e) {
				e.printStackTrace();
				R_service = null;
			}
			if(R_service!=null){
//				Toast.makeText(context, "服务启动成功", Toast.LENGTH_LONG).show();
			}else{
//				Toast.makeText(context, "服务启动失败", Toast.LENGTH_LONG).show();
			}
			Log.i("ServiceConnection","###Service Connected. Ver=" + sDrvSvrVersion);
			initDrivers(R_service);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			R_service = null;
			m_binderflag = false;
			sDrvSvrVersion="(后台服务不可用)";
			Log.i("ServiceConnection","###Service Disconnected.");
			initDrivers(R_service);
		}

	};

	private DriverServiceManger() {
	}

	public void startService(Context context) {
		this.context = context;
		Intent it = new Intent(context,ACTION.getClass());

		this.context.bindService(it, serviceConnection,	Context.BIND_AUTO_CREATE);
		//Toast.makeText(this.context, "启动后台服务", Toast.LENGTH_LONG).show();
	}

	public void stopService() {
		if (isServiceRunning()) {
			this.context.unbindService(this.serviceConnection);
//			Toast.makeText(this.context, "停止后台服务ֹ", Toast.LENGTH_LONG).show();
		} else {
//			Toast.makeText(this.context, "服务未运行", Toast.LENGTH_LONG).show();
		}
	}

	public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			String str = service.service.getClassName();
			if ("com.nushine.nshlbus.com.driverlayer.os_driverServer.DriverManager".equals(str)) {
				return true;
			}
		}
		return false;
	}

//	public void OnForward(View v) {
//		if (R_service == null) {
//			return;
//		}
//		try {
//			R_service.setCar_DNR(0x55);
//			int[] info = new int[100];
//			int[] cell = new int[30];
//			int[] temp = new int[10];
//			String str = "";
//			R_service.getGeneral_Car(str, info);
//			R_service.getDetial_BatCellVol(1, cell);
//			R_service.getDetial_BatTemp(1, temp);
//			int i = info.length;
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void OnEmpty(View v) {
//		if (R_service == null) {
//			return;
//		}
//		try {
//			R_service.setCar_DNR(0xff);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public void OnBackUp(View v) {
//		if (R_service == null) {
//			return;
//		}
//		try {
//			R_service.setCar_DNR(0xaa);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public String getVersion() {
		return this.sDrvSvrVersion;
	}

	/**
	 * 获取主界面车灯窗设置UI数据
	 * @return
	 */
	public CarSettingDriver getCarSettingDriver() {
		return m_carSettingDriver;
	}

	/**
	 * 获取空调UI数据
	 * @return
	 */
	public AirConditionDriver getAirConditionDriver() {
		return m_airCondiDriver;
	}

	/**
	 * 获取ECOC能量管理UI数据（包括主界面剩余里程、剩余电量）
	 * @return
	 */
	public EcocEnergyInfoDriver getEcocEnergyInfoDriver() {
		return m_ecocEnergyInfoDriver;
	}

	/**
	 * 获取能量管理详情UI数据
	 * @param
	 * @return
	 * 
	 * deprecated, please use EnergyInfoDriver.getBattaryDetailInfo(int index) instead.
	 */
//	public EnergyInfoDetailsDriver getEnergyInfoDetailsDriver(int nBattaryNum) {
//		return (R_service == null)? null : (new EnergyInfoDetailsDriver(R_service, nBattaryNum));
//	}
	/*
	* h获取tbox
	* */

	public TBoxInfoDriver getM_tBoxInfoDriver() {
		return m_tBoxInfoDriver;
	}
}
