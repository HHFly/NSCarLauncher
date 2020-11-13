// IECarDriver.aidl
package com.driverlayer.os_driverServer;

// Declare any non-default types here with import statements
interface IECarDriver{
/**
	 * 获取接口实现的版本号
	 * @return 版本号
	 */
	String getVersion();

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
     * 读取车辆运动模式能量等级及对应模式回收调节档位
     * param 定义：
     *  param[0]        0:经济模式；1:运动模式；3:暴躁模式；4:NEDC模式
     *  param[1]        经济模式能量回收调节 0:低,1:中,2:高；
     *  param[2]        运动模式能量回收调节 0:低,1:中,2:高；
     */
    int getCar_SportEnergy(out int[] param);

    /**
     * 设置车辆运动模式能量等级及对应模式回收调节档位
     * param 定义：
     *  param[0]        0:经济模式；1:运动模式；3:暴躁模式；4:NEDC模式
     *  param[1]        经济模式能量回收调节 0:低,1:中,2:高；
     *  param[2]        运动模式能量回收调节 0:低,1:中,2:高；
     */
    int setCar_SportEnergy(out int[] param);

    /**
    * 获取能量回收开关
    * @return 0:能量回收开;1:能量回收关
    */
    int getPowerCycle();

    /**
    * 获取能量回收开关
    * @param isOnOff
    */
    int setPowerCycle(int isOnOff);

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

    /**
     * 获取车速、剩余里程
     * param 定义：
     * 	param[0]		车速
     * 	param[1]		剩余里程
     */
	int getCarInfo(out int[] param);
}

