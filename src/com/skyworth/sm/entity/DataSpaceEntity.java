package com.skyworth.sm.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skyworth.sm.content.MemeryInfo;

import android.icu.text.LocaleDisplayNames.DialectHandling;

/**
 * Created by zhangmingbao on 17-2-6.
 */

public class DataSpaceEntity {
    public String getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(String totalSpace) {
        this.totalSpace = totalSpace;
    }

    public String getAppUsed() {
        return appUsed;
    }

    public void setAppUsed(String appUsed) {
        this.appUsed = appUsed;
    }

    public String getUserUsed() {
        return userUsed;
    }

    public void setUserUsed(String userUsed) {
        this.userUsed = userUsed;
    }

    public String getSysUsed() {
        return sysUsed;
    }

    public void setSysUsed(String sysUsed) {
        this.sysUsed = sysUsed;
    }

//    public Map<String, Long> getDirSizeUnderData() {
//        return dirSizeUnderData;
//    }
//
//    public void setDirSizeUnderData(Map<String, Long> dirSizeUnderData) {
//        this.dirSizeUnderData = dirSizeUnderData;
//    }

    public String getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(String usedSpace) {
        this.usedSpace = usedSpace;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }
    public String dataInfotoString() {
    	StringBuilder sBuilder  = new StringBuilder();
    	sBuilder.append("data分区空间总大小：")
    	.append(totalSpace)
    	.append("\n")
    	.append("data分区剩余空间大小：")
    	.append(freeSpace)
    	.append("\n")
    	.append("data分区已使用空间大小：")
    	.append(usedSpace)
    	.append("\n")
    	.append("系统占用空间大小(app+data+dalvik-cache+app-lib)：")
    	.append(sysUsed)
    	.append("\n")
    	.append("应用占用空间大小(app+data+app-lib)：")
    	.append(appUsed)
    	.append("\n")
    	.append("各文件夹使用情况如下：")
    	.append("\n");
    	for(DirDetails details:dList)
    	{
    		sBuilder.append(details.getfName())
    		.append("  [")
    		.append(details.getfType())
    		.append("]             ")
    		.append(MemeryInfo.reSize2BKMG(details.getFsize()))
    		.append("\n");
    	}
    	return sBuilder.toString();
    }
//    public String dataInfotoString() {
//    	String str = "data分区空间总大小："+totalSpace + "\n"+
//        		"data分区剩余空间大小："+freeSpace + "\n"+
//        		"data分区已使用空间大小："+usedSpace + "\n"+
//        		"系统占用空间大小(app+data+dalvik-cache+app-lib)："+sysUsed+"\n"+
//        		"应用占用空间大小(app+data+app-lib)："+appUsed+"\n"+
//        		"用户占用空间大小(user)："+userUsed+"\n"+
//        		"各文件夹使用情况如下：" + "\n";
//    	Set<String> ks = dirSizeUnderData.keySet();
//    	Iterator<String> it = ks.iterator();
//    	while(it.hasNext())
//    	{
//    		String key = it.next();
//    		str += key + ":                     " + MemeryInfo.reSize2BKMG(dirSizeUnderData.get(key))+"\n";
//    	}
//        return str;
//    }
    public String sdcardInfotoString() {
    	StringBuilder sBuilder = new StringBuilder();
    	sBuilder.append("SDcard空间总大小：")
    	.append(SDtotalSpace)
    	.append("\n")
    	.append("SDcard剩余空间大小：")
    	.append(SDfreeSpace)
    	.append("\n")
    	.append("各文件夹使用情况如下：\n");
    	Set<String> ks = dirSizeUnderSDcard.keySet();
    	Iterator<String> it = ks.iterator();
    	while(it.hasNext())
    	{
    		String key = it.next();
    		sBuilder.append(key)
    		.append("          ")
    		.append(MemeryInfo.reSize2BKMG(dirSizeUnderSDcard.get(key)))
    		.append("\n");
    	}
        return sBuilder.toString();
    }
    public String getSDtotalSpace() {
		return SDtotalSpace;
	}

	public void setSDtotalSpace(String sDtotalSpace) {
		SDtotalSpace = sDtotalSpace;
	}

	public String getSDfreeSpace() {
		return SDfreeSpace;
	}

	public void setSDfreeSpace(String sDfreeSpace) {
		SDfreeSpace = sDfreeSpace;
	}

	public String getSDusedSpace() {
		return SDusedSpace;
	}
public void getDADsize() {
	for(DirDetails details:dList)
	{
		if(details.getfName().equals("data"))
		{
			setDatadirsize(details.getFsize());
		}
		if(details.getfName().equals("app"))
		{
			setAppdirsize(details.getFsize());
		}
		if(details.getfName().equals("data"))
		{
			setDalvik_cachesize(details.getFsize());
		}
	}
}
	public void setSDusedSpace(String sDusedSpace) {
		SDusedSpace = sDusedSpace;
	}

	public Map<String, Long> getDirSizeUnderSDcard() {
		return dirSizeUnderSDcard;
	}

	public void setDirSizeUnderSDcard(Map<String, Long> dirSizeUnderSDcard) {
		this.dirSizeUnderSDcard = dirSizeUnderSDcard;
	}
    public List<DirDetails> getdList() {
		return dList;
	}

	public void setdList(List<DirDetails> dList) {
		this.dList = dList;
	}
    public long getDatadirsize() {
    	if(datadirsize == 0)
    	{
    		getDADsize();
    	}
		return datadirsize;
	}

	private void setDatadirsize(long datadirsize) {
		this.datadirsize = datadirsize;
	}

	public long getAppdirsize() {
    	if(appdirsize == 0)
    	{
    		getDADsize();
    	}
		return appdirsize;
	}

	private void setAppdirsize(long appdirsize) {
		this.appdirsize = appdirsize;
	}

	public long getDalvik_cachesize() {
    	if(dalvik_cachesize == 0)
    	{
    		getDADsize();
    	}
		return dalvik_cachesize;
	}

	private void setDalvik_cachesize(long dalvik_cachesize) {
		this.dalvik_cachesize = dalvik_cachesize;
	}
public DirDetails getDirDetailsObj() {
	return new DirDetails();
}
    String totalSpace = null;
    String freeSpace = null;
    String usedSpace = null;
	String SDtotalSpace = null;
    String SDfreeSpace = null;
    String SDusedSpace = null;
   // Map<String, Long> dirSizeUnderData = new HashMap<String, Long>();
    List<DirDetails> dList = new ArrayList<DataSpaceEntity.DirDetails>();
	Map<String, Long> dirSizeUnderSDcard = new HashMap<String, Long>();
    String sysUsed = null;
    String userUsed = null;
    String appUsed = null;
    long datadirsize = 0;
	long appdirsize = 0;
    long dalvik_cachesize = 0;
public class DirDetails
{
	private String fName  = null;
	private String fType = "普通文件";
	private long fsize = 0;
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getfType() {
		return fType;
	}
	public void setfType(String fType) {
		this.fType = fType;
	}
	public long getFsize() {
		return fsize;
	}
	public void setFsize(long fsize) {
		this.fsize = fsize;
	}
}

}
