package com.skyworth.sm.content;

import android.annotation.SuppressLint;
import android.hardware.Camera.Size;
import android.icu.text.DecimalFormat;

/**
 * Created by zhangmingbao on 17-2-6.
 */

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.internal.view.BaseIWindow;
import com.skyworth.sm.entity.DataSpaceEntity;

public class MemeryInfo {
	StatFs sf = null;
	Socket socket;
    String buff = "";
    byte[] testbuff = null;
	@SuppressLint("NewApi")
	public DataSpaceEntity getDataSpace(LoadData.CallBack callBack) throws IOException, InterruptedException {
		Map<String, Long> dirInfoList = null;
		DataSpaceEntity ds = new DataSpaceEntity();
		File data = Environment.getDataDirectory();
		sf = new StatFs(data.getPath());
		long blockSize = sf.getBlockSizeLong();
		long totalSpace = sf.getTotalBytes();
		long freeSpace = sf.getFreeBlocksLong() * blockSize;
		ds.setTotalSpace(reSize2BKMG(totalSpace));
		ds.setFreeSpace(reSize2BKMG(freeSpace));
		ds.setUsedSpace(reSize2BKMG(totalSpace - freeSpace));
		getDirSizelist(data, callBack);
//		dirInfoList = getDirSizelist(new File("/data/data/com.iflytek.xiri"));
//		dirInfoList = getDirList(data);
//		System.out.print(dirInfoList == null ? "NULL" : dirInfoList.toString());
//		ds.setDirSizeUnderData(dirInfoList);
//		 ds.setAppUsed(reSize2BKMG(dirInfoList.get("/data/app")+dirInfoList.get("/data/data")));
//		 ds.setSysUsed(reSize2BKMG(dirInfoList.get("/data/app")+dirInfoList.get("/data/data")+dirInfoList.get("/data/dalvik-cache")));
//		 ds.setUserUsed(reSize2BKMG(dirInfoList.get("/data/user")));
		File sdcard = Environment.getExternalStorageDirectory();
		sf = new StatFs(sdcard.getPath());
		long SDtotalSpace = sf.getTotalBytes();
		long SDfreeSpace = sf.getFreeBlocksLong() * blockSize;
		ds.setSDtotalSpace(reSize2BKMG(SDtotalSpace));
		ds.setSDfreeSpace(reSize2BKMG(SDfreeSpace));
		ds.setSDusedSpace(reSize2BKMG(SDtotalSpace - SDfreeSpace));
//		dirInfoList = getDirSizelist(sdcard);
		dirInfoList = getDirList(sdcard);
		ds.setDirSizeUnderSDcard(dirInfoList);

		return ds;
	}

	public Map<String, Long> getDirSizelist(File dir) throws IOException {
		Map<String, Long> dirInfoList = new HashMap<String, Long>();
		File[] dirList = dir.listFiles();
		if (dir.isFile()) {
			dirInfoList.put(dir.getName(), dir.length());
			return dirInfoList;
		}
		long size = 0;
		if(dirList == null)
		{
			dirInfoList.put(dir.getName(), (long) 4096);
			return dirInfoList;
		}
		for (File subFile : dirList) {
			dirInfoList.put(subFile.getName(), getDirSize(subFile));
		}
		return dirInfoList;
	}
	public void getDirSizelist(File dir,LoadData.CallBack callBack) throws IOException {
		LoadData loadData = new LoadData(callBack,dir.getAbsolutePath());
		loadData.beginLoadData();
		
	}
	private long getDirSize(File dir) throws IOException {
		long size = 0;
		// System.out.println(dir.getAbsolutePath()+"11111111111111111111111111111111111111111111111111111111111111111111");
		if (dir.isFile()) {
			size += dir.length();
		} else {
			File[] fileArr = dir.listFiles();
			if (fileArr == null || fileArr.length == 0) {

				return size + 4096;
			}
			System.out.println(fileArr);
			int t = 1;
			for (File f : fileArr) {

				if (f.getAbsolutePath().contains(f.getCanonicalPath())) {
					if (t == 1) {
						size += getDirSize(f) + 4096;
						t = 2;
					} else {
						size += getDirSize(f);
					}
				} else {
					if (t == 1) {
						size += 4096;
						t = 2;
					}

				}
			}
		}
		return size;
	}

