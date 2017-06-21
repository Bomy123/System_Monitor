package com.skyworth.system_monitor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.skyworth.system_monitor.R;
import com.skyworth.sm.content.AppManage;
import com.skyworth.sm.content.LoadData;
import com.skyworth.sm.content.MemeryInfo;
import com.skyworth.sm.entity.DataSpaceEntity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AppManage.CallBack,LoadData.CallBack{

	long newfreeSpace = 0;
	long freeSpace = 0;
    TextView datatv = null;
    TextView sdcardtv = null;
    MemeryInfo dsi = new MemeryInfo();
    Object obj = new Object();
    Boolean firstclearflag = true;
    ProgressBar progressBar;
    DataSpaceEntity ds = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.mainprogressbar);
        datatv = (TextView) findViewById(R.id.spaceContent);
        datatv.setMovementMethod(ScrollingMovementMethod.getInstance());
        datatv.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
				{
					datatv.setBackgroundColor(Color.LTGRAY);
					datatv.setTextSize(20);
				}
				else
				{
					datatv.setBackgroundColor(0);
					datatv.setTextSize(15);
					datatv.scrollTo(0, 0);
				}
			}});
        sdcardtv = (TextView) findViewById(R.id.sdcardContent);
        sdcardtv.setMovementMethod(ScrollingMovementMethod.getInstance());
        sdcardtv.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
				{
					sdcardtv.setBackgroundColor(Color.LTGRAY);
					sdcardtv.setTextSize(20);
				}
				else
				{
					sdcardtv.setBackgroundColor(0);
					sdcardtv.setTextSize(15);
					sdcardtv.scrollTo(0, 0);
				}
			}});
        Button ambtn = (Button) findViewById(R.id.appmanage);
        ambtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,App_Manage.class);
				startActivity(intent);
			}});
        Button clearcachebtn = (Button) findViewById(R.id.clearallcache);
        clearcachebtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				progressBar.setVisibility(View.VISIBLE);
				// TODO Auto-generated method stub
				try {
					clearallcache();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
        Button cleardatabtn = (Button) findViewById(R.id.clearalldata);
        cleardatabtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				progressBar.setVisibility(View.VISIBLE);
				// TODO Auto-generated method stub
				try {
					clearalldata();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}});
    }
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	try {
		ds = dsi.getDataSpace(this);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//datatv.setText(ds.dataInfotoString());
	sdcardtv.setText(ds.sdcardInfotoString());
}
	@Override
	public void onComplete(int id) {
		// TODO Auto-generated method stub
		synchronized(obj)
		{
		System.out.println("1111111111111111111111111114444444444444444444444444444444444444444444444444444444");
		obj.notify();
		System.out.println("1111111111111111111111111115555555555555555555555555555555555555555555555555555554");
		}
	}

	public void clearallcache() throws InterruptedException, IOException
	{
		freeSpace = getFreeSpace();
		PackageManager pm = this.getPackageManager();
		AppManage am = new AppManage(this);
		List<ApplicationInfo> applicationlist = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(ApplicationInfo ai:applicationlist)
		{
			synchronized(obj){
			System.out.println("1111111111111111111111222222222222222222222222222222222222222222222222222");
			if(!firstclearflag)
			{
			   obj.wait();
			}
			firstclearflag = false;
			System.out.println("11111111111111111111113333333333333333333333333333333333333333333333333333");
			if(!ai.packageName.endsWith(this.getPackageName()))
			{
				am.clearCache(ai.packageName);
			}
			else
			{
				firstclearflag = true;
			}
			}
		}
		ds = dsi.getDataSpace(this);
		//datatv.setText(ds.dataInfotoString());
		sdcardtv.setText(ds.sdcardInfotoString());
		newfreeSpace = getFreeSpace();
		Toast.makeText(this, "此次清理共节省"+MemeryInfo.reSize2BKMG((newfreeSpace-freeSpace))+"空间", Toast.LENGTH_LONG).show();
		firstclearflag = true;
		//progressBar.setVisibility(View.GONE);
	}
	public void clearalldata() throws InterruptedException, IOException
	{
		freeSpace = getFreeSpace();
		PackageManager pm = this.getPackageManager();
		AppManage am = new AppManage(this);
		List<ApplicationInfo> applicationlist = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(ApplicationInfo ai:applicationlist)
		{
			synchronized(obj)
			{
			System.out.println("1111111111111111111111222222222222222222222222222222222222222222222222222");
			if(!firstclearflag)
			{
			   obj.wait();
			}
			}
			firstclearflag = false;
			System.out.println("11111111111111111111113333333333333333333333333333333333333333333333333333");
			if(!ai.packageName.endsWith(this.getPackageName()))
			{
				am.clearUserData(ai.packageName);
			}
			else
			{
				firstclearflag = true;
			}
		}
		ds = dsi.getDataSpace(this);
		//datatv.setText(ds.dataInfotoString());
		sdcardtv.setText(ds.sdcardInfotoString());
		newfreeSpace = getFreeSpace();
		Toast.makeText(this, "此次清理共节省"+MemeryInfo.reSize2BKMG((newfreeSpace-freeSpace))+"空间", Toast.LENGTH_LONG).show();
		firstclearflag = true;
		//progressBar.setVisibility(View.GONE);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public long getFreeSpace()
	{
		StatFs sf = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = sf.getBlockSizeLong();
		return sf.getFreeBlocksLong()*blockSize;
	}

	@Override
	public void onLoadDataComplete(List<DataSpaceEntity.DirDetails> dList) {
		// TODO Auto-generated method stub
		System.out.println("ds:"+ds);
		while(ds == null);
		//Map<String, Long> map = ds.getDirSizeUnderData();
		
//		if (map.isEmpty()) {
//			System.out.println(map+"123");
//		}else {
//			System.out.println("123");
//		}
		
		
		while(!ds.getdList().isEmpty());
		ds.setdList(dList);
		 ds.setAppUsed(MemeryInfo.reSize2BKMG(ds.getAppdirsize()+ds.getDatadirsize()));
		 ds.setSysUsed(MemeryInfo.reSize2BKMG(ds.getAppdirsize()+ds.getDatadirsize()+ds.getDalvik_cachesize()));
		 ///ds.setUserUsed(MemeryInfo.reSize2BKMG(dirInfoList.get("user")));
		 this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				datatv.setText(ds.dataInfotoString());
				//datatv.setText("124424242442424244244442224244");
				System.out.println(ds.dataInfotoString());
				datatv.invalidate();
				progressBar.setVisibility(View.GONE);
			}
		});
		 
	}

	@Override
	public void onError(String msg) {
		// TODO Auto-generated method stub
		final String mString = msg;
		 this.runOnUiThread(new Runnable() {
				
			@Override
			public void run() {
				// TODO Auto-generated method stub
				datatv.setText("get data subdir info error,msg:"+mString);
				progressBar.setVisibility(View.GONE);
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}
}
