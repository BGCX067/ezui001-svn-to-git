package tw.com.sti.store.api.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

final public class OrderInfo {
	
	private String pkg;
	private int version;
	private String title;
	private String icon;
	private String provider;
	private float price;
	private byte priceType;
	private int payStatus;
	private long orderId;
	private String priceText;
	private String orderNo;
	private Date orderTime;
	private Date rightStartDate;
	private Date rightEndDate;
	private int payType;

	final static OrderInfo createInstance(JSONObject json) {
		OrderInfo ret = new OrderInfo();
		

		ret.pkg = json.optString("pkg");
		ret.version = json.optInt("version");
		ret.title = json.optString("title");
		ret.icon = json.optString("icon");
		ret.provider = json.optString("provider");
		ret.price = Float.parseFloat(json.optString("price").trim());
		ret.priceType = Byte.parseByte(json.optString("priceType").trim());
		ret.payStatus = json.optInt("payStatus");
		ret.priceText = json.optString("priceText");
		ret.orderId = json.optLong("orderId");
		ret.orderNo = json.optString("orderNo");
		try{
			ret.orderTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.optString("orderTime"));
		}catch(Throwable e){}
		try{
			ret.rightStartDate=new SimpleDateFormat("yyyy-MM-dd").parse(json.optString("rightStartDate"));
		}catch(Throwable e){}
		try{
			ret.rightEndDate=new SimpleDateFormat("yyyy-MM-dd").parse(json.optString("rightEndDate"));
		}catch(Throwable e){}
		ret.payType=json.optInt("payType", 0);
		return ret;
	}

	private OrderInfo() {
	}

	public String getPkg() {
		return pkg;
	}

	public int getVersion() {
		return version;
	}

	public String getTitle() {
		return title;
	}

	public String getIcon() {
		return icon;
	}

	public String getProvider() {
		return provider;
	}

	public float getPrice() {
		return price;
	}

	public byte getPriceType() {
		return priceType;
	}

	public int getPayStatus() {
		return payStatus;
	}

	public long getOrderId() {
		return orderId;
	}

	public String getPriceText() {
		return priceText;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public Date getRightStartDate() {
		return rightStartDate;
	}

	public Date getRightEndDate() {
		return rightEndDate;
	}

	public int getPayType() {
		return payType;
	}
}
