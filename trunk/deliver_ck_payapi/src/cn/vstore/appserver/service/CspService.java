package cn.vstore.appserver.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.smart.appstore.server.api.Constants;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.CspCode;

import com.fetnet.appstore.sdk.business.service.core.domain.csp.AuthenicateAPUserResponse;
import com.fetnet.appstore.sdk.business.service.core.domain.csp.DesubscribeAPServiceResponse;
import com.fetnet.appstore.sdk.business.service.core.domain.csp.GetUserAPSubStatusResponse;
import com.fetnet.appstore.sdk.business.service.core.domain.csp.GetUserMSISDNByIPResponse;
import com.fetnet.appstore.sdk.business.service.core.domain.csp.GetUserUIDResponse;
import com.fetnet.appstore.sdk.business.service.core.domain.csp.SubscribeServiceResponse;
import com.fetnet.appstore.sdk.business.service.core.facade.invoker.FetCspSdkFacadeInvoker;

@Service("CspService")
public class CspService {

    private static final Logger logger = LoggerFactory.getLogger(CspService.class);

    /**
     * 登入時，CSP驗證帳號密碼
     */
    ServiceResult<AuthenicateAPUserResponse> credential(String userId, String pwd) {

        AuthenicateAPUserResponse authenicateAPUser = null;
        try {
            FetCspSdkFacadeInvoker cspInvoker = new FetCspSdkFacadeInvoker();
            authenicateAPUser = cspInvoker.AuthenicateAPUser(userId, pwd, "N", userId);
            if (authenicateAPUser == null) {
                return new ServiceResult<AuthenicateAPUserResponse>(CspCode.CSP_NO_FEEDBACK, null);
            }

            String code = authenicateAPUser.getReturnCode();
            String des = authenicateAPUser.getDescription();
            logger.info(new StringBuilder().append("AuthenicateAPUser userId: ").append(userId)
            //
            .append(", ReturnCode: ").append(code).append(", Description: ").append(des).toString());

            if (code == null) {
                return new ServiceResult<AuthenicateAPUserResponse>(CspCode.CSP_NO_FEEDBACK, authenicateAPUser);
            }

            ResultCode resultCode = null;
            code = code.trim();
            if (BusinessCommonCode.CSP_FAIL_CODE_LOGIN_INVALID_PASSWORD.equals(code)) {
                resultCode = CspCode.INVALID_PASSWORD;
            } else if (BusinessCommonCode.CSP_FAIL_CODE_LOGIN_INVALID_USER.equals(code)) {
                resultCode = CspCode.INVALID_USER;
            } else if (BusinessCommonCode.CSP_FAIL_CODE_LOGIN_INVALID_USERID.equals(code)) {
                resultCode = CspCode.INVALID_USERID;
            } else if ((!BusinessCommonCode.CSP_CODE_SUCCESS.equals(code))
                    && (!BusinessCommonCode.CSP_CODE_FAIL_CREATE_IPAY.equals(code))
                    && (!BusinessCommonCode.CSP_CODE_FAIL_NO_EMAIL.equals(code))) {
                resultCode = CspCode.INVALID_USERID;
            } else {
                resultCode = CspCode.CSP_SUCCESS;
            }
            return new ServiceResult<AuthenicateAPUserResponse>(resultCode, authenicateAPUser);
        } catch (Throwable e) {
            return new ServiceResult<AuthenicateAPUserResponse>(CspCode.CSP_NO_FEEDBACK, authenicateAPUser, e);
        }
    }

    /**
     * 登入時，從CSP取得userUid
     */
    ServiceResult<GetUserUIDResponse> getUserUid(String userId) {
        GetUserUIDResponse getUserUID = null;
        try {
            FetCspSdkFacadeInvoker cspInvoker = new FetCspSdkFacadeInvoker();
            getUserUID = cspInvoker.GetUserUID(userId, "0", userId);

            if (getUserUID == null)
                return new ServiceResult<GetUserUIDResponse>(CspCode.CSP_NO_FEEDBACK, getUserUID);

            String userUid = getUserUID.getUID();
            String retcode = getUserUID.getReturnCode();
            String scdrDes = getUserUID.getDescription();
            logger.info(new StringBuilder().append("GetUserUID userId: ").append(userId) //
            .append(", ReturnCode: ").append(retcode) //
            .append(", Description: ").append(scdrDes) //
            .append(", UID: ").append(userUid).toString());

            if (!BusinessCommonCode.CSP_CODE_SUCCESS.equals(retcode)
                    || StringUtils.isBlank(userUid)) {
                return new ServiceResult<GetUserUIDResponse>(CspCode.CSP_NO_FEEDBACK, getUserUID);
            }

            return new ServiceResult<GetUserUIDResponse>(CspCode.CSP_SUCCESS, getUserUID);
        } catch (Throwable e) {
            return new ServiceResult<GetUserUIDResponse>(CspCode.CSP_NO_FEEDBACK, getUserUID, e);
        }
    }

    /**
     * 登入時和自動登入時，尚未註冊者，向Csp註冊Zero service
     */
    ServiceResult<Boolean> registerZeroService(String userUid, String userId) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            FetCspSdkFacadeInvoker cnvoker = new FetCspSdkFacadeInvoker();
            SubscribeServiceResponse scdr = cnvoker.SubscribeService(userUid, BusinessCommonCode.ZERO_SERVICE_ID, null, "0", "WAP", BusinessCommonCode.ZERO_SERVICE_ID, null, dateFormat.format(new Date()), BusinessCommonCode.SUBSCRIBE_NOT_SEND_SMS, userId);
            String scdrCode = scdr.getReturnCode();
            String scdrDes = scdr.getDescription();
            logger.info(new StringBuilder().append("SubscribeService(Zero service) userId: ").append(userId).append(", ReturnCode: ").append(scdrCode).append(", Description: ").append(scdrDes).toString());

