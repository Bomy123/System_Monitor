package com.skyworth.sm.entity;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangmingbao on 17-2-7.
 */

public class AppInfoEntity implements Parcelable{
    private Bitmap icon = null;
    private String pkgName =null;
    private String appLabel = null;
    private Long appSize = null;
    private String cacheSize = null;
    private String dataSize = null;
    private String codeSize = null;
    //default:the application is not running
    private int isRunning = 0;
    //default:the application is not sysApp
    private int isSysApp = 0;

    @Override
    public String toString() {
        return appLabel+":\n"+"  AppInfoEntity{" +
                "icon=" + icon +
                ", pkgName='" + pkgName + '\'' +
                ", appLabel='" + appLabel + '\'' +
                ", appSize='" + appSize + '\'' +
                ", cacheSize='" + cacheSize + '\'' +
                ", dataSize='" + dataSize + '\'' +
                ", codeSize='" + codeSize + '\'' +
                ", isRunning=" + isRunning +
                ", isSysApp=" + isSysApp +
                '}'+ "\n";
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }


    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(String codeSize) {
        this.codeSize = codeSize;
    }

    public int getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(int isRunning) {
        this.isRunning = isRunning;
    }


    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public Long getAppSize() {
        return appSize;
    }

    public void setAppSize(Long appSize) {
        this.appSize = appSize;
    }

    public int getIsSysApp() {
        return isSysApp;
    }

    public void setIsSysApp(int isSysApp) {
        this.isSysApp = isSysApp;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
//		out.writeParcelable(icon, flags);
		out.writeString(appLabel);
		out.writeLong(appSize);
		out.writeString(cacheSize);
		out.writeString(dataSize);
		out.writeString(codeSize);
		out.writeInt(isRunning);
		out.writeInt(isSysApp);
		out.writeString(pkgName);
	}
public static final Parcelable.Creator<AppInfoEntity> CREATOR = new Creator<AppInfoEntity>()
{

	@Override
	public AppInfoEntity createFromParcel(Parcel data) {
		// TODO Auto-generated method stub
		AppInfoEntity ai = new AppInfoEntity();
//		ai.setIcon((Bitmap) data.readParcelable(Bitmap.class.getClassLoader()));
		ai.setAppLabel(data.readString());
		ai.setAppSize(data.readLong());
		ai.setCacheSize(data.readString());
		ai.setDataSize(data.readString());
		ai.setCodeSize(data.readString());
		ai.setIsRunning(data.readInt());
		ai.setIsSysApp(data.readInt());
		ai.setPkgName(data.readString());
		return ai;
	}

	@Override
	public AppInfoEntity[] newArray(int arg0) {
		// TODO Auto-generated method stub
		return new AppInfoEntity[arg0];
	}
	
};



}
