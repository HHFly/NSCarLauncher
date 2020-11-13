package com.nushine.nshlbus.com.driverlayer.os_driverJ1939;

public class J1939ConstFlag {
	public enum J1939_FrameType{
		CAN_FRAMTYPE_STD,
		CAN_FRAMTYPE_EXT
	}
	public enum J1939_DataType{
		CAN_RTR_DATA,
		CAN_RTR_REMOTE
	}
	public enum J1939_Error{
		J1939_ERR_OK,
		J1939_ERR_SEND
	}
}
