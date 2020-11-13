package com.kandi.dell.nscarlauncher.candriver;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.driverlayer.os_driverServer.IECarDriver;

/**
 * @author ivan.lv
 *
 */
public class DriverServiceManger {

	AirConditionDriver m_airCondiDriver = null;
	CarSettingDriver m_carSettingDriver = null;
	EmsDriver m_emsDriver = null;
	SettingDriver m_settingDriver = null;
	CarInfoDriver m_carInfoDriver = null;
	void initDrivers(IECarDriver R_service) {
		m_airCondiDriver = (R_service==null)?null:(new AirConditionDriver(R_service));
		m_carSettingDriver = (R_service==null)?null:(new CarSettingDriver(R_service));
		m_emsDriver = (R_service==null)?null:(new EmsDriver(R_service));
		m_settingDriver = (R_service==null)?null:(new SettingDriver(R_service));
		m_carInfoDriver = (R_service==null)?null:(new CarInfoDriver(R_service));
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
		Intent it = new Intent(ACTION);
		it.setPackage("com.nushine.nshlbus");
		this.context.bindService(it, serviceConnection,	Context.BIND_AUTO_CREATE);
	}

	public void stopService() {
		if (isServiceRunning()) {
			this.context.unbindService(this.serviceConnection);
		} else {
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
	
	public String getVersion() {
		return this.sDrvSvrVersion;
	}
	/**
	 * 获取空调UI数据
	 * @return
	 */
	public AirConditionDriver getAirConditionDriver() {
		return m_airCondiDriver;
	}

	/**
	 * 获取车辆控制数据
	 * @return
	 */
	public CarSettingDriver getCarSettingDriver() {
		return m_carSettingDriver;
	}

	/**
	 * 获取能量管理数据
	 * @return
	 */
	public EmsDriver getEmsDriver() {
		return m_emsDriver;
	}

	/**
	 * 获取设置界面相关CAN数据
	 * @return
	 */
	public SettingDriver getSettingDriver() {
		return m_settingDriver;
	}

	/**
	 * 车速、剩余里程
	 * @return
	 */
	public CarInfoDriver getCarInfoDriver() {
		return m_carInfoDriver;
	}
}
