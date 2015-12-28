package cn.vstore.appserver.form.payment;

import java.util.Date;

import cn.vstore.appserver.form.TokenForm;

public class SelectOrderPayForm extends TokenForm {

	private String merchantsid;
	private String productid;
	private String channelid;
	private String version;
	private String merusername;
	private String meruseremail;
	private String privatefield;
	private String userID;
	private String usertp;
	private String paytype;
	private String orderno;
	private String payorderno;
	private String orderstatus;
	private String returnurl;
	private String denomination;
	private String cardno;
	private String cardpwd;
	private String cardtype;
	private String cardinfo;
	private String amount;
	private String itemid;
	private String packageName;
	private String reserveinfo;
	private String timestamp;
	private String verifytype;
	private String verifystr;
	private String signstring;
	private Date createdate;
	private Date paydate;
	private Long memid;
	private String privateKey;
	private String buytype;
	private String accounts;
	private String propsId;
	private String propsType;
	private String price;
	private String loginId;
	private String nums;
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getMerchantsid() {
		return merchantsid;
	}
	public void setMerchantsid(String merchantsid) {
		this.merchantsid = merchantsid;
	}
	public String getProductid() {
		return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
	}
	public String getChannelid() {
		return channelid;
	}
	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMerusername() {
		return merusername;
	}
	public void setMerusername(String merusername) {
		this.merusername = merusername;
	}
	public String getMeruseremail() {
		return meruseremail;
	}
	public void setMeruseremail(String meruseremail) {
		this.meruseremail = meruseremail;
	}
	public String getPrivatefield() {
		return privatefield;
	}
	public void setPrivatefield(String privatefield) {
		this.privatefield = privatefield;
	}
	public String getUsertp() {
		return usertp;
	}
	public void setUsertp(String usertp) {
		this.usertp = usertp;
	}
	public String getPaytype() {
		return paytype;
	}
	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public String getPayorderno() {
		return payorderno;
	}
	public void setPayorderno(String payorderno) {
		this.payorderno = payorderno;
	}
	public String getOrderstatus() {
		return orderstatus;
	}
	public void setOrderstatus(String orderstatus) {
		this.orderstatus = orderstatus;
	}
	public String getReturnurl() {
		return returnurl;
	}
	public void setReturnurl(String returnurl) {
		this.returnurl = returnurl;
	}
	public String getDenomination() {
		return denomination;
	}
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getCardpwd() {
		return cardpwd;
	}
	public void setCardpwd(String cardpwd) {
		this.cardpwd = cardpwd;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCardinfo() {
		return cardinfo;
	}
	public void setCardinfo(String cardinfo) {
		this.cardinfo = cardinfo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getReserveinfo() {
		return reserveinfo;
	}
	public void setReserveinfo(String reserveinfo) {
		this.reserveinfo = reserveinfo;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getVerifytype() {
		return verifytype;
	}
	public void setVerifytype(String verifytype) {
		this.verifytype = verifytype;
	}
	public String getVerifystr() {
		return verifystr;
	}
	public void setVerifystr(String verifystr) {
		this.verifystr = verifystr;
	}
	public String getSignstring() {
		return signstring;
	}
	public void setSignstring(String signstring) {
		this.signstring = signstring;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public Date getPaydate() {
		return paydate;
	}
	public void setPaydate(Date paydate) {
		this.paydate = paydate;
	}
	public Long getMemid() {
		return memid;
	}
	public void setMemid(Long memid) {
		this.memid = memid;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getBuytype() {
		return buytype;
	}
	public void setBuytype(String buytype) {
		this.buytype = buytype;
	}
	public String getAccounts() {
		return accounts;
	}
	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}
	public String getPropsType() {
		return propsType;
	}
	public void setPropsType(String propsType) {
		this.propsType = propsType;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPropsId() {
		return propsId;
	}
	public void setPropsId(String propsId) {
		this.propsId = propsId;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getNums() {
		return nums;
	}
	public void setNums(String nums) {
		this.nums = nums;
	}
}
