// IECarDriver.aidl
package com.driverlayer.kdos_driverServer;

// Declare any non-default types here with import statements
interface IECarDriver{
/**
	 * 获取接口实现的版本号
	 * @return 版本号
	 */
	String getVersion();

	/**
	 * 获取整车控制设备BCU的基本信息
	 * @param 			null
	 * @return			返回BCU设备信息字符串
	 *
	 * BCU设备信息字符串格式：
	 * 	BCU供应商名称,
	 * 	硬件版本号,
	 * 	软件版本号,
	 */
	String getBaseInfo_Bcu();

	/*******************************************************************
	*ECOC通讯接口
	********************************************************************/
		/**
	 * 获取电池概要信息
	 * @param batNum		电池编号，正常编号介于0～3，其他值无效
	 * @param array_info	返回电池概要信息中的数字信息数组；
	 * @return				返回电池编号；
	 *
	 * 电池概要整形数据信息顺序:
	 *	array_info[0]	获取成功返回0，失败返回负值错误代码
	 * 	array_info[1]	电池电压，范围(0~110.0V),分辨率0.1V
	 * 	array_info[2]	电池电流，范围(-1600.0~1600.0A),分辨率0.1A
	 *	array_info[3]	最大单体电压(单位1mV),
	 *	array_info[4]	最低单体电压(单位1mV),
	 *	array_info[5]	最大温度(分辨率1摄氏度),
	 *	array_info[6]	最小温度(分辨率1摄氏度),
	 * 	array_info[7]	电池绝缘电阻(低值),
	 * 	array_info[8]	SOC剩余电量(0.0~100.0%),
	 * 	array_info[9]	SOH电池健康度 (0.0~100.0%),
	 * 	array_info[10]	完整充电次数,
	 * 	array_info[11]	硬件版本
	 * 	array_info[12]	软件版本
	 *	array_info[13]	电池充电状态(0：放电；1：充电；2：搁置)
	 *	array_info[14]	电池安全性(0:安全，1：报警，2：故障)
	 */
	String Ecoc_getGeneral_Battery(int batNum,out int[] array_info);

	/**
	 * 获取整车概要信息
	 * @param array_info	整形数据采用数组方式顺序排列上传。
	 * @return				获取成功返回0，失败返回负值错误代码
	 *
	 * 整车概要信息数据顺序：
	 * 	array_info[0]	整车SOC,
	 * 	array_info[1]	剩余里程,KM
	 * 	array_info[2]	控制器转速(范围：0—65536 rpm),
	 * 	array_info[3]	正极绝缘电阻(0—200000 KOhm),
	 * 	array_info[4]	负极绝缘电阻(0—200000 KOhm),
	 * 	array_info[5]	电池就位数(0-4),
	 *  array_info[6]	剩余电量,1KWH,
	 *  array_info[7]	车速,1km/h,
	 *  array_info[8]	充电线连接状态:0未连接，1连接,
	 *  array_info[9]	充电状态:0:停止；1:启动 2:故障停止
	 */
	int Ecoc_getGeneral_Car(out int[] array_info);
	/*********************Ecoc专用接口End***********************/

	/**
	 * 获取历史趋势图数据
	 * @param nTrendType	曲线类型：1:电流(A)/2:电量(%)/3:发动机功率(kw) 0xffff 当前时间节点无数据
	 * @param nDataLen		所获取采样数量，从当前时间算起的历史数据
	 * @param nInterval		采样间隔ms
	 * @param nValueArr		数据值数组，数组长度 >= nDataLen
	 * @return				实际返回的采样量
	 */
	int Ecoc_getTrendData(int nTrendType, int nDataLen, int nInterval,out int[] nValueArr);

