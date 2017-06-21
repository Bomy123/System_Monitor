package com.skyworth.system_monitor;

import com.skyworth.sm.content.AppManage;
import com.skyworth.sm.content.InstalledApp;
import com.skyworth.sm.content.MemeryInfo;
import com.skyworth.sm.entity.AppInfoEntity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AppDetils extends Activity implements OnClickListener, AppManage.CallBack {

	TextView appname, applevel, appstatus, appsize, sizename, dataname, datasize, cachename, cachesize, codename,
			codesize;
	Button uninstallapp, forcestop, cleardata, clearcache;
	ImageView appicon;
	ProgressBar progress;
	String pkgname = null;
	AppManage am = null;
	InstalledApp appinfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detils);
		am = new AppManage(this);
		AppInfoEntity ai = (AppInfoEntity) getIntent().getParcelableExtra("appInfo");
		this.pkgname = ai.getPkgName();
		try {
			initView(ai);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initView(AppInfoEntity ai) throws NameNotFoundException {
		appinfo = new InstalledApp(this);
		appicon = (ImageView) findViewById(R.id.appdetailicon);
		BitmapDrawable bd = (BitmapDrawable) appinfo.getIcon(pkgname);
		Bitmap icontmp = bd.getBitmap();
		Bitmap icon = Bitmap.createScaledBitmap(icontmp, 120, 120, true);
		appicon.setImageBitmap(icon);
		appname = (TextView) findViewById(R.id.app_detail_name);
		appname.setText(ai.getAppLabel());
		appstatus = (TextView) findViewById(R.id.app_detail_status);
		appstatus.setText(ai.getIsRunning() == 0 ? this.getResources().getText(R.string.stop_text)
				: this.getResources().getText(R.string.running_text));
		applevel = (TextView) findViewById(R.id.app_detail_level);
		applevel.setText(ai.getIsSysApp() == 0 ? this.getResources().getText(R.string.userapp_text)
				: this.getResources().getText(R.string.sysapp_text));
		appsize = (TextView) findViewById(R.id.size_total);
		appsize.setText(MemeryInfo.reSize2BKMG(ai.getAppSize()));
		datasize = (TextView) findViewById(R.id.data_size);
		datasize.setText(ai.getDataSize());
		cachesize = (TextView) findViewById(R.id.cache_size);
		cachesize.setText(ai.getCacheSize());
		codesize = (TextView) findViewById(R.id.code_size);
		codesize.setText(ai.getCodeSize());

		uninstallapp = (Button) findViewById(R.id.uninstall);
		uninstallapp.setOnClickListener(this);
		if (ai.getIsSysApp() == 1) {
			uninstallapp.setFocusable(false);
			uninstallapp.setClickable(false);
			uninstallapp.setAlpha(0.3f);
		}
		forcestop = (Button) findViewById(R.id.forcestop);
		forcestop.setOnClickListener(this);
		if (ai.getIsRunning() == 0) {
			forcestop.setFocusable(false);
			forcestop.setClickable(false);
			forcestop.setAlpha(0.3f);
		}
		cleardata = (Button) findViewById(R.id.cleardata);
		cleardata.setOnClickListener(this);
		clearcache = (Button) findViewById(R.id.clearcache);
		clearcache.setOnClickListener(this);
		progress = (ProgressBar) findViewById(R.id.progressbar);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.uninstall:
			progress.setVisibility(View.VISIBLE);
			am.uninstallApp(pkgname);
			break;
		case R.id.forcestop:
			progress.setVisibility(View.VISIBLE);
			am.forceStopApp(pkgname);
			break;
		case R.id.cleardata:
			progress.setVisibility(View.VISIBLE);
			am.clearUserData(pkgname);
			break;
		case R.id.clearcache:
			progress.setVisibility(View.VISIBLE);
			am.clearCache(pkgname);
			break;
		}

	}

	@Override
	public void onComplete(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.uninstall:
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					App_Manage.needupdateitem = -1;
					App_Manage.needrefresh = 1;
					Intent intent = new Intent(AppDetils.this, App_Manage.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}).start();
//			this.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Toast.makeText(AppDetils.this, "应用成功卸载，正在跳转页面。。。。", Toast.LENGTH_LONG).show();
//					progress.setVisibility(View.GONE);
//				}
//			});
			break;
		case R.id.forcestop:
			App_Manage.needupdateitem = 1;
			App_Manage.needrefresh = 1;
			this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					InstalledApp app = new InstalledApp(AppDetils.this);
					if (app.isRunning(pkgname) == 0) {
						appstatus.setText(AppDetils.this.getResources().getText(R.string.stop_text));
						forcestop.setFocusable(false);
						forcestop.setClickable(false);
						forcestop.setAlpha(0.3f);
					}
					progress.setVisibility(View.GONE);
				}
			});

			break;
		case R.id.cleardata:
			App_Manage.needupdateitem = 1;
			App_Manage.needrefresh = 1;
			this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					datasize.setText("0KB");
					cleardata.setFocusable(false);
					cleardata.setClickable(false);
					cleardata.setAlpha(0.3f);
					progress.setVisibility(View.GONE);
				}
			});

			break;
		case R.id.clearcache:
			App_Manage.needupdateitem = 1;
			App_Manage.needrefresh = 1;
			this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					cachesize.setText("0KB");
					clearcache.setFocusable(false);
					clearcache.setClickable(false);
					clearcache.setAlpha(0.3f);
					progress.setVisibility(View.GONE);
				}
			});

			break;
		}
	}
}
