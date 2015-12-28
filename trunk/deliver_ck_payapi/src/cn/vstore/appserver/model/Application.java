package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Application implements Serializable {

    /**
     * PRICE_TYPE
     */
    public static final String PRICE_TYPE_FREE = "0"; //免費
    public static final String PRICE_TYPE_ONCE = "1"; //計次
    public static final String PRICE_TYPE_MONTH = "2"; //月租
    public static final String PRICE_INNAPP_PURCHASE = "3"; //InAppPurchase
    public static final String PRICE_INNAPP_CAR = "4"; //INNAPP購物車

    private static final long serialVersionUID = 16329572551403345L;
    private String pkg;
    private int version;
    private String title;
    private String icon;
    private String provider;
    private int totalRank;
    private int rankTimes;
    private Double price;
    private String priceType;
    private int payStatus;
    private int monthlyCycle;
    private long fileSize;
    private int totalDownloadTimes;
    private String versionName;
    private String filePath;
    
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getTotalDownloadTimes() {
		return totalDownloadTimes;
	}

	public void setTotalDownloadTimes(int totalDownloadTimes) {
		this.totalDownloadTimes = totalDownloadTimes;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	private SubscribeExpDate subscribeExpDate;

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(int totalRank) {
        this.totalRank = totalRank;
    }

    public int getRankTimes() {
        return rankTimes;
    }

    public void setRankTimes(int rankTimes) {
        this.rankTimes = rankTimes;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public int getPayStatus() {
        // 如果是測試用假付款成功status=6,回傳到手機的status改為2(付款成功)
        if (payStatus == UserPayment.STATUS_PAYMENT_TEST_SUCCESS) {
            payStatus = UserPayment.STATUS_PAYMENT_SUCCESS;
            // 如果是測試用假訂單取消status=7,回傳到手機的status改為5(訂單取消)
        } else if (payStatus == UserPayment.STATUS_PAYMENT_TEST_CANCEL) {
            payStatus = UserPayment.STATUS_PAYMENT_UN_SUBSCRIBE;
        }
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public SubscribeExpDate getSubscribeExpDate() {
        return subscribeExpDate;
    }

    public void setSubscribeExpDate(SubscribeExpDate subscribeExpDate) {
        this.subscribeExpDate = subscribeExpDate;
    }

    public float getRating() {
        if (totalRank <= 0 || rankTimes <= 0)
            return 0;
        float rating = (float) totalRank / (float) rankTimes;
        
        if (rating <= (int) rating + 0.25) {
        	 return (int) rating; 
        }else if(rating > (int) rating + 0.25 && rating <= (int) rating + 0.75){
        	 return ((int) rating + 0.5f);
        }else{
        	return ((int) rating + 1);
        }
              
//        if (rating >= (int) rating + 0.5) {
//            return ((int) rating + 0.5f);
//        } else {
//            return (int) rating;
//        }
    }

    // 存入PayStatus，來源為PaymentInformation
    public void setPaymentInformationList(List<PaymentInformation> list) {
        for (PaymentInformation paymentInformation : list) {
            if (paymentInformation.getPkg() != null && paymentInformation.getPkg().equals(this.pkg)) {
                this.setPayStatus(paymentInformation.getStatus());

                // 月租且取消訂閱後，尚未到期者，秀SubscribeExpDate日期
                if (PRICE_TYPE_MONTH.equals(paymentInformation.getMyPriceType())
                        && UserPayment.STATUS_PAYMENT_UN_SUBSCRIBE == this.getPayStatus()) {
                    Date date = paymentInformation.getRightEndDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    this.setSubscribeExpDate(new SubscribeExpDate(String.valueOf(calendar.get(Calendar.MONTH) + 1), String.valueOf((calendar.get(Calendar.DATE)))));
                }
            }
        }
    }

    public int getMonthlyCycle() {
		return monthlyCycle;
	}

	public void setMonthlyCycle(int monthlyCycle) {
		this.monthlyCycle = monthlyCycle;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
}