	/**
	 * 获取电池概要信息
	 * @param batNum		电池编号，正常编号介于0～3，其他值无效
	 * @param array_info	返回电池概要信息中的数字信息数组；
	 * @return				返回电池概要信息中的一些字符串数据，各参数用逗号分隔；
	 *
	 * 电池概要信息字串顺序:
	 * 	厂商名称,
	 *	电池序列号,
	 *	硬件版本号,
	 *	软件版本号,
	 *
	 * 电池概要整形数据信息顺序:
	 *	array_info[0]	获取成功返回0，失败返回负值错误代码
	 * 	array_info[1]	电池电压范围(0~110.0V),分辨率0.1V
	 * 	array_info[2]	电池电流范围(-1600.0~1600.0A),分辨率0.1A
	 * 	array_info[3]	SOC剩余电量(0.0~100.0%),
	 * 	array_info[4]	SOH电池健康度 (0.0~100.0%),
	 * 	array_info[5]	完整充放电次数,
	 * 	array_info[6]	累计输入kwh(>=0),分辨率0.1kwh
	 * 	array_info[7]	累计输出kwh(>=0),分辨率0.1kwh
	 * 	array_info[8]	本次输入kwh(0~65536),分辨率0.1kwh
	 * 	array_info[9]	本次输出kwh(0~65536),分辨率0.1kwh
	 *
	 * 	array_info[10]	电池单体数量,
	 * 	array_info[11]	最高单体电压编号,
	 *	array_info[12]	最低单体电压编号,
	 *	array_info[13]	最大单体电压(单位1mV),
	 *	array_info[14]	最低单体电压(单位1mV),
	 *	array_info[15]	单体平均值	(单位1mV),
	 *
	 * 	array_info[16]	电池内部温度探头数量,
	 *	array_info[17]	最大温度探头标号,
	 *	array_info[18]	最小温度探头标号,
	 *	array_info[19]	最大温度(分辨率1摄氏度),
	 *	array_info[20]	最小温度(分辨率1摄氏度),
	 *
	 *	array_info[21]	电池加热状态(1:on/0:off),
	 *	array_info[22]	均衡状态(1:on/0:off),
	 *	array_info[23]	单体均衡启动数目,
	 *	array_info[24]	单体均衡状态掩码(int型，每bit代表一个单体均衡状态开关，1:on/0:off),
	 *	array_info[25]	电池充电状态(0：放电；1：充电；2：搁置)
	 *	array_info[26]	电池安全性(int 0:安全，1：报警，2：故障)
	 * 	array_info[27]	硬件版本
	 * 	array_info[28]	软件版本
	 * 	array_info[29]	正极绝缘电阻(0—200000 KOhm),
	 * 	array_info[30]	负极绝缘电阻(0—200000 KOhm),
	 */
	String getGeneral_Battery(int batNum,out int[] array_info);


	/**
	 * 获取整车概要信息
	 * @param array_info	整形数据采用数组方式顺序排列上传。
	 * @return				获取成功返回0，失败返回负值错误代码
	 *
	 * 整车概要信息数据顺序：
	 * 	array_info[0]	整车SOC,
	 * 	array_info[1]	汽车电池总电压(0~400.0V),分辨率100mV
	 * 	array_info[2]	电流环采样电流(-1600.0~1600.0A),分辨率100mA
	 * 	array_info[3]	剩余里程,KM
	 * 	array_info[4]	控制器转速(范围：0—65536 rpm),
	 * 	array_info[5]	输出扭矩(范围：0—65536Nm),
	 * 	array_info[6]	正极绝缘电阻(0—200000 KOhm),
	 * 	array_info[7]	负极绝缘电阻(0—200000 KOhm),
	 * 	array_info[8]	电池就位数(0-4),0表示电池空，车上没有电池
	 * 	array_info[9]	整车电池就位掩码(short，1:电池已就位),
	 *
	 * 	array_info[10]	最大电压单体所在电池编号,
	 * 	array_info[11]	最大电压单体编号,
	 * 	array_info[12]	最大电压单体电压值(mV,整型),
	 *
	 * 	array_info[13]	最小电压单体所在电池编号,
	 * 	array_info[14]	最小电压单体编号,
	 * 	array_info[15]	最小电压单体电压值(mV,整型),
	 *
	 * 	array_info[16]	最高温度所在电池编号,
	 * 	array_info[17]	最高温度单体编号,
	 * 	array_info[18]	最高温度单体温度(摄氏度,整型),
	 *
	 * 	array_info[19]	最低温度所在电池编号,
	 * 	array_info[20]	最低温度单体编号,
	 * 	array_info[21]	最低温度单体温度(摄氏度,整型),
	 *
	 */
	int getGeneral_Car(out int[] array_info);


