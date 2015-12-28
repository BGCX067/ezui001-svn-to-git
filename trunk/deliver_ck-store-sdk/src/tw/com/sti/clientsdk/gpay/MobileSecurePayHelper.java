/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package tw.com.sti.clientsdk.gpay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.sti.clientsdk.ResourceFactory;
import tw.com.sti.store.api.android.util.Logger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class MobileSecurePayHelper
{
	private static final Logger L=Logger.getLogger(MobileSecurePayHelper.class);
	private static final ResourceBundle resources = ResourceFactory.getResourceBundle();
	
	private ProgressDialog mProgress = null;
	Context mContext = null;
	
	public MobileSecurePayHelper(Context context)
	{
		this.mContext = context;
	}
	
	public boolean  detectMobile_sp()
	{
		boolean isMobile_spExist = isMobile_spExist();
		if( !isMobile_spExist )
		{
			//
			// get the cacheDir.
			File cacheDir 	 = mContext.getCacheDir();
			final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
	
			//
			// 捆绑安装
			retrieveApkFromAssets(mContext, "mobile_sp.apk", cachePath);
			
			mProgress = BaseHelper.showProgress(mContext, null, resources.getString("msg_check_safe_pay_service"), 
						false, true);
			
			new Thread(new Runnable() {
				public void run()
				{
					//
					// 检测是否有新的版本。
					PackageInfo apkInfo = getApkInfo(mContext, cachePath);
					String newApkdlUrl = checkNewUpdate(apkInfo);
					
					//
					// 动态下载
					final String apkUrl = "http://msp.alipay.com/download/mobile_sp.apk";
					if( newApkdlUrl != null )
						retrieveApkFromNet(mContext, newApkdlUrl, cachePath);
					
					// send the result back to caller.
					Message msg = new Message();
					msg.what = AlixId.RQF_INSTALL_CHECK;
					msg.obj	 = cachePath;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					mHandler.sendMessage(msg);
				}
			}).start();
		}
		// else ok.
		
		return isMobile_spExist;
	}
	
	public void showInstallConfirmDialog(final Context context, final String cachePath)
	{
		AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
//		tDialog.setIcon(R.drawable.info);
		tDialog.setTitle(resources.getString("dialog_confirm_install_hint"));
		tDialog.setMessage(resources.getString("dialog_confirm_install"));

		tDialog.setPositiveButton(resources.getString("submit"),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{
						//
						// 修改apk权限
						BaseHelper.chmod("777", cachePath);
						
						//
						// install the apk.
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");
						context.startActivity(intent);
					}
				});

		tDialog.setNegativeButton(resources.getString("cancel"),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which)
					{
					}
				});

		tDialog.show();
	}
	//	check app is install
	public boolean isMobile_spExist()
	{
		PackageManager manager = mContext.getPackageManager(); 
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for( int i = 0; i < pkgList.size(); i++ )
		{
			PackageInfo pI = pkgList.get(i);
			if( pI.packageName.equalsIgnoreCase("com.alipay.android.app") )
				return true;
		}
		
		return false;
	}	
	
	//
	// 捆绑安装
	public boolean retrieveApkFromAssets(Context context, String fileName, String path)
	{
		boolean bRet = false;
		
		
		try
		{
			InputStream is = context.getAssets().open(fileName);
	
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new  FileOutputStream(file);

			byte[] temp 	= new byte[1024];
			int i 			= 0;
			while( (i = is.read(temp)) > 0 )
			{
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();
			
			bRet = true;

		}
		catch (IOException e)
		{
			L.e("alipay error", e);
		}
		
		return bRet;
	} 	
	
	/** 获取未安装的APK信息
	 * @param context
	 * @param archiveFilePath APK文件的路径。如：/sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath)
	{
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_META_DATA);
		return apkInfo;
	}
	
	// 
	// 检查是否有新的版本，如果有，返回apk的下载地址。
	public String checkNewUpdate(PackageInfo packageInfo)
	{
		String url = null;	

		try
		{
			JSONObject resp = sendCheckNewUpdate(packageInfo.versionName);
			//JSONObject resp = sendCheckNewUpdate("1.0.0");
			if( resp.getString("needUpdate").equalsIgnoreCase("true") )
			{
				url = resp.getString("updateUrl");
			}
			// else ok.
		}
		catch(Exception e)
		{
			L.e("alipay error", e);
		}
		
		return url;
	}	
	
	public JSONObject sendCheckNewUpdate(String versionName)
	{
		JSONObject objResp = null;
		try
		{
			JSONObject req = new JSONObject();
			req.put(AlixDefine.action, AlixDefine.actionUpdate);
			
			JSONObject data = new JSONObject();
			data.put(AlixDefine.platform, "android");
			data.put(AlixDefine.VERSION, versionName);
			data.put(AlixDefine.partner, "");
			
			req.put(AlixDefine.data, data);
			
			objResp = sendRequest( req.toString() );
		}
		catch (JSONException e)
		{
			L.e("alipay error", e);
		}
		
		return objResp;
	}
	
	public JSONObject sendRequest(final String content)
	{
		NetworkManager nM = new NetworkManager(this.mContext);
		
		//
		JSONObject jsonResponse = null;
		try
		{
			String response = null;

			synchronized (nM)
			{
				//
				response = nM.SendAndWaitResponse( content, "https://msp.alipay.com/x.htm" );
			}
			
			jsonResponse = new JSONObject(response);
		}
		catch (Exception e)
		{
			L.e("alipay error", e);
		}

		//
		if (jsonResponse != null)
			L.d(jsonResponse.toString());	
		
		return jsonResponse;
	}

	// 
	// 动态下载
	public boolean retrieveApkFromNet(Context context, String strurl, String filename)
	{
		boolean bRet = false;		

    	try
    	{
			NetworkManager nM = new NetworkManager(this.mContext);
			bRet =  nM.urlDownloadToFile(context, strurl, filename);
    	}
    	catch(Exception e)
    	{
    		L.e("alipay error", e);
    	}
    	
		return bRet;
	}
	
	//
	// close the progress bar
    void closeProgress()
    {
    	try
    	{
	    	if( mProgress != null )
	    	{
	    		mProgress.dismiss();
	    		mProgress = null;
	    	}
    	}
    	catch(Exception e)
    	{
    		L.e("alipay error", e);
    	}
    }
    
	//
	// the handler use to receive the install check result.
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{	
			try
			{
				switch (msg.what)
				{
					case AlixId.RQF_INSTALL_CHECK:
					{
						//
						closeProgress();
						String cachePath = (String)msg.obj;
						
						showInstallConfirmDialog(mContext, cachePath);
					}
					break; 
				}

				super.handleMessage(msg);
			}
			catch(Exception e)
			{
				L.e("alipay error", e);
			}
		}
	};
}