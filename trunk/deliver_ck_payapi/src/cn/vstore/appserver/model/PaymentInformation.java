package cn.vstore.appserver.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class PaymentInformation implements Serializable {

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
    private String orderNo;
    private Date cancelTime;
    private Integer monthlyCycle;
    private BigDecimal orderRefundHistoryId;
    private BigDecimal payeeStoreId;
    private String transactionTypeId;
    private String transactionData;

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

	public String getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(String transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public String getTransactionData() {
		return transactionData;
	}

	public void setTransactionData(String transactionData) {
		this.transactionData = transactionData;
	}

}