	/***********************************************
	 * 电动汽车状态获取接口
	 ***********************************************/

	/**
	 * 获取电池单体电压信息
	 * @param batNum	电池编号，正常编号介于0～3，其他值无效
	 * @param cell[]	返回电池单体电压信息数组，电压单位mv，单体电压值范围：0—4500mV
	 * @return			返回电池单体数量,上限为50;
	 */
	int getDetial_BatCellVol(int batNum, out int[] cell);


	/**
	 * 获取电池详细温度信息
	 * @param batNum	电池编号，正常编号介于0～3，其他值无效
	 * @param temp[]	返回电池内部温度采样值数组，温度单位：摄氏度。
	 * @return			返回电池内部温度探头数量,上限为50；
	 */
	int getDetial_BatTemp(int batNum,out int[] temp);


	/**
	 * 获取电池SOC信息
	 * @param array_info	返回电池数量和SOC信息；
	 * @return				获取成功返回0，失败返回负值错误代码；
	 *
	 * 电池概要整形数据信息顺序:
	 *	array_info[0]	电池数量(最大为4)，正常为2箱电池
	 * 	array_info[1]	第1箱电池SOC
	 * 	array_info[2]	第2箱电池SOC
	 *	array_info[3]	第3箱电池SOC
	 *	array_info[4]	第4箱电池SOC
	 */
	 int getAllBat_Soc(out int[] array_info);


	/**
	 * 获取充电机状态信息
	 * @param array_info	返回充电机信息
	 * @return				返回充电机厂商信息
	 *
	 * 充电机整形数据顺序：
	 * 	array_info[0]	充电电压(V),范围(0~110.0V),分辨率0.1V
	 * 	array_info[1]	充电电流(A),范围(-1600.0~1600.0A),分辨率0.1A
	 * 	array_info[2]	本次充电电量(kwh),范围(0~65536),分辨率0.1kwh
	 * 	array_info[3]	本次充电容量(Ah),范围(0~1000),分辨率0.1Ah
	 *	array-info[4]	充电机状态，0:停止；1:启动 2:故障停止
	 */
	String getGeneral_Charger(out int[] array_info);

	/**
	 * 获取系统错误信息
	 * @param num			实际返回的错误信息条数，num[0]有效
	 * @return				返回错误信息字符串数组
	 *
	 *
	 */
	String[] getError_Car();

	/**
	 * 清除系统所有历史错误信息
	 * @return				被清除的错误信息数
	 */
	int cleanError_Car();

	/**
	 * 获取系统警告信息
	 * @param num	实际返回的警告信息条数
	 * @return		返回警告信息字符串数组
	 *
	 *
	 */
	String[] getWarning_Car();

	/**
	 * 清除系统所有历史警告信息
	 * @return		被清除的警告信息数
	 */
	int cleanWarning_Car();

	/******************************************************
	 * 						电动汽车设置接口
	 ******************************************************/
	/**
	 * 设置按钮状态
	 * @param buttonState	默认0一键开窗；
	 * @return				设置成功返回0，失败返回负值错误代码
	 */
	int set_OneKeyOpenWindow(int buttonState);

	/**
	 * 设置汽车档位
	 * @param dnrState	包含三个状态，前进(0x01)，空档(0x02)，倒车(0x03)；
	 * @return			设置成功返回0，失败返回负值错误代码
	 */
	int setCar_DNR(int dnrState);

	/**
	 * 读取车辆档位
	 * @param null
	 * @return			返回档位信息 1:D档,2:N档,3:R档；
	 */
	int getCar_DNR();

