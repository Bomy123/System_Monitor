package com.skyworth.sm.content;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.RemoteException;
import android.util.Log;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.skyworth.sm.entity.AppInfoEntity;

/**
 * Created by zhangmingbao on 17-2-7.
 */

public class InstalledApp {
   private List<ApplicationInfo> applicationInfoList = null;
   private List<AppInfoEntity> appInfoList = null;
    private Context mContext = null;
    private long codeSize = 0;
    private long cacheSize = 0;
    private long dataSize = 0;
    Boolean flag = true;
    PackageManager pm = null;
    public InstalledApp(Context context)
    {
        this.pm = context.getPackageManager();
        this.mContext = context;
    }
    public List<ApplicationInfo> getApplicationInfoList() {
        applicationInfoList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        return applicationInfoList;
    }


    public List<AppInfoEntity> getAppInfoList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //这个方法不明白
            getApplicationInfoList();
        appInfoList = new ArrayList<AppInfoEntity>();
        Bitmap iconbitmap = null;
        Drawable icon = null;
        for(ApplicationInfo applicationInfo:applicationInfoList)
        {
            AppInfoEntity appInfoEntity = new AppInfoEntity();
            
            icon = applicationInfo.loadIcon(pm);
            if(icon instanceof BitmapDrawable)
            {
            iconbitmap = ((BitmapDrawable) icon).getBitmap();
            }
            else
            {
            Bitmap	bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Config.ARGB_8888);
            	Canvas canvas = new Canvas(bitmap);
            	icon.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
            	icon.draw(canvas);
            	iconbitmap = bitmap;
            }
            appInfoEntity.setIcon(iconbitmap);
            appInfoEntity.setPkgName(applicationInfo.packageName);
            appInfoEntity.setIsSysApp((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0?0:1);
            pm.getPackageSizeInfo(applicationInfo.packageName,new PackageStatsObserver());
            while(flag);
            flag = true;
            appInfoEntity.setAppLabel(applicationInfo.loadLabel(pm).toString());
            appInfoEntity.setCacheSize(MemeryInfo.reSize2BKMG(cacheSize));
            appInfoEntity.setDataSize(MemeryInfo.reSize2BKMG(dataSize));
            appInfoEntity.setCodeSize(MemeryInfo.reSize2BKMG(codeSize));
            appInfoEntity.setAppSize(cacheSize+codeSize+dataSize);
            appInfoEntity.setIsRunning(isRunning(applicationInfo.packageName));
            appInfoList.add(appInfoEntity);
        }
//        System.out.println(appInfoList.toString());
        return appInfoList;
    }
public AppInfoEntity getAppSize(String pkgname)
{
	AppInfoEntity pEntity = new AppInfoEntity();
	setFlag(true);
	pm.getPackageSizeInfo(pkgname, new PackageStatsObserver());
	while (getFlag()) ;
	pEntity.setCacheSize(MemeryInfo.reSize2BKMG(cacheSize));
	pEntity.setDataSize(MemeryInfo.reSize2BKMG(dataSize));
	pEntity.setCodeSize(MemeryInfo.reSize2BKMG(codeSize));
	pEntity.setAppSize(cacheSize+codeSize+dataSize);
	return pEntity;
}
private class PackageStatsObserver extends IPackageStatsObserver.Stub{

    @Override
    public void onGetStatsCompleted(PackageStats packageStats, boolean b) throws RemoteException {
        codeSize = packageStats.codeSize;
        cacheSize  = packageStats.cacheSize;
        dataSize = packageStats.dataSize;
        flag = false;
    }
}
private void setFlag(boolean flag) {
	synchronized (this) {
		this.flag = flag;
	}
}
private boolean getFlag() {
	synchronized (this) {
		return flag;
	}
}
public int isRunning(String pkgname)
{
	int isRun = 0;
	ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
	for(ActivityManager.RunningAppProcessInfo ar:processInfoList)
	{
		if(ar.processName.equals(pkgname))
		{
			isRun = 1;
		}
	}
	return isRun;
	}
public Drawable getIcon(String pkgname) throws NameNotFoundException
{
	return pm.getApplicationIcon(pkgname);
	}

}
