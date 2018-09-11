package com.example.dell.nscarlauncher.ui.bluetooth;

public class FlagProperty {
	public static String BtCode = "0000";                          //蓝牙密码
	public static int STREAM_MAX_MUSIC = 0;                        //音乐最大音量
	public static int STREAM_MUSIC = 0;                            //音乐音量
	public static String phone_number = "";                        //来电时电话号码
	public static boolean flag_phone_ringcall = false;             //是否来电过程中
	public static boolean flag_bluetooth = false;                 //蓝牙的打开与关闭
	public static boolean flag_phone_incall_click = false;         //是否是主板接听来电
	public static boolean is_3gphone_comming = false;              //是否3G来电
	public static boolean is_3gcall_start = false;                 //是否3g有过电话拨通
	public static boolean is_one_oper = false;                     //是否有过call in 或者call outgoing
	public static boolean is_3g = false;                           //3g状态
	public static boolean have_sim = false;                        //是否插入sim卡
	public static boolean is_calling = false;                      //是否正在打电话过程中
	public static boolean is_callindex_one = false;                //1线是否通话中
	public static boolean is_callindex_two = false;                //2线是否通话中
	public static String phone_number_one = "";                    //蓝牙电话1电话显示
	public static String phone_number_two = "";                    //蓝牙电话2电话显示
	public  static  boolean isHourdate =true;     //时间设置
	public static final int PLAY_MSG = 1;		//播放
	public static final int PAUSE_MSG = 2;		//暂停
	public static final int STOP_MSG = 3;		//停止
	public static final int CONTINUE_MSG = 4;	//继续
	public static final int PRIVIOUS_MSG = 5;	//上一首
	public static final int NEXT_MSG = 6;		//下一首
	public static final int PROGRESS_CHANGE = 7;//进度改变
	public static final int PLAYING_MSG = 8;	//正在播放
	public static  int  CarMode =0; //0默认 1 已授权 2 未授权
	public static  int  CarWork =0; // 1:运动模式,0:经济模式；
	public static int staus =1; //	1空调离线,0空调正常；
    public static int BCMStaus =1; //	0表示BCM正常;1表示BCM掉线
	public static  int CarPower =0;//汽车电量
	public static  int  Speed =0; //车速
}