	/**
	 * 设置汽车运动模式
	 * @param motor_mode 包含2个模式，经济模式（0x01）,运动模式（0x02）;
	 * @return			 设置成功返回0，失败返回负值错误代码
	 */
	int setCar_WorkMode(int motor_mode);

	/**
	 * 读取车辆模式参数
	 * @param null
	 * @return			返回车辆模式 1:运动模式,0:经济模式；
	 */
	int getCar_WorkMode();

	/**
	 * 设置EPS助力模式
	 * @param EPS_mode  包含3个模式，助力轻（0x01）,助力中（0x02）,助力重（0x03）;
	 * @return			 设置成功返回0，失败返回负值错误代码
	 */
	int setCar_EPSassistance(int EPS_mode);

	/**
	 * 读取EPS助力模式
	 * @param null
	 * @return			返回EPS助力模式 1:助力轻,2:助力中,3:助力重；
	 */
	int getCar_EPSassistance();

	/**
	 * 设置汽车空调工作参数
	 * 接口参数中没有被按下的按钮下发无效值0xffff;
	 * @param isOpenAc	0x01:关闭；0x02:开启
	 * 		  isOpenPtc 0x01:关闭；0x02:开启
	 *
	 * @param mode  	bit0-3空调工作模式:1:吹面；2：吹面+吹脚；3：吹脚；4：吹脚+除霜;5:除霜
	 					bit4: 为1表示开启内循环，为0默认为外循环；
	 					--------------------------------------------------
	 					--bit7--bit6--bit5--bit4--bit3--bit2--bit1--bit0--
	 					-- 保留 --保留  -- 保留--内循环--[     空调工作模式选择            ]--
	 					--------------------------------------------------
	 * @param temp  	设置温度，范围1-16档 (18-34摄氏度)；
	 * @param windSpeed	风速，1到8，共8档； 9表示关闭风机；
	 * @return			设置成功返回0，失败返回负值错误代码
	 */
	int setAirCon_Para(int isOpenAc,int isOpenPtc, int mode, int temp, int windSpeed);


	/**
	 * 读取汽车空调工作参数
	 * @param status	数组长度为3，数据定义：
	 					status[0]	车内环境温度:单位1摄氏度，范围：-40-128
					  	status[1]	车外环境温度:(同上)
						status[2]	车内湿度：0-100%，分辨率1；
						status[3]	设定温度：范围1-16档 (18-34摄氏度)
						status[4]	0x00:AC+PTC关闭;
					 				bit0-bit1:1开启AC，0关闭AC;
				 					bit2-bit3:1开启PTC，0关闭PTC;
					 				bit4-bit5:1开启风扇，0关闭风扇;
				 					--------------------------------------------------
				 					--bit7--bit6--bit5--bit4--bit3--bit2--bit1--bit0--
				 					-- 保留 --保留  --[	风扇控制    ]--[	PTC控制    ]--[  AC控制    ]--
				 					--------------------------------------------------
					    status[5]  	bit0-3空调工作模式:1:吹面；2：吹面+吹脚；3：吹脚；4：吹脚+除霜;5:除霜
				 					bit4: 为1表示开启内循环，为0默认为外循环；
				 					--------------------------------------------------
				 					--bit7--bit6--bit5--bit4--bit3--bit2--bit1--bit0--
				 					-- 保留 --保留  -- 保留--内循环--[     空调工作模式选择            ]--
				 					--------------------------------------------------
				 		status[6]	风速，1到8，共8档；
				 		status[7]	后除霜：1开启，0关闭
	 * @return			1空调离线,0空调正常；
	 */
	int GetAirCon_Status(out int[] status);

