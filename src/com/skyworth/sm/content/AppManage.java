package com.skyworth.sm.content;

import com.skyworth.sm.helper.CreateObjectHelper;
import com.skyworth.system_monitor.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.content.pm.IPackageDataObserver;

/**
 * Created by zhangmingbao on 17-2-7.
 */

public class AppManage {
	PackageManager pm = null;
	private Context mContext = null;
	CallBack callBack = null;

	public AppManage(Object obj) {
		this.mContext = (Context) obj;
		this.pm = mContext.getPackageManager();
		this.callBack = (CallBack) obj;
	}

	private class ClearDataObserver extends IPackageDataObserver.Stub {
		CallBack callBack = null;

		public ClearDataObserver(CallBack cb) {
			this.callBack = cb;
		}

		public void onRemoveCompleted(final String packageName, final boolean succeeded) {
			callBack.onComplete(R.id.cleardata);

		}
	}

	private class ClearCacheObserver extends IPackageDataObserver.Stub {
		CallBack callBack = null;

		public ClearCacheObserver(CallBack cb) {
			this.callBack = cb;
		}

		public void onRemoveCompleted(final String packageName, final boolean succeeded) {
			callBack.onComplete(R.id.clearcache);
		}
	}

	private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
		CallBack callBack = null;

		public PackageDeleteObserver(CallBack cb) {
			this.callBack = cb;
		}

		@Override
		public void packageDeleted(String s, int i) throws RemoteException {
			callBack.onComplete(R.id.uninstall);
		}
	}

	public void uninstallApp(String pkgName) {
		pm.deletePackage(pkgName, new PackageDeleteObserver(callBack), 0);
	}

	public void clearUserData(String pkgName) {
		pm.clearApplicationUserData(pkgName, new ClearDataObserver(callBack));
	}

	public void clearCache(String pkgName) {
		pm.deleteApplicationCacheFiles(pkgName, new ClearCacheObserver(callBack));
	}

	public void forceStopApp(String pkgName) {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		am.forceStopPackage(pkgName);
		callBack.onComplete(R.id.forcestop);
	}

  public interface CallBack {
		void onComplete(int tag);
	}
}
