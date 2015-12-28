package tw.com.sti.clientsdk;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import tw.com.sti.clientsdk.unionpay.XmlDefinition;
import tw.com.sti.clientsdk.unionpay.SignBy;

import tw.com.sti.clientsdk.gpay.AlixId;
import tw.com.sti.clientsdk.gpay.BaseHelper;
import tw.com.sti.clientsdk.gpay.MobileSecurePayHelper;
import tw.com.sti.clientsdk.gpay.MobileSecurePayer;
import tw.com.sti.clientsdk.gpay.ResultChecker;
import tw.com.sti.clientsdk.gpay.Rsa;
import tw.com.sti.store.api.ApiInvoker;
import tw.com.sti.store.api.ApiUrl;
import tw.com.sti.store.api.Configuration;
import tw.com.sti.store.api.android.AndroidApiService;
import tw.com.sti.store.api.android.util.Logger;
import tw.com.sti.store.api.vo.OrderRet;
import tw.com.sti.store.api.vo.UPayConfig;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class ShoppingCartActivity extends Activity {
	private static final Logger L=Logger.getLogger(ShoppingCartActivity.class);
	
	final static String CMD_PAY_PLUGIN = "cmd_pay_plugin";
	final static String CMD_USERS_PLUGIN = "cmd_user_plugin";
	
	public final static String RET_SUCCESS="0000";
	public final static String RET_CREATE_ORDER_FAIL="0001";
	public final static String RET_PROCESS_FAIL="0002";
	public final static String RET_PAY_FAIL="0003";
	
	public final static String TYPE_RSA = "RSA";
	public final static String TYPE_DSA = "DSA";
	public final static String TYPE_MD5 = "MD5";
	
	public final static int PAY_BY_GPAY = 1;
	public final static int PAY_BY_UNIONPAY = 2;
	public final static int PAY_BY_PEPAY_TY_BILL = 3;//使用PePay電信帳單付款
	
	private ProgressDialog mProgress = null;
	private String partner;
	private String seller;
	private String signType;
	private String key;
	private String storeOid;
	private int paymentType = 0;
	private final static ResourceBundle resources = ResourceFactory.getResourceBundle();
	
	private UPayConfig uPayConfig;
	
	protected void payByGPAY(final String pkg,final String partner,final String seller,final String signType, final String key, final String storeOid) {

		if( pkg == null || pkg.length() <= 0 )
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_lack_apk"), resources.getString("dialog_ascertain"));
			return;
		}
		if( partner == null || partner.length() <= 0 ||
				seller == null || seller.length() <= 0 )
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_lack_partner_or_seller"), resources.getString("dialog_ascertain"));
			return;
		}
		if( signType == null || (!TYPE_RSA.equalsIgnoreCase(signType)&&!TYPE_DSA.equalsIgnoreCase(signType)&&!TYPE_MD5.equalsIgnoreCase(signType)))
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_signtype_must_be_rsa"), resources.getString("dialog_ascertain"));
			return;
		}
		if( key == null || key.length() <= 0 )
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_lack_key"), resources.getString("dialog_ascertain"));
			return;
		}
		if( storeOid != null && storeOid.length() >40 )
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_order_num_overflow"), resources.getString("dialog_ascertain"));
			return;
		}
		this.partner=partner;
		this.seller=seller;
		this.signType=signType;
		this.key=key;
		this.storeOid=storeOid;
		this.paymentType = PAY_BY_GPAY;
		final String payeeInfo="<gpay><partner>"+partner+"</partner><seller>"+seller+"</seller></gpay>";
		// check to see if the MobileSecurePay is already installed.
		MobileSecurePayHelper mspHelper =  new MobileSecurePayHelper(this);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if( !isMobile_spExist )
			return;
		mProgress = BaseHelper.showProgress(this, null, resources.getString("msg_paying"), false, true);

		new AsyncTask<Void, Void, ApiInvoker<OrderRet>>() {
			private ApiInvoker<OrderRet> apiInvoker;

			protected void onCancelled() {
				if (apiInvoker != null)
					apiInvoker.stop();
				apiInvoker = null;
			};

			@Override
			protected ApiInvoker<OrderRet> doInBackground(Void... params) {
				AndroidApiService apiService = AndroidApiService.getInstance(getApplicationContext(), getConfiguration());
				apiInvoker = apiService.getNewOrder(pkg,storeOid,payeeInfo);
				apiInvoker.invoke();
				return apiInvoker;
			}

			protected void onPostExecute(ApiInvoker<OrderRet> apiInvoker) {
				if (apiInvoker != null)
					responseOrder(apiInvoker);
			};
		}.execute();
	}

	protected void payByUnionPay(final String pkg, String isTest, String checkSignUrl, String merchantId, 
			String merchantName, String merchantPublicCer, String privateKeyFileName, 
			String privateKeyAlias, String privateKeyPassword, String transTimeout,
			final String storeOid) {
		if( pkg == null || pkg.length() <= 0 )
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_lack_apk"), resources.getString("dialog_ascertain"));
			return;
		}
		if( checkSignUrl == null || checkSignUrl.length() <= 0 ||
			merchantId == null || merchantId.length() <= 0 ||
			merchantName == null || merchantName.length() <= 0 ||
			merchantPublicCer == null || merchantPublicCer.length() <= 0 ||
			privateKeyFileName == null || privateKeyFileName.length() <= 0 ||
			privateKeyAlias == null || privateKeyAlias.length() <= 0 ||
			privateKeyPassword == null || privateKeyPassword.length() <= 0 || transTimeout == null)
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_lack_UnionPay_config"), resources.getString("dialog_ascertain"));
			return;
		}
		if( storeOid != null && storeOid.length() >40 )
		{
			BaseHelper.showDialog(this, resources.getString("dialog_tips"), resources.getString("dialog_order_num_overflow"), resources.getString("dialog_ascertain"));
			return;
		}
		this.partner = merchantId;
		this.storeOid = storeOid;
		this.paymentType = PAY_BY_UNIONPAY;
		final String payeeInfo="<unionpay><merchantId>" + merchantId + "</merchantId></unionpay>";
		uPayConfig = new UPayConfig(isTest, checkSignUrl, merchantId, merchantName, merchantPublicCer, 
				privateKeyFileName, privateKeyAlias, privateKeyPassword, transTimeout, new ApiUrl(getConfiguration()).getUnionPaymentReceiverUrl());

		mProgress = BaseHelper.showProgress(this, null, resources.getString("msg_paying"), false, true);

		new AsyncTask<Void, Void, ApiInvoker<OrderRet>>() {
			private ApiInvoker<OrderRet> apiInvoker;

			protected void onCancelled() {
				if (apiInvoker != null)
					apiInvoker.stop();
				apiInvoker = null;
			};

			@Override
			protected ApiInvoker<OrderRet> doInBackground(Void... params) {
				AndroidApiService apiService = AndroidApiService.getInstance(getApplicationContext(), getConfiguration());
				apiInvoker = apiService.getNewOrder(pkg,storeOid,payeeInfo,uPayConfig);
				apiInvoker.invoke();
				return apiInvoker;
			}

			protected void onPostExecute(ApiInvoker<OrderRet> apiInvoker) {
				if (apiInvoker != null)
					responseOrder(apiInvoker);
			};
		}.execute();
	}

	// 因為 onResume 有重新讀取，會有多緒的問題，所以要把apiInvoker傳過來
	private void responseOrder(ApiInvoker<OrderRet> apiInvoker) {
		if(apiInvoker.isSuccess()){
			OrderRet order = apiInvoker.getRet();
			if(order !=null && order.isReLogin()) {
				showLogin();
				return;
			} else if (order != null && order.isSuccess()) {
				switch(paymentType){
				case PAY_BY_GPAY:
					g_payment(order);
					break;
				case PAY_BY_UNIONPAY:
					u_payment(order);
					break;
				}
				return;
			}
		}
		doResult(RET_CREATE_ORDER_FAIL, resources.getString("msg_order_error"));
	}
	
	private void g_payment(OrderRet order){
		
		try
		{	  
			String orderInfo 	= getOrderInfo(order);
			String strsign 		= sign(orderInfo);
			strsign				= URLEncoder.encode(strsign);
			String info 		= 	orderInfo
									+ "&sign=" +  "\"" + strsign  + "\""
									+ "&sign_type=" + "\"" + signType + "\"";
			// start the pay.
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, this);
			
			if( bRet )
			{
//				// show the progress bar to indicate that we have started paying.
//				closeProgress();
//				mProgress = BaseHelper.showProgress(
//						this, null, "正在支付", 
//							false, true);
			}
			else{
				doResult(RET_PROCESS_FAIL, resources.getString("msg_pay_fail"));
			}
		}
		catch (Exception ex)
		{
			doResult(RET_PROCESS_FAIL, resources.getString("msg_pay_fail"));
		}
		
	}
	
	private void u_payment(OrderRet order){
		closeProgress();
		try
		{	  
			String merchantOrderId = (String) ((order.getOrder().getOrderNo()!=null&&order.getOrder().getOrderNo().length()>0)?order.getOrder().getOrderNo():order.getOrder().getOrderId());
			String merchantOrderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(order.getOrder().getOrderTime());//订单时间(YYYYMMDDHHMMSS)
			String originalsign3  ="merchantId=" + uPayConfig.getMerchantId();
				originalsign3	+= "&merchantOrderId=" + merchantOrderId;
				originalsign3	+= "&merchantOrderTime=" + merchantOrderTime;
			Log.d("u_payment-originalsign3:", originalsign3);

			// 创建签名类对象
			SignBy getSign = new SignBy();
			String xmlSign3 = "";
			try {
				InputStream PrivateSign = getFromAssets(uPayConfig.getPrivateKeyFileName());
				// 创建3位签名
				xmlSign3 = getSign.createSign(originalsign3, uPayConfig.getPrivateKeyAlias(), uPayConfig.getPrivateKeyPassword(),
						PrivateSign);
				Log.d("u_payment-xmlSign3:",xmlSign3);
			} catch (FileNotFoundException e) {
				Log.d("u_payment", "私钥获取错误，无私钥信息");
				e.printStackTrace();
			}
			
			// 生成3要素报文
			String launchPay = XmlDefinition.LanchPay(uPayConfig.getMerchantId(), merchantOrderId,
					merchantOrderTime, xmlSign3, uPayConfig.getBackEndUrl());
			Log.d("u_payment-launchPay", launchPay);
			
			// 向插件提交3要素报文
			
			byte[] to_upomp = launchPay.getBytes();
			ComponentName com = new ComponentName(this.getComponentName().getPackageName(),
					"com.unionpay.upomp.lthj.plugin.ui.MainActivity");
			
			// 设置Intent指向
			Intent intent = new Intent();
			intent.setComponent(com); // 启动插件(进入支付页面)
			intent.putExtra("action_cmd", CMD_PAY_PLUGIN);
			
			// 启动插件(进入用户管理页面)
			// intent. putExtra("action_cmd", CMD_USERS_PLUGIN);
			// 将封装好的xml报文传入bundle
			Bundle mbundle = new Bundle();
			// to_upomp为商户提交的XML
			mbundle.putByteArray("xml", to_upomp);
			// 注：此处的action是：商户的action 
			mbundle.putString("merchantPackageName","cn.com.vapk.vstore.client.action.PAYRESULT");//该处两个参数错误，会出现商户客户端传入包名为空的错误
			// 将bundle置于intent中
			intent.putExtras(mbundle);
			// 使用intent跳转至手机POS
			startActivity(intent);
		}
		catch (Exception ex)
		{
			Log.d("u_payment-launchPay", ex.getMessage());
			doResult(RET_PROCESS_FAIL, resources.getString("msg_pay_fail"));
		}
		
	}
	
	private String getOrderInfo(OrderRet order)
	{
		
		String strOrderInfo  ="partner=" + "\"" + partner + "\"";
		strOrderInfo 		+= "&";
		strOrderInfo 		+= "seller=" + "\"" + seller + "\"";
		strOrderInfo 		+= "&";
		strOrderInfo		+= "out_trade_no=" + "\"" + ((order.getOrder().getOrderNo()!=null&&order.getOrder().getOrderNo().length()>0)?order.getOrder().getOrderNo():order.getOrder().getOrderId()) + "\"";
		strOrderInfo 		+= "&";
		strOrderInfo		+= "subject=" + "\"" + order.getOrder().getTitle() + "\"";
		strOrderInfo 		+= "&";
		strOrderInfo		+= "body=" + "\"" +order.getOrder().getProvider() + "\"";
		strOrderInfo 		+= "&";
		strOrderInfo		+= "total_fee=" + "\"" + order.getOrder().getPrice() + "\"";
		strOrderInfo 		+= "&";
		strOrderInfo		+= "notify_url="  + "\"" + new ApiUrl(getConfiguration()).getGPaymentReceiverUrl() + "\"";
		L.d(strOrderInfo);
	
		return strOrderInfo;
	}
	
	private String sign(String content)
	{
		String sign = null;
		
		if( signType.indexOf(TYPE_RSA) >= 0)
			sign = Rsa.sign(content, key);
		
		return sign;
	}
	
	private void closeProgress()
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
    	}
    }
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{	
			try
			{
				String strRet = (String)msg.obj;
				L.d("gpay ret:"+strRet);
				switch (msg.what)
				{
					case AlixId.RQF_PAY:
					{
						//
						closeProgress();

						String memo = "";
						try
						{
							String[] memos = strRet.indexOf(";") > 0 ? strRet.split(";") : null;
							if(memos != null) {
								memo = memos[1].substring(memos[1].indexOf("{") + 1, memos[1].indexOf("}"));
							}
							
							// handle result
							ResultChecker resultChecker = new ResultChecker(strRet);
							boolean isPayOk = resultChecker.isPayOk();
							if(isPayOk){
								doResult(RET_SUCCESS, memo);
							}else{
								doResult(RET_PAY_FAIL, memo);
							}
						}
						catch(Exception e)
						{
							doResult(RET_PROCESS_FAIL, memo);
						}
					}
					break; 
				}

				super.handleMessage(msg);
			}
			catch(Exception e)
			{
			}
		}
	};
	
	// 银联：处理支付结果
		@Override
	    protected void onNewIntent(Intent intent) {

			super.onNewIntent(intent);
			Bundle bundle=intent.getExtras();
		    Log.i("bundle",(bundle==null)+"");
		    
		    //处理支付返回数据
		    if (bundle != null&&bundle.containsKey("xml")) {
			    byte[] xml = bundle.getByteArray("xml");
			    String Sxml;
				try {
					Sxml = new String(xml,"utf-8");
					String memo = Sxml.substring(Sxml.indexOf("<respDesc>")+10, Sxml.indexOf("</respDesc>"));
					Log.i("pay-result",Sxml);
					if(Sxml.contains("<respCode>0000</respCode>")){
						doResult(RET_SUCCESS, memo);
					}else{
						doResult(RET_PAY_FAIL, memo);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		    }
	    }
	
	private void doResult(String retCode, String memo){
		closeProgress();
		payResult(this, retCode, memo);
	}
	
	protected abstract Configuration getConfiguration();
	
	protected abstract void payResult(Context ctx, String retCode, String memo);
	
	protected abstract void showLogin();
	
	// 银联：从assets 文件夹中读取私钥档案
	protected InputStream getFromAssets(String fileName) throws FileNotFoundException{
		InputStream PrivateSign = null;
		try {
			PrivateSign = getResources().getAssets().open(fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return PrivateSign;
	}

}