	/**
	 * 汽车门窗以及大灯状态通知
	 * @param actType	控制对象类型
	 * @param actNum	控制对象编号
	 * @param actState	动作状态设置
	 *
	 * 设备名称 actType值 actNum编号范围	actState状态定义
	 *  中控门锁	0x01	1			0x01:使能锁止/0x02:解锁
	 *  车窗		0x02	1~7			0x01:升/0x02:降/0x03:停止
	 *  后备箱  	0x03	1			0x01:使能锁止/0x02:解锁
	 *  充电盖  	0x04	1			0x01:开启/0x02:关闭
	 *  大灯		0x05	1			0x01:远光灯/0x02:近光灯/0x03:关闭
	 *  双跳		0x06	1			0x01:开启/0x02:关闭
	 *  前雾灯	0x07	1			0x01:开启/0x02:关闭
	 *  小灯		0x08	1			0x01:开启/0x02:关闭
	 *  电池仓门	0x09	1			0x01:升/0x02:降/0x03：停止
	 *  后雾灯	0x0a	1			0x01:开启/0x02:关闭
	 *
	 *  上述值除了状态定义中的值，其他值都无效。车窗和车门编号顺序一致，
	 *  从驾驶员侧按照之字形计数，前左/前右/后左/后右...天窗编号为7。
	 */
	void setCar_Action(int actType, int actNum, int actState);

	/**
	 * 获取汽车门窗以及大灯状态
	 * @param array_info 获取车身设备状态
	 * @return 			返回值：0表示BCM正常;1表示BCM掉线
	 *
	 *
	 * 车身状态定义：
	 *array_info[0] 车窗1状态:0x01:升/0x02:降/0x03:停止
	 *array_info[1]	车窗2状态:0x01:升/0x02:降/0x03:停止
	 *array_info[2]	车窗3状态:0x01:升/0x02:降/0x03:停止
	 *array_info[3]	车窗4状态:0x01:升/0x02:降/0x03:停止
	 *array_info[4] 车窗5状态:0x01:升/0x02:降/0x03:停止
	 *array_info[5]	车窗6状态:0x01:升/0x02:降/0x03:停止
	 *array_info[6]	车窗7状态:0x01:升/0x02:降/0x03:停止
	 *array_info[7]	车门锁状态：0x01:使能锁止/0x02:解锁
	 *array_info[8] 后备箱状态：0x01:使能锁止/0x02:解锁
	 *array_info[9]	充电盖状态：0x01:开启/0x02:关闭
	 *array_info[10] 大灯状态：0x01:远光灯/0x02:近光灯/0x03:关闭
	 *array_info[11] 小灯状态：0x01:开启/0x02:关闭
	 *array_info[12]	前雾灯状态：0x01:开启/0x02:关闭
	 *array_info[13]	后雾灯状态：0x01:开启/0x02:关闭
	 *array_info[14]	双跳状态：0x01:开启/0x02:关闭
	 *array_info[15]	电池舱门状态：0x01:升/0x02:降/0x03:停止
	 *
	 *  从驾驶员侧按照之字形计数，前左/前右/后左/后右...天窗编号为7。
	 */
	int getCarState(out int[] array_info);

	/****************************************************
	 * 电池充电设置接口
	 ****************************************************/

	/**
	 * 获取当前电池充电设置参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	充电停止总压			(单位 0.1V)
	 *		param[1]	充电停止单体电压			(单位 1mV)
	 *		param[2]	充电停止温度			(单位 1度)
	 *		param[3]	充电停止电流			(单位 0.1A)
	 *		param[4]	降流单体电压			(单位 1mV)
	 *		param[5]	降流延时时间			(单位 1S)
	 *
	 *		param[6]	Step0_Charger_temp			(单位 1度)
	 *		param[7]	Step0_Charger_cellv			(单位:mV)
	 *		param[8]	Step0_Charger_current		(单位：0.1A)
	 *		param[9]	Step1_Charger_temp			(单位 1度)
	 *		param[10]	Step1_Charger_cellv			(单位:mV)
	 *		param[11]	Step1_Charger_current		(单位：0.1A)
	 *		param[12]	Step2_Charger_temp			(单位 1度)
	 *		param[13]	Step2_Charger_cellv			(单位:mV)
	 *		param[14]	Step2_Charger_current		(单位：0.1A)
	 *
	 *		param[15]	Step0_Dis_temp		(单位 1度)
	 *		param[16]	Step0_Dis_cellw		(单位:mV)
	 *		param[17]	Step0_Dis_celle		(单位：mV)
	 *		param[18]	Step1_Dis_temp		(单位 1度)
	 *		param[19]	Step1_Dis_cellw		(单位:mV)
	 *		param[20]	Step1_Dis_celle		(单位：mV)
	 *		param[21]	Step2_Dis_temp		(单位 1度)
	 *		param[22]	Step2_Dis_cellw		(单位:mV)
	 *		param[23]	Step1_Dis_celle		(单位：mV)
	 */
	int getBattaryParam(out int[] param);