	public static String reSize2BKMG(float size) {
		if (size > 1024) {
			float size2K = size / 1024;
			if (size2K >= 1024) {
				float size2M = size2K / 1024;
				if (size2M >= 1024) {
					float size2G = size2M / 1024;
					return float2(size2G) + "G";
				} else {
					return float2(size2M) + "M";
				}
			} else {
				return float2(size2K) + "KB";
			}
		} else {
			return size + "Byte";
		}
	}
	private static float float2(float num)
	{
		return (float)Math.round(num * 100)/100;
	}
	private Map<String, Long> getDirList(File file) throws IOException, InterruptedException {
		Map<String, Long> dirInfoList = new HashMap<String, Long>();
		String pString = file.getAbsolutePath();
		int tag = pString.split("/").length+1;
		String cmd = "du -k "+file.getAbsolutePath();
		
		Process process = Runtime.getRuntime().exec(cmd);
		InputStream iStream = process.getInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
		String line;
		while((line = bReader.readLine())!=null)
		{
			
			System.out.println("zhangmingbao"+line);
			String[] dirInfo = line.toString().trim().split("/");
			if(dirInfo.length == tag)
			{
				dirInfoList.put(line.replace(dirInfo[0], ""), 1024*Long.parseLong(dirInfo[0].trim()));
			}
		}
		process.waitFor();
		iStream.close();
		bReader.close();
		process.destroy();
		return dirInfoList;
	}
//	public interface CallBack
//	{
//		void onLoadDataComplete(Map<String, Long> dirInfoList);
//		void onError(String msg);
//	}
//	class LoadData extends Thread {
//		CallBack callBack;
//		String path;
//		Map<String, Long> dirInfoList = new HashMap<String, Long>();
//		public LoadData(CallBack callBack,String path) {
//			// TODO Auto-generated constructor stub
//			this.callBack = callBack;
//			this.path = path;
//		}
//        @Override
//        public void run() {
//            super.run();
//            try {
//                System.out.println("socket.isConnected():5200");
//                socket = new Socket("127.0.0.1", 5200);
//                System.out.println("socket.isConnected():" + socket.isConnected());
//               
//                while (!socket.isConnected()) {
//                	 System.out.println("socket.isConnected() = false");
//                }
//                	System.out.println("send data:"+path);
//                    OutputStream outputStream = socket.getOutputStream();
//                    outputStream.write(("info"+path).getBytes("UTF-8"));
//                    outputStream.flush();
//                    testbuff = new byte[2048]; 
//                    System.out.println("receive  data:");
//                    while (testbuff[0] == 0) {
////                        System.out.println("testbuff:" + ());
//                        DataInputStream   dataInputStream = new DataInputStream(socket.getInputStream());
//                        dataInputStream.read(testbuff);
//                    }
//                    
//                    buff = new String(testbuff);
//                    
//                    final String mbuff = buff.substring(0, buff.lastIndexOf(".")+3);
//                    System.out.println("mbuff len:"+mbuff.length());
//                    if(mbuff.contains("di"))
//                    {
//                    	String string = "";
//                     	BufferedReader bReader = new BufferedReader(new FileReader(new File(mbuff)));
//                    	while((string = bReader.readLine()) != null)
//                    	{
//                    		System.out.println(string);
//                    		String[] strarr = string.trim().split("\\|");
//                    		System.out.println(strarr.toString());
//                    		System.out.println(strarr[0]+":"+strarr[1]);
//                    		dirInfoList.put(strarr[0], Long.parseLong(strarr[1].trim()));
//                    	}
//                    	callBack.onLoadDataComplete(dirInfoList);
//                    	
//                    	bReader.close();
//                    }
//                    else
//                    {
//                    	callBack.onError(mbuff);
//                    }
//                    outputStream.close();
//                    socket.close();
//                    testbuff = null;
////                    buff = "";
//                }
//            catch (IOException e) {
//            	callBack.onError("抱歉，数据加载失败了！！");;
//            }
//            finally {
//				if(socket != null && !socket.isClosed())
//				{
//					try {
//						socket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//        }
//    }
	
}
