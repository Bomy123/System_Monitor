package com.skyworth.sm.content;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skyworth.sm.entity.DataSpaceEntity;



public class LoadData extends Thread {
	Socket socket;
    String buff = "";
    byte[] testbuff = null;
	CallBack callBack;
	String path;
	List<DataSpaceEntity.DirDetails> dList = new ArrayList<DataSpaceEntity.DirDetails>();
	Map<String, Long> dirInfoList = new HashMap<String, Long>();
	public LoadData(CallBack callBack,String path) {
		// TODO Auto-generated constructor stub
		this.callBack = callBack;
		this.path = path;
	}
	public void beginLoadData() {
		start();
	}
    @Override
    public void run() {
        super.run();
        try {
            System.out.println("socket.isConnected():5200");
            socket = new Socket("127.0.0.1", 5200);
            System.out.println("socket.isConnected():" + socket.isConnected());
           
            while (!socket.isConnected()) {
            	 System.out.println("socket.isConnected() = false");
            }
            	System.out.println("send data:"+path);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(("info"+path).getBytes("UTF-8"));
                outputStream.flush();
                testbuff = new byte[2048]; 
                System.out.println("receive  data:");
                while (testbuff[0] == 0) {
//                    System.out.println("testbuff:" + ());
                    DataInputStream   dataInputStream = new DataInputStream(socket.getInputStream());
                    dataInputStream.read(testbuff);
                }
                
                buff = new String(testbuff);
                
                final String mbuff = buff.substring(0, buff.lastIndexOf(".")+3);
                //System.out.println("mbuff len:"+mbuff.length());
                if(mbuff.contains("di"))
                {
                	String string = "";
                 	BufferedReader bReader = new BufferedReader(new FileReader(new File(mbuff)));
                 	DataSpaceEntity ds = new DataSpaceEntity();
                	while((string = bReader.readLine()) != null)
                	{
                		DataSpaceEntity.DirDetails details = ds.getDirDetailsObj();
                		
                		System.out.println(string);
                		String[] strarr = string.trim().split("\\|");
                		//System.out.println(strarr.toString());
                		//System.out.println(strarr[0]+":"+strarr[1]);
                		details.setfName(strarr[0]);
                		details.setfType(strarr[1]);
                		details.setFsize(Long.parseLong(strarr[2].trim()));
                		dList.add(details);
                	}
                	callBack.onLoadDataComplete(dList);
                	
                	bReader.close();
                }
                else
                {
                	callBack.onError(mbuff);
                }
                outputStream.close();
                socket.close();
                testbuff = null;
//                buff = "";
            }
        catch (IOException e) {
        	callBack.onError("抱歉，数据加载失败了！！");;
        }
        finally {
			if(socket != null && !socket.isClosed())
			{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    public interface CallBack
    {
    	void onLoadDataComplete(List<DataSpaceEntity.DirDetails> dList);
    	void onError(String msg);
    }
}
