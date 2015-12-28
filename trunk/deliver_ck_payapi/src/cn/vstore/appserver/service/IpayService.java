package cn.vstore.appserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.service.ResultCode.IpayCode;

import com.fetnet.appstore.sdk.business.service.core.domain.ipay.GetOrderDeductionStatusResponse;
import com.fetnet.appstore.sdk.business.service.core.facade.invoker.FetIpaySdkFacadeInvoker;

@Service("IpayService")
public class IpayService {

    protected static final Logger logger = LoggerFactory.getLogger(IpayService.class);

    /**
     * 檢查計次付款資訊
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    ServiceResult<Boolean> checkPaymentInfoByOneTime(IpayService self, String userId,
        String serviceId, long orderId) {

        ResultCode resultCode = null;
        String code = null;
        String exceptionString = null;
        boolean isSuccess = false;

        try {
            FetIpaySdkFacadeInvoker invoker = new FetIpaySdkFacadeInvoker();
            GetOrderDeductionStatusResponse gdsr = invoker.GetOrderDeductionStatus(String.valueOf(orderId), serviceId, userId);
            String statusG = gdsr.getStatus();
            code = gdsr.getReturnCode();
            exceptionString = gdsr.getDescription();
            if (code != null && (code.trim().equals(BusinessCommonCode.IPAY_CODE_SUCCESS))
                    && statusG != null
                    && statusG.trim().equals(BusinessCommonCode.IPAY_PAY_SUCCSESS)) {
                // 付款成功
                isSuccess = true;
            } else {
                // 付款失敗
                resultCode = IpayCode.IPAY_PAYMENT_FAIL;
            }
        } catch (Throwable e) {
            return new ServiceResult<Boolean>(IpayCode.IPAY_NO_FEEDBACK, new Boolean(isSuccess), code, exceptionString, e);
        }
        return new ServiceResult<Boolean>(resultCode, new Boolean(isSuccess), code, exceptionString);
    }
}