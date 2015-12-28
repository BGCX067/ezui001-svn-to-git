package cn.vstore.appserver.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class GamePayInformation implements Serializable {

    private static final long serialVersionUID = -6347735608250934457L;
    private long id;
    private String pkg;
    private int status;
    private String myPriceType;
    private Date payTime;
    private Date rightStartDate;
    private Date rightEndDate;
    private Integer versionId;
    private Integer lastVersionId;
    private String userUid;
    private String userId;
    private int paymentType;
    private Double amount;
    private String apiName;
    private String retCode;
    private String retMsg;
    private String token;
    private String paymentId;
    private String imei;
    private BigDecimal storeId;
    private String storeOid;
    private String orderNo;//订单号
    private Date cancelTime;
    private Integer monthlyCycle;
    private BigDecimal orderRefundHistoryId;
    private BigDecimal payeeStoreId;
	private String arel;//
	private String userID;//用户id
	private String propsId;//道具id
	private String propsType;//道具类型
	private String num;//道具数量
	private String price;//总价格
	private String snum;//渠道号
	
	private String productId;//飞流分配商品id(对应当乐mid<商户编号>)
	private String companyId;//飞流分配公司id(对应当乐gid<游戏编号>)
	private String channelId;//飞流分配渠道id(对应当乐sid<服务器编号>)
	private Double denomination;//游戏卡面额
	private String merPriv;//保留字段
	private String vervifystring;//加密串
	private String cardNo;
	private String cardPwd;
	private	String orderType;
	
	
	private String interOrderId;//service_id修改(飞流订单或当乐支付成功返回的订单号码)
	private String userIpAddr;//CANCEL_SRC_TYPE 修改(请求提交的IP)
	private String payUserId;//DOWNLOAD_ID修改(当乐指定支付用户的类型<固定值189用户填1，非189用户填0>)
	private String cardStatus;//RET_CODE_API_NAME修改(飞流返回支付结果中支付卡的卡状态)
	private String orderTime;//新增(飞流需要支持日期参数)
    
	public String getArel() {
		return arel;
	}

	public void setArel(String arel) {
		this.arel = arel;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMyPriceType() {
        return myPriceType;
    }

    public void setMyPriceType(String myPriceType) {
        this.myPriceType = myPriceType;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getLastVersionId() {
        return lastVersionId;
    }

    public void setLastVersionId(Integer lastVersionId) {
        this.lastVersionId = lastVersionId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRetMsgWithThrowable(Throwable throwable) {
        if (throwable != null) {
            String exceptionString = throwable.getMessage();
            if (exceptionString != null && exceptionString.length() > 150) {
                exceptionString = exceptionString.substring(0, 150);
            }
            this.retMsg = exceptionString;
        }
        if (StringUtils.isBlank(this.retMsg)) {
            this.retMsg = "";
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public BigDecimal getStoreId() {
		return storeId;
	}

	public void setStoreId(BigDecimal storeId) {
		this.storeId = storeId;
	}

	public String getStoreOid() {
		return storeOid;
	}

	public void setStoreOid(String storeOid) {
		this.storeOid = storeOid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getRightStartDate() {
		return rightStartDate;
	}

	public void setRightStartDate(Date rightStartDate) {
		this.rightStartDate = rightStartDate;
	}

	public Date getRightEndDate() {
		return rightEndDate;
	}

	public void setRightEndDate(Date rightEndDate) {
		this.rightEndDate = rightEndDate;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	public Integer getMonthlyCycle() {
		return monthlyCycle;
	}

	public void setMonthlyCycle(Integer monthlyCycle) {
		this.monthlyCycle = monthlyCycle;
	}

	public BigDecimal getOrderRefundHistoryId() {
		return orderRefundHistoryId;
	}

	public void setOrderRefundHistoryId(BigDecimal orderRefundHistoryId) {
		this.orderRefundHistoryId = orderRefundHistoryId;
	}

	public BigDecimal getPayeeStoreId() {
		return payeeStoreId;
	}

	public void setPayeeStoreId(BigDecimal payeeStoreId) {
		this.payeeStoreId = payeeStoreId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

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

	public String getVervifystring() {
		return vervifystring;
	}

	public void setVervifystring(String vervifystring) {
		this.vervifystring = vervifystring;
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

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
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
