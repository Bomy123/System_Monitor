package com.skyworth.system_monitor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.android.internal.R.integer;
import com.skyworth.sm.content.InstalledApp;
import com.skyworth.sm.content.MemeryInfo;
import com.skyworth.sm.entity.AppInfoEntity;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager.TaskThumbnails;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class App_Manage extends Activity implements OnClickListener, OnItemClickListener {
	private Button showAll, showRun, showUser, showSys;
	private ListView appListView;
	InstalledApp installedApp = null;
	List<AppInfoEntity> appInfoList = null;
	List<AppInfoEntity> appInfoListTmp = null;
	ProgressBar progressBar = null;
	Long sysAppSize = (long) 0;
	Long userAppSize = (long) 0;
	Boolean isFirstStart = true;
	static int memindex = -1;
	public static short needupdateitem = 0;
	public static  short needrefresh = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app__manage);
		initView();
		refreshdata();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		showAll.setFocusable(false);
//		showRun.setFocusable(false);
//		showUser.setFocusable(false);
//		showSys.setFocusable(false);
//		appListView.setFocusable(false);
		System.out.println("App_Manage1"+String.valueOf(needupdateitem));
		if(needrefresh == 0 || needupdateitem != 0)
		{
		refreshdata();
		}
	}

	private void initView() {

		showAll = (Button) findViewById(R.id.all);
		showAll.setOnClickListener(this);
		
		showRun = (Button) findViewById(R.id.running);
		showRun.setOnClickListener(this);
		
		showUser = (Button) findViewById(R.id.userapp);
		// showUser.setText(this.getResources().getString(R.string.userapptext)+"(总大小："+MemeryInfo.reSize2BKMG(userAppSize)+")");
		showUser.setOnClickListener(this);
		
		showSys = (Button) findViewById(R.id.sysapp);
		// showSys.setText(this.getResources().getString(R.string.sysapptext)+"(总大小："+MemeryInfo.reSize2BKMG(sysAppSize)+")");
		showSys.setOnClickListener(this);
		
		appListView = (ListView) findViewById(R.id.app_list);
		appListView.setOnItemClickListener(this);
		
		progressBar = (ProgressBar) findViewById(R.id.manageprogressbar);
	}

	private void refreshdata() {
		AsyncListTask asyncTask = new AsyncListTask();
		asyncTask.execute("");
	}

	@Override
	public void onClick(View v) {
		appInfoListTmp = new ArrayList<AppInfoEntity>();
		switch (v.getId()) {
		case R.id.all:
			appInfoListTmp = appInfoList;
			break;
		case R.id.running:
			for (AppInfoEntity ai : appInfoList) {
				if (ai.getIsRunning() == 1)
					appInfoListTmp.add(ai);
			}
			break;
		case R.id.userapp:
			for (AppInfoEntity ai : appInfoList) {
				if (ai.getIsSysApp() == 0)
					appInfoListTmp.add(ai);
			}
			break;
		case R.id.sysapp:
			for (AppInfoEntity ai : appInfoList) {
				if (ai.getIsSysApp() == 1)
					appInfoListTmp.add(ai);
			}
			break;
		}
		ListAdapter adapter = new MyAdapter(appInfoListTmp, this);
		appListView.setAdapter(adapter);
		appListView.invalidate();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Adapter adapter = parent.getAdapter();
		AppInfoEntity ai = (AppInfoEntity) adapter.getItem(position);
		memindex = position;
		System.out.println("App_Manage"+String.valueOf(memindex));
		Intent intent = new Intent(App_Manage.this, AppDetils.class);
		intent.putExtra("appInfo", ai);
		startActivity(intent);

	}

	class MyAdapter extends BaseAdapter {
		private List<AppInfoEntity> appInfoList = null;
		private Context mContext = null;

		public MyAdapter(List<AppInfoEntity> appInfoList, Context context) {
			this.appInfoList = appInfoList;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return appInfoList == null ? 0 : appInfoList.size();
		}

		@Override
		public AppInfoEntity getItem(int position) {
			return appInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			AppInfoEntity ai = getItem(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.app_item, null);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
				viewHolder.tv_app_name = (TextView) convertView.findViewById(R.id.app_name);
				viewHolder.tv_pkgname = (TextView) convertView.findViewById(R.id.pkgname);
				viewHolder.tv_app_size = (TextView) convertView.findViewById(R.id.app_size);
				viewHolder.tv_app_status = (TextView) convertView.findViewById(R.id.app_status);
				viewHolder.tv_app_level = (TextView) convertView.findViewById(R.id.app_level);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.icon.setImageBitmap(Bitmap.createScaledBitmap(ai.getIcon(), 80, 80, true));
			viewHolder.tv_app_name.setText(ai.getAppLabel());
			viewHolder.tv_pkgname.setText(ai.getPkgName());
			viewHolder.tv_app_size.setText(MemeryInfo.reSize2BKMG(ai.getAppSize()));
			viewHolder.tv_app_status
					.setText(ai.getIsRunning() == 0 ? mContext.getResources().getText(R.string.stop_text)
							: mContext.getResources().getText(R.string.running_text));
			viewHolder.tv_app_level.setText(ai.getIsSysApp() == 0 ? "用户应用" : "系统应用");
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					viewHolder.tag = "zhangmingbao我被点击了";
					System.out.print(viewHolder.tag);
				}
			});
			return convertView;
		}

		private final class ViewHolder {
			ImageView icon;
			TextView tv_app_name;
			TextView tv_pkgname;
			TextView tv_app_size;
			TextView tv_app_status;
			TextView tv_app_level;
			String tag;
		}
	}

	final class AsyncListTask extends AsyncTask<String, integer, ListAdapter> {

		@Override
		protected ListAdapter doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			installedApp = new InstalledApp(App_Manage.this);
			if (memindex == -1 || appInfoList == null ||appInfoList.isEmpty()) {
				try {
					appInfoList = installedApp.getAppInfoList();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (memindex >= 0 && memindex < appInfoList.size()) {
					if (needupdateitem == 1) {
						AppInfoEntity appInfoEntity = appInfoList.get(memindex);
						System.out.println("App_Manage set "+memindex+appInfoEntity.getPkgName() + appInfoEntity.getIsRunning());
						AppInfoEntity appInfoEntitynew = installedApp.getAppSize(appInfoEntity.getPkgName());
						appInfoEntity.setCacheSize(appInfoEntitynew.getCacheSize());
						appInfoEntity.setDataSize(appInfoEntitynew.getDataSize());
						appInfoEntity.setCodeSize(appInfoEntitynew.getCodeSize());
						appInfoEntity.setAppSize(appInfoEntitynew.getAppSize());
						appInfoEntity.setIsRunning(installedApp.isRunning(appInfoEntity.getPkgName()));
						appInfoList.set(memindex, appInfoEntity);
						System.out.println("App_Manage set "+appInfoEntity.getPkgName() + appInfoEntity.getIsRunning());
					} else {
						if (needupdateitem == -1) {
							appInfoList.remove(memindex);
						}
					}
				}
			}
			userAppSize = (long) 0;
			sysAppSize = (long) 0;
			for (AppInfoEntity appInfo : appInfoList) {
				if (appInfo.getIsSysApp() == 1) {
					sysAppSize += appInfo.getAppSize();
				} else {
					userAppSize += appInfo.getAppSize();
				}
			}
			ListAdapter adapter = new MyAdapter(appInfoList, App_Manage.this);
			memindex = -1;
			needupdateitem = 0;
			needrefresh = 0;
			return adapter;
		}

		@Override
		protected void onPostExecute(ListAdapter adapter) {
			// TODO Auto-generated method stub
			appListView.setAdapter(adapter);
			appListView.invalidate();
			showUser.setText(App_Manage.this.getResources().getString(R.string.userapptext) + "(总大小："
					+ MemeryInfo.reSize2BKMG(userAppSize) + ")");
			showSys.setText(App_Manage.this.getResources().getString(R.string.sysapptext) + "(总大小："
					+ MemeryInfo.reSize2BKMG(sysAppSize) + ")");
			showAll.setFocusable(true);
			showRun.setFocusable(true);
			showUser.setFocusable(true);
			showSys.setFocusable(true);
			appListView.setFocusable(true);
			showAll.requestFocus();
			progressBar.setVisibility(View.GONE);
			super.onPostExecute(adapter);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressBar.setVisibility(View.VISIBLE);
			showAll.setFocusable(false);
			showRun.setFocusable(false);
			showUser.setFocusable(false);
			showSys.setFocusable(false);
			appListView.setFocusable(false);
			super.onPreExecute();
		}

	}
}
