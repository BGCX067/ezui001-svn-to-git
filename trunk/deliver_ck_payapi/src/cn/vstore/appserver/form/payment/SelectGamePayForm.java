package cn.vstore.appserver.form.payment;


import org.apache.commons.lang.builder.ToStringBuilder;

import cn.vstore.appserver.form.TokenForm;

public class SelectGamePayForm extends TokenForm {

	private String storeOid;
	private String payeeInfo;
	
	private String isTest;
	private String checkSignUrl;
	private String merchantId;
	private String merchantName;
	private String merchantPublicCer;
	private String privateKeyFileName;
	private String privateKeyAlias;
	private String privateKeyPassword;
	private String transTimeout;
	private String backEndUrl;

	//游戏支付所增加字段
	private String packageName;//apk名称
	private String arel;//
	private String userID;//用户id
	private String propsId;//道具id
	private String propsType;//道具类型码
	private String num;//数量
	private String price;//总价
	private String snum;//渠道号
	private String amount;//支付金额
	
	//飞流支付增加字段
	private Double denomination;//游戏卡面额
	private String merPriv;//保留字段
	private String cardNo;//卡号
	private String cardPwd;//卡密
	private String orderType;//卡支付类型
	private String companyId; //= "100016";//公司ID
	private String productId; //= "100016";//产品ID
	private String channelId; //= "100043";//渠道ID
	private String orderNo;
	private String verStr;
	private String interOrderId;//service_id修改(飞流订单或当乐支付成功返回的订单号码)
	private String userIpAddr;//CANCEL_SRC_TYPE 修改(请求提交的IP)
	private String payUserId;//DOWNLOAD_ID修改(当乐指定支付用户的类型<固定值189用户填1，非189用户填0>)
	private String cardStatus;//RET_CODE_API_NAME修改(飞流返回支付结果中支付卡的卡状态)
	private String orderTime;//新增(飞流需要支持日期参数)
	
	
	public Double getDenomination() {
		return denomination;
	}

	public void setDenomination(Double denomination) {
		this.denomination = denomination;
	}

	public String getMerPriv() {
		return merPriv;
	}

	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
	}

	public String getArel() {
		return arel;
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

	public String getPropsType() {
		return propsType;
	}

	public void setPropsType(String propsType) {
		this.propsType = propsType;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSnum() {
		return snum;
	}

	public void setSnum(String snum) {
		this.snum = snum;
	}

	public void setArel(String arel) {
		this.arel = arel;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public String getStoreOid() {
		return storeOid;
	}

	public void setStoreOid(String storeOid) {
		this.storeOid = storeOid;
	}

	public String getPayeeInfo() {
		return payeeInfo;
	}

	public void setPayeeInfo(String payeeInfo) {
		this.payeeInfo = payeeInfo;
	}

	public String getCheckSignUrl() {
		return checkSignUrl;
	}

	public void setCheckSignUrl(String checkSignUrl) {
		this.checkSignUrl = checkSignUrl;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantPublicCer() {
		return merchantPublicCer;
	}

	public void setMerchantPublicCer(String merchantPublicCer) {
		this.merchantPublicCer = merchantPublicCer;
	}

	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	public String getPrivateKeyAlias() {
		return privateKeyAlias;
	}

	public void setPrivateKeyAlias(String privateKeyAlias) {
		this.privateKeyAlias = privateKeyAlias;
	}

	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}

	public String getTransTimeout() {
		return transTimeout;
	}

	public void setTransTimeout(String transTimeout) {
		this.transTimeout = transTimeout;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String getBackEndUrl() {
		return backEndUrl;
	}

	public void setBackEndUrl(String backEndUrl) {
		this.backEndUrl = backEndUrl;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardPwd() {
		return cardPwd;
	}

	public void setCardPwd(String cardPwd) {
		this.cardPwd = cardPwd;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	  
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getVerStr() {
		return verStr;
	}

	public void setVerStr(String verStr) {
		this.verStr = verStr;
	}

	public String getInterOrderId() {
		return interOrderId;
	}

	public void setInterOrderId(String interOrderId) {
		this.interOrderId = interOrderId;
	}

	public String getUserIpAddr() {
		return userIpAddr;
	}

	public void setUserIpAddr(String userIpAddr) {
		this.userIpAddr = userIpAddr;
	}

	public String getPayUserId() {
		return payUserId;
	}

	public void setPayUserId(String payUserId) {
		this.payUserId = payUserId;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
}