	/**
	 * 设置当前电池充电设置参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	充电停止总压			(单位 0.1V)
	 *		param[1]	充电停止单体电压			(单位 1mV)
	 *		param[2]	充电停止温度			(单位 1度)
	 *		param[3]	充电停止电流			(单位 0.1A)
	 *		param[4]	降流单体电压			(单位 1mV)
	 *		param[5]	降流延时时间			(单位 1S)
	 *
	 *		param[6]	Step0_Charger_temp			(单位 1度)
	 *		param[7]	Step0_Charger_cellv			(单位:mV)
	 *		param[8]	Step0_Charger_current		(单位：0.1A)
	 *		param[9]	Step1_Charger_temp			(单位 1度)
	 *		param[10]	Step1_Charger_cellv			(单位:mV)
	 *		param[11]	Step1_Charger_current		(单位：0.1A)
	 *		param[12]	Step2_Charger_temp			(单位 1度)
	 *		param[13]	Step2_Charger_cellv			(单位:mV)
	 *		param[14]	Step2_Charger_current		(单位：0.1A)
	 *
	 *		param[15]	Step0_Dis_temp		(单位 1度)
	 *		param[16]	Step0_Dis_cellw		(单位:mV)
	 *		param[17]	Step0_Dis_celle		(单位：mV)
	 *		param[18]	Step1_Dis_temp		(单位 1度)
	 *		param[19]	Step1_Dis_cellw		(单位:mV)
	 *		param[20]	Step1_Dis_celle		(单位：mV)
	 *		param[21]	Step2_Dis_temp		(单位 1度)
	 *		param[22]	Step2_Dis_cellw		(单位:mV)
	 *		param[23]	Step1_Dis_celle		(单位：mV)
	 */
	int setBattaryParam(in int[] param);


	/**
	 * 获取充电机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	充电机最高充电电压			(单位 0.1V)
	 *		param[1]	充电机最高充电电流			(单位 0.1A)
	 *		param[2]	均衡使能					(0:禁用，1:使能)
	 */
	int getBattaryChargingParam(out int[] param);


	/**
	 * 设置充电机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	充电机最高充电电压			(单位 0.1V)
	 *		param[1]	充电机最高充电电流			(单位 0.1A)
	 *		param[2]	均衡使能					(0:禁用，1:使能)
	 */
	int setBattaryChargingParam(in int[] param);


	/**
	 * 获取电池组控制单元(BCU)参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	电池粘连检测周期			(单位 秒)
	 *		param[1]	电流环选型					(#电流环编号 0~255)
	 *		param[2]	最小电池数目				(0~255)
	 *		param[3]	协议使能					(位掩码 bit0:康迪协议，bit1:大有协议，bit3:万向协议  0:禁用，1:使能)
	 *		param[4]	RTC低功耗使能				(0:禁用，1:使能)
	 *		param[5]	RTC低功耗CAN使能			(0:禁用，1:使能)
	 *		param[6]	电流环自动校准使能			(0:禁用，1:使能)
	 *		param[7]	均衡使能					(0:禁用，1:使能)
	 *		param[8]	清除电池粘连标志				(0:禁用，1:使能)
	 *		param[9]	绝缘报警值					(单位 KR)
	 *		param[10]	绝缘故障值					(单位 KR)
	 */
	int getBCUParam(out int[] param);


