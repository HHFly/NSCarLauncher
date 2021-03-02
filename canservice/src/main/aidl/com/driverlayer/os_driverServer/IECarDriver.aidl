// IECarDriver.aidl
package com.driverlayer.os_driverServer;

// Declare any non-default types here with import statements
interface IECarDriver{
    /**
	 * 获取接口实现的版本号
	 * @return 版本号
	 */
	String getVersion();

	/**
     * 读取车辆运动模式能量等级及对应模式回收调节档位
     * param 定义：
     *  param[0]
            Bit8  左转向开关  0：无效；1：有效
            Bit7  右转向开关  0：无效；1：有效
            Bit6  灯光总开关  0：无效；1：有效
            Bit5  近光灯开关  0：无效；1：有效
            Bit4  远光灯开关  0：无效；1：有效
            Bit3  超车灯开关  0：无效；1：有效
            Bit2～1  保留  保留
        param[1]
            Bit8  雨刮间歇档开关  0：无效；1：有效
            Bit7  雨刮低速挡开关  0：无效；1：有效
            Bit6  雨刮高速挡开关  0：无效；1：有效
            Bit5  喷淋开关  0：无效；1：有效
            Bit4～3  保留  保留
            Bit2  自动大灯开关  0：无效；1：有效
            Bit1  自动雨刮开关  0：无效；1：有效
        param[2]
            后雾灯 0：无效；1：有效
        param[3]
            主照明灯1 0：无效；1：有效
        param[4]
            主照明灯2 0：无效；1：有效
     */
    int getCar_Status(out int[] param);

    /**
     * 设置车辆运动模式能量等级及对应模式回收调节档位
     * param 定义：
     *  param[0]
            Bit8  左转向开关  0：无效；1：有效
            Bit7  右转向开关  0：无效；1：有效
            Bit6  灯光总开关  0：无效；1：有效
            Bit5  近光灯开关  0：无效；1：有效
            Bit4  远光灯开关  0：无效；1：有效
            Bit3  超车灯开关  0：无效；1：有效
            Bit2～1  保留  保留
        param[1]
            Bit8  雨刮间歇档开关  0：无效；1：有效
            Bit7  雨刮低速挡开关  0：无效；1：有效
            Bit6  雨刮高速挡开关  0：无效；1：有效
            Bit5  喷淋开关  0：无效；1：有效
            Bit4～3  保留  保留
            Bit2  自动大灯开关  0：无效；1：有效
            Bit1  自动雨刮开关  0：无效；1：有效
        param[2]
            后雾灯 0：无效；1：有效
        param[3]
            主照明灯1 0：无效；1：有效
        param[4]
            主照明灯2 0：无效；1：有效
     */
    int setCar_Status(in int[] param);
}

