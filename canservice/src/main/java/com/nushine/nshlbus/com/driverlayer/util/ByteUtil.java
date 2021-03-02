package com.nushine.nshlbus.com.driverlayer.util;

public  class ByteUtil {
        /**
         * 拼接字节到字节数组中
         *
         * @param paramArrayOfByte 原始字节数组
         * @param paramByte        要拼接的字节
         * @return 拼接后的数组
         */
        public static byte[] MergerArray(byte[] paramArrayOfByte, byte paramByte) {
            byte[] arrayOfByte = new byte[paramArrayOfByte.length + 1];
            System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
            arrayOfByte[paramArrayOfByte.length] = paramByte;
            return arrayOfByte;
        }

        /**
         * 两个字节数组拼接
         *
         * @param paramArrayOfByte1 字节数组1
         * @param paramArrayOfByte2 字节数组2
         * @return 拼接后的数组
         */
        public static byte[] MergerArray(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
            byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramArrayOfByte2.length];
            System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
            System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length, paramArrayOfByte2.length);
            return arrayOfByte;
        }

    public static byte[] MergerArray(byte[]... bytes) {
        int total_length = 0;
        for (int i=0;i<bytes.length;i++){
            total_length += bytes[i].length;
        }
        byte[] arrayOfByte = new byte[total_length];
        int count_length = 0;
        for (int i=0;i<bytes.length;i++){
            System.arraycopy(bytes[i], 0, arrayOfByte, count_length, bytes[i].length);
            count_length += bytes[i].length;
        }
        return arrayOfByte;
    }

}