            // CSP_CODE_SUCCESS="00000000"; CSP code 成功
            // CSP_SERVICE_SUBSCRIBED="04000003"; service 已被註冊，但視為成功
            if (BusinessCommonCode.CSP_CODE_SUCCESS.equals(scdrCode)
                    || BusinessCommonCode.CSP_SERVICE_SUBSCRIBED.equals(scdrCode)) {
                return new ServiceResult<Boolean>(CommonCode.SUCCESS, Boolean.TRUE, scdrCode, scdrDes);
            }

            return new ServiceResult<Boolean>(CspCode.CSP_NO_FEEDBACK, Boolean.FALSE, scdrCode, scdrDes);

        } catch (Throwable e) {
            return new ServiceResult<Boolean>(CspCode.CSP_NO_FEEDBACK, Boolean.FALSE, null, null, e);
        }
    }

    /**
     * 使用IP登入
     */
    ServiceResult<GetUserMSISDNByIPResponse> credentialIp(String ipAddress) {

        GetUserMSISDNByIPResponse getUserMSISDNByIP = null;
        try {
            FetCspSdkFacadeInvoker invoker = new FetCspSdkFacadeInvoker();
            getUserMSISDNByIP = invoker.GetUserMSISDNByIP(ipAddress, "");
            if (getUserMSISDNByIP == null) {
                return new ServiceResult<GetUserMSISDNByIPResponse>(CspCode.CSP_NO_FEEDBACK);
            }

            String userId = getUserMSISDNByIP.getMsisdn();
            String scdrCode = getUserMSISDNByIP.getReturnCode();
            String scdrDes = getUserMSISDNByIP.getDescription();
            logger.info(new StringBuilder().append("GetUserIDByIP userId: ").append(userId)//
            .append(", ReturnCode: ").append(scdrCode)//
            .append(", Description: ").append(scdrDes).toString());

            scdrCode = scdrCode.trim();
            if (BusinessCommonCode.CSP_CODE_SUCCESS.equals(scdrCode)
                    && StringUtils.isNotBlank(userId)) {
                return new ServiceResult<GetUserMSISDNByIPResponse>(CspCode.CSP_SUCCESS, getUserMSISDNByIP);
            }

            return new ServiceResult<GetUserMSISDNByIPResponse>(CspCode.CSP_NO_FEEDBACK, getUserMSISDNByIP);
        } catch (Throwable e) {
            return new ServiceResult<GetUserMSISDNByIPResponse>(CspCode.CSP_NO_FEEDBACK, getUserMSISDNByIP, e);
        }
    }

    /**
     * 檢查月租付款資訊
     */
    ServiceResult<Boolean> checkPaimentInfoBySubscribe(String userId, String serviceId) {

        ResultCode resultCode = null;
        String code = null;
        String exceptionString = null;
        boolean isSuccess = false;

        try {
            FetCspSdkFacadeInvoker invoker = new FetCspSdkFacadeInvoker();
            GetUserAPSubStatusResponse gusr = invoker.GetUserAPSubStatus(userId, serviceId, userId);
            code = gusr.getReturnCode();
            String statusG = gusr.getSubStatus();
            exceptionString = gusr.getDescription();
            if (code != null && (code.trim().equals(BusinessCommonCode.CSP_CODE_SUCCESS))
                    && statusG != null
                    && statusG.trim().equals(BusinessCommonCode.CSP_STATUS_SUBSCRIBED)) {
                isSuccess = true;
            } else {
                resultCode = CspCode.CSP_PAYMENT_FAIL;
            }
        } catch (Throwable e) {
            return new ServiceResult<Boolean>(CspCode.CSP_NO_FEEDBACK, new Boolean(isSuccess), code, exceptionString, e);
        }
        return new ServiceResult<Boolean>(resultCode, new Boolean(isSuccess), code, exceptionString);
    }

    /**
     * 取消訂閱
     */
    ServiceResult<Boolean> unsubscribe(String userId, String serviceId, String userUid) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        ResultCode resultCode = null;
        boolean isUpdateData = false;
        String code = null;
        String exceptionString = null;
        try {
            // CSP取消訂閲
            FetCspSdkFacadeInvoker invoker = new FetCspSdkFacadeInvoker();
            DesubscribeAPServiceResponse dsubRes = invoker.DesubscribeAPService(userUid, serviceId, "Web", serviceId, null, dateFormat.format(new Date()), Constants.SUBSCRIBE_NOT_SEND_SMS, userId);
            if (dsubRes != null) {
                code = dsubRes.getReturnCode();
                exceptionString = dsubRes.getDescription();
                if (BusinessCommonCode.CSP_CODE_SUCCESS.equalsIgnoreCase(code)
                        || BusinessCommonCode.CSP_UNSUBSCRIBED.equalsIgnoreCase(code)) {
                    isUpdateData = true;
                } else {
                    resultCode = CspCode.UNSUBSCRIBE_FAIL;
                }
            } else {
                resultCode = CspCode.UNSUBSCRIBE_FAIL;
            }
        } catch (Throwable e) {
            return new ServiceResult<Boolean>(CspCode.CSP_NO_FEEDBACK, new Boolean(isUpdateData), code, exceptionString, e);
        }
        return new ServiceResult<Boolean>(resultCode, new Boolean(isUpdateData), code, exceptionString);
    }
}