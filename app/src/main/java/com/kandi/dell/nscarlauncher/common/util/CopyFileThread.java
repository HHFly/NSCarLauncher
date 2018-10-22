package com.kandi.dell.nscarlauncher.common.util;

import android.util.Log;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class CopyFileThread extends Thread{
    private String srcPath;//源文件
    private String destPath;//目标文件地址
    private long start,end;//start起始位置,end结束位置

    public CopyFileThread(String srcPath,String destPath,long start,long end){
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run(){
        try {
            long beginTimes = System.currentTimeMillis();
            Log.i("CopyFileThread","start:"+beginTimes);
            //创建只读的随机访问文件
            RandomAccessFile in = new RandomAccessFile(srcPath,"r");
            //创建可读写的随机访问文件
            RandomAccessFile out = new RandomAccessFile(destPath,"rw");
            //将输入跳转到指定位置
            in.seek(start);
            //从指定位置开始写
            out.seek(start);
            //文件输入通道
            FileChannel inChannel = in.getChannel();
            //文件输出通道
            FileChannel outChannel = out.getChannel();
            //锁住需要操作的区域,false代表锁住
            FileLock lock = outChannel.lock(start,(end-start),false);
            //将字节从此通道的文件传输到给定的可写入字节的输出通道
            inChannel.transferTo(start,(end-start),outChannel);
            lock.release();
            out.close();
            in.close();
            long endTimes = System.currentTimeMillis();
            Log.i("CopyFileThread",""+Thread.currentThread().getName()+"-alltime:"+(endTimes-beginTimes));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}