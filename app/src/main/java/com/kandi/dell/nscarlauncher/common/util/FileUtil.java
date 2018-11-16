package com.kandi.dell.nscarlauncher.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

public class FileUtil {
	private static final String FOLDER_NAME = "/WebSnapShotImage";
	public static List<String> GetFiles(String Path, String Extension) 
	{
		List<String> lstFile = new ArrayList<String>();
	    File[] files =new File(Path).listFiles();
	    if(files != null) {
		    for (int i =0; i < files.length; i++)
		    {
		        File f = files[i];
		        if (f.isFile())
		        {
		        	if(Extension.contains(",")){
		        		String[] arrstr = Extension.split(",");
		        		for(int j=0;j<arrstr.length;j++){
		        			if (f.getPath().substring(f.getPath().length() - arrstr[j].length()).equals(arrstr[j])){
		        				lstFile.add(f.getPath());
		        			}
		        		}
		        	}else{
		        		if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)){
	        				lstFile.add(f.getPath());
	        			}
		        	}
		        }
		    }
	    }
	    return lstFile;
	}
	
	public static List<String> GetFiles2(String Path, String Extension) 
	{
		List<String> lstFile = new ArrayList<String>();
	    File[] files =new File(Path).listFiles();
	    if(files != null) {
		    for (int i =0; i < files.length; i++)
		    {
		        File f = files[i];
		        if (f.isFile())
		        {
		        	if(Extension.contains(",")){
		        		String[] arrstr = Extension.split(",");
		        		for(int j=0;j<arrstr.length;j++){
		        			if ((f.getPath().substring(f.getPath().length() - arrstr[j].length()).toLowerCase()).equals(arrstr[j])){
		        				lstFile.add(f.getPath());
		        			}
		        		}
		        	}else{
		        		if ((f.getPath().substring(f.getPath().length() - Extension.length()).toLowerCase()).equals(Extension)){
	        				lstFile.add(f.getPath());
	        			}
		        	}
		        }else if(f.isDirectory() && f.getPath().indexOf("/.") == -1){
//		        	GetFiles2(f.getAbsolutePath(), Extension, lstFile);
		        	
		        	String[] pathSplit = f.getPath().split("/");
		        	if (pathSplit.length > 1) {
		        		if ("Music".equals(pathSplit[1]) || "Movies".equals(pathSplit[1])) {
		        			GetFiles2(f.getAbsolutePath(), Extension, lstFile);
						}
					}
		        	
		        }
		    }
	    }
	    return lstFile;
	}
	
	public static List<String> GetFiles2(String Path, String Extension, List<String> lstFile) 
	{
	    File[] files =new File(Path).listFiles();
	    if(files != null) {
		    for (int i =0; i < files.length; i++)
		    {
		        File f = files[i];
		        if (f.isFile())
		        {
		        	if(Extension.contains(",")){
		        		String[] arrstr = Extension.split(",");
		        		for(int j=0;j<arrstr.length;j++){
		        			if ((f.getPath().substring(f.getPath().length() - arrstr[j].length()).toLowerCase()).equals(arrstr[j])){
		        				lstFile.add(f.getPath());
		        			}
		        		}
		        	}else{
		        		if ((f.getPath().substring(f.getPath().length() - Extension.length()).toLowerCase()).equals(Extension)){
	        				lstFile.add(f.getPath());
	        			}
		        	}
		        }else if(f.isDirectory() && f.getPath().indexOf("/.") == -1){
		        	GetFiles2(f.getAbsolutePath(), Extension, lstFile);
		        }
		    }
	    }
	    return lstFile;
	}
	
	/**
	 * 保存bitmap到文件
	 * @param filename
	 * @param bmp
	 * @return
	 */
	public static String saveBitmapToSDCard(String filename, Bitmap bmp) {
		
		// 文件相对路径
		String fileName = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 文件保存的路径
			String fileDir = Environment.getExternalStorageDirectory() + FOLDER_NAME;
			
			// 如果文件夹不存在，创建文件夹
			if (!createDir(fileDir)) {
				//System.out.println("不存在");
			}
			File file = null;
			FileOutputStream outStream = null;
			
			try {
				file = new File(fileDir, filename);
				
				fileName = file.toString();
				outStream = new FileOutputStream(fileName);
				if(outStream != null)
                {
                    bmp.compress(Bitmap.CompressFormat.PNG, 90, outStream);
                    outStream.close();
                }
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if (outStream != null) {
						outStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileName;
	}
	
	/**
	 * 创建指定路径的文件夹，并返回执行情况 ture or false
	 * @param filePath
	 * @return
	 */
	public static boolean createDir(String filePath) {
		File fileDir = new File(filePath);
		boolean bRet = true;
		if (!fileDir.exists()) {
			String[] aDirs = filePath.split("/");
			StringBuffer strDir = new StringBuffer();
			for (int i = 0; i < aDirs.length; i++) {
				fileDir = new File(strDir.append("/").append(aDirs[i]).toString());
				if (!fileDir.exists()) {
					if (!fileDir.mkdir()) {
						bRet = false;
						break;
					}
				}
			}
		}

		return bRet;
	}
	
	public static void deleteFile(File file) {
		if (file.exists()) { 
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles(); 
				for (int i = 0; i < files.length; i++) { 
					FileUtil.deleteFile(files[i]); 
				}
			}
			file.delete();
		} else {
			
		}
	}
	
	/** 复制文件 **/
	public static boolean copyFile(File src, File tar) {
		if (src.isFile()) {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				InputStream is = new FileInputStream(src);
				OutputStream op = new FileOutputStream(tar);
				bis = new BufferedInputStream(is);
				bos = new BufferedOutputStream(op);
				byte[] bt = new byte[1024 * 8];
				int len = bis.read(bt);
				while (len != -1) {
					bos.write(bt, 0, len);
					len = bis.read(bt);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (src.isDirectory()) {
			File[] f = src.listFiles();
			tar.mkdir();
			for (int i = 0; i < f.length; i++) {
				copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile() + File.separator
						+ f[i].getName()));
			}
		}
		return true;
	}
	
	/**
	  * 获取指定文件大小
	  * 
	  * @param f
	  * @return
	  * @throws Exception
	  */
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
			fis.close();
		}
		return size / 1024 / 1024;
	}
	
	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSizeLong();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocksLong();
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}
	
	public static long getUsbFreeSize() {
		// 取得SD卡文件路径
		File path = new File("/storage/udisk/");
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSizeLong();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocksLong();
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}
	
	public static String getString(InputStream inputStream) {  
	    InputStreamReader inputStreamReader = null;  
	    try {  
	        inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
	    } catch (UnsupportedEncodingException e1) {  
	        e1.printStackTrace();  
	    }  
	    BufferedReader reader = new BufferedReader(inputStreamReader);  
	    StringBuffer sb = new StringBuffer("");  
	    String line;  
	    try {  
	        while ((line = reader.readLine()) != null) {  
	            sb.append(line);  
	            sb.append("\n");  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return sb.toString();  
	}
}	