	/**
	 * 设置电池组控制单元(BCU)参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	电池粘连检测周期			(单位 秒)
	 *		param[1]	电流环选型					(#电流环编号 0~255)
	 *		param[2]	最小电池数目				(0~255)
	 *		param[3]	协议使能					(位掩码 bit0:康迪协议，bit1:万向协议，bit2:大有协议. 0:禁用，1:使能)
	 *		param[4]	RTC低功耗使能				(0:禁用，1:使能)
	 *		param[5]	RTC低功耗CAN使能			(0:禁用，1:使能)
	 *		param[6]	电流环自动校准使能			(0:禁用，1:使能)
	 *		param[7]	均衡使能					(0:禁用，1:使能)
	 *		param[8]	清除电池粘连标志				(0:禁用，1:使能)
	 *		param[9]	绝缘报警值					(单位 KR)
	 *		param[10]	绝缘故障值					(单位 KR)
	 */
	int setBCUParam(in int[] param);


	/**
	 * BCU继电器粘连状态清除
	 * @param param		预留，默认0
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 */
	int resetBCURelayStick(int param);


 	/**
	 * 获取电机控制机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	最高转数限定				(单位 RPM)
	 *		param[1]	怠速转矩限定				(单位 牛/米, -600 ~ 600)
	 *		param[2]	SOC							(单位 千分比)
	 */
	int getMotorControlerParam(out int[] param);


	 /**
	 * 设置电机控制机参数
	 * @param param		参数数组
	 * @return 			成功返回0，失败返回负值错误代码
	 *
	 * 参数数组定义
	 *		param[0]	最高转数限定				(单位 RPM)
	 *		param[1]	怠速转矩限定				(单位 牛/米, -600 ~ 600)
	 *		param[2]	SOC						(单位 0.1%)
	 */
	int setMotorControlerParam(in int[] param);

	/*******************************************************************
	*设备升级通讯接口
	********************************************************************/
	/*
	 * @param path	升级固件位置;
	 * 		  flag  true:启动升级；false:停止升级；
	 * 		  addr_offset	升级地址（0-20）地址偏移,默认为0,对于多个相同设备的需要选择地址附加偏移，例如BMS
	 * return null
	 */
	void StartUpdataHex(String path,int addr_offset,boolean falg);

	/*
	 * @param 参数数组;
	 * @return 升级进度值，百分比；
	 *
	 * param 定义：
	 * 	param[0]		厂商信息；
	 * 	param[1..2]		硬件版本号，高字节在前，低字节在后
	 * 	param[3..4]		软件版本号，高字节在前，低字节在后
	 * 	param[5..6]		升级文件大小（kb）
	 *	param[7]		升级状态 0x55进入升级,0xAA升级完成,01启动信息错误,02升级失败
	 */
	int GetUpdataStatus(out int[] param);

	/*
	 * @param 参数数组;
	 *
	 * param 定义：
	 * 	param[0]		tbox防拆状态：0->默认状态;1->设备防拆;2->天线防拆;3->正常状态
	 * 	param[1]		tbox授权状态：0->默认状态;1->授权;2->未授权；
	 */
	int GetTBoxStatus(out int[] param);

	/*
	 * 返回空字符串为未获得全车架号;
	 */
	String GetCarVin();

	/**
	 * 读取车辆模式能量等级
	 * @param null
	 * @return			返回能量等级 0:低,1:中,2:高；
	 */
	int getCar_Energy();

	/**
	 * 读取能量管理概要信息
	 * @param 参数数组;
	 * @return
	 *
	 * param 定义：
	 * 	param[0]		剩余电量；
	 * 	param[1]		总电压
	 * 	param[2]		总电流
	 * 	param[3]		单体最高
	 *	param[4]		单体最低
	 *	param[5]		最高温度
	 *	param[6]		最低温度
	 *	param[7]		正绝缘阻值
	 *	param[8]		负绝缘阻值
	 *	param[9]		电池箱数
	 *	param[10]		soc
	 *  param[11]		剩余里程
	 */
	int getPowerManager(out int[] param);

	/**
	* 请求车架号
	*/
	void requestCarVin();
}

