package cn.vstore.appserver.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.model.Credential;
import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.model.StoreInfo;
import cn.vstore.appserver.service.ResultCode.AutoLoginCode;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.LoginCode;
import cn.vstore.core.model.vo.Account;
import cn.vstore.core.model.vo.AccountExample;

import com.sti.util.StringUtil;

@Service("AuthenticationService")
public class AuthenticationService {
	protected static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;

    @Autowired
    private CspService cspService;

    @Autowired
    private BlankListService blankListService;

    /**
     * login : 01. 登入 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Credential> login(AuthenticationService self, String userId, String pwd,
        String imei, String iccid) {

        // 過濾參數中不必要字元
        userId = userId.replace("\n", "").trim();

        try {

            // 是否有登入過
            boolean isUsed = false;

            // 取得使用者資訊
            AccountExample ae=new AccountExample();
    		ae.createCriteria().andLoginIdEqualTo(userId.toUpperCase());
    		Account acc = (Account)sqlMapClientTemplate.queryForObject("account.ibatorgenerated_selectByExample", ae);
    		Prosumer prosumer=null;
            if (acc != null) {
                prosumer = self.getProsumerByUserId(userId);
            	if(prosumer!=null){
	            	if(new Integer(1).equals(prosumer.getStatus())){
	            		String spwd = StringUtil.AES128CBCEncrypt(pwd, "aseyb129clet9vle");
	            		if(spwd.equals(acc.getPasswd())){
	            			isUsed = true;
	            		}else{
	                    	ResultCode failResultCode = LoginCode.LOGIN_INVALID_PWD;
	                        return new ServiceResult<Credential>(failResultCode, null);
	            		}
	            	}else{
	                	ResultCode failResultCode = LoginCode.LOGIN_INVALID_ACCOUNT;
	                    return new ServiceResult<Credential>(failResultCode, null);
	            	}
            	}else{
            		//沒有一般user的身份
                    return new ServiceResult<Credential>(LoginCode.NO_PROSUMER_ACCOUNT, null);
            	}
            }
            else {
            	ResultCode failResultCode = LoginCode.LOGIN_NO_ACCOUNT;
                return new ServiceResult<Credential>(failResultCode, null);
            }

            // 存入login時間以及userId
            prosumer.setUserId(userId);
            prosumer.setLoginTime(new Timestamp(System.currentTimeMillis()));

            // 存入Account，條件為新的Token(MD5),iccid,imei
            prosumer.setAccount(new cn.vstore.appserver.model.Account(prosumer.getNewToken(), iccid, imei));
            // 取出user的白名單類型
            prosumer.setBlankType(new Integer(blankListService.getBlankTypeValueByUserId(userId)));

            // 存入 Prosumer
            self.saveOpUpdateProsumer(self, prosumer, isUsed);
            Credential credential = prosumer.getCredential();
            credential.setNickName(acc.getNickname());
            return new ServiceResult<Credential>(CommonCode.SUCCESS, credential);
        } catch (Throwable e) {
            ResultCode failResultCode = CommonCode.SERVICE_FAIL.bindSource(CommonCode.SERVICE_FAIL);
            return new ServiceResult<Credential>(failResultCode, null, e);
        }

    }

    /**
     * iPLogin : 03.IpLoginApi IP登入 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Credential> ipLogin(AuthenticationService self, String ipAddress,
        String imei, String iccid) {

    	return null;
//        try {
//            // CSP的IP驗證並回傳userId
////            ServiceResult<GetUserMSISDNByIPResponse> cspCredentialResult = cspService.credentialIp(ipAddress);
////            ResultCode resultCode = cspCredentialResult.getResult();
////            if (!CspCode.CSP_SUCCESS.equals(resultCode))
////                return new ServiceResult<Credential>(IpLoginCode.LOGIN_FAIL.bindSource(resultCode), null, cspCredentialResult.getThrowable());
//
//            // 取得CSP回傳資訊
////            GetUserMSISDNByIPResponse getUserMSISDNByIP = cspCredentialResult.getData();
////            String userId = getUserMSISDNByIP.getMsisdn();
////            String code = getUserMSISDNByIP.getReturnCode();
////            String des = getUserMSISDNByIP.getDescription();
//
//            // 從CSP取得userUid
////            ServiceResult<GetUserUIDResponse> cspUserUidResult = cspService.getUserUid(userId);
////            if (!CspCode.CSP_SUCCESS.equals(cspUserUidResult.getResult())) {
////                ResultCode failResultCode = ResultCode.LoginCode.LOGIN_SERVICE_FAIL.bindSource(cspUserUidResult.getResult());
////                return new ServiceResult<Credential>(failResultCode, null, cspUserUidResult.getThrowable());
////            }
//
//            // 是否有登入過
//            boolean isUsed = false;
//            // 取得使用者資訊
//            Prosumer prosumer = self.getProsumerByUserIdAndPassword(userId);
//            if (prosumer != null)
//                isUsed = true;
//            else
//                prosumer = new Prosumer();
//            // 取得CSP回傳資訊
//            String userUid = cspUserUidResult.getData().getUID();
//            prosumer.setUserUid(userUid);
//
//            // 存入login時間以及userId
//            prosumer.setUserId(userId);
//            prosumer.setLoginTime(new Timestamp(new Date().getTime()));
//
//            // 存入Account，條件為新的Token(MD5),iccid,imei
//            prosumer.setAccount(new Account(prosumer.getNewToken(), iccid, imei));
//
//            // 當registerService = 0，需call ibm的csp取得註冊
//            if (prosumer.getRegisterService() == Prosumer.ZERO_SERVICE_UNREGISTER) {
//                ServiceResult<Boolean> registerResult = self.registerZeroService(self, userUid, userId, iccid, imei);
//                if (!CommonCode.SUCCESS.getCompleteCode().equals(registerResult.getResult().getCompleteCode())) {
//                    ResultCode failResultCode = ResultCode.LoginCode.LOGIN_SERVICE_FAIL.bindSource(registerResult.getResult());
//                    return new ServiceResult<Credential>(failResultCode, null, registerResult.getThrowable());
//                }
//                if (registerResult.getData() == true)
//                    prosumer.setRegisterService(Prosumer.ZERO_SERVICE_REGISTERED);
//            }
//
//            // 取出user的白名單類型
//            prosumer.setBlankType(new Integer(blankListService.getBlankTypeValueByUserId(userId)));
//
//            // 存取 Prosumer
//            try {
//                self.saveOpUpdateProsumer(self, prosumer, isUsed, code, des);
//                return new ServiceResult<Credential>(CommonCode.SUCCESS, prosumer.getCredential());
//            } catch (Throwable e) {
//                return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
//            }
//
//        } catch (Throwable e) {
//            return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
//        }

    }

    /**
     * autoLogin : 02. 自動登入 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Credential> autoLogin(AuthenticationService self, String imei,
        String iccid, String token) {

        Prosumer prosumer = null;

        try {

            // 取得使用者資訊
            prosumer = self.getProsumerByAccount(token);
            if (prosumer == null)
                return new ServiceResult<Credential>(AutoLoginCode.AUTO_LOGIN_FAIL, null);

            // 取得白名單中blankType的值
            prosumer.setBlankType(new Integer(blankListService.getBlankTypeValueByUserId(prosumer.getUserId())));

            // MD5 (取得 Token)
            prosumer.setToken(prosumer.getNewToken());

            // 存取 Prosumer
            try {
                self.updateProsumerAutoLoginData(self, prosumer);
            } catch (Throwable e) {
                return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
            }

        } catch (Throwable e) {
            return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
        }
        Credential credential = prosumer.getCredential();
        if("".equals(prosumer.getNickName()) || prosumer.getNickName() == null){
        	credential.setNickName(prosumer.getUserName());
        }else{
        	 credential.setNickName(prosumer.getNickName());
        }
        return new ServiceResult<Credential>(CommonCode.SUCCESS, credential);
    }
    /**
     * VpadLogin : 03.VpadLoginApi IP登入 service
     */
    @Transactional(propagation = Propagation.NEVER)
    public ServiceResult<Credential> vpadLogin(AuthenticationService self,
        String imei, String iccid) {
    	try {
    		Prosumer prosumer = self.getProsumerByImeiForVpad(imei);
    		if(prosumer==null){
    			logger.debug("Will register new user "+imei);
    			prosumer=new Prosumer();
    			prosumer.setBlankType(100);
    			prosumer.setIccid(iccid);
    			prosumer.setImei(imei);
    			prosumer.setIsSim(0);
    			prosumer.setPassword("");
    			prosumer.setRegisterService(0);
    			prosumer.setUserId(imei);
    			prosumer.setUserName(imei);
    			prosumer.setUserUid(imei);
    			prosumer.setToken(prosumer.getNewToken());
    			self.saveOpUpdateProsumer(self, prosumer, false);
    		}else{
    			prosumer.setToken(prosumer.getNewToken());
    			logger.debug("Will update user "+imei+" token="+prosumer.getToken());
    			self.saveOpUpdateProsumer(self, prosumer, true);
    		}
    		return new ServiceResult<Credential>(CommonCode.SUCCESS, prosumer.getCredential());
    	} catch (Throwable e) {
    		logger.error(e.getMessage(),e);
    		return new ServiceResult<Credential>(CommonCode.SERVICE_FAIL, null, e);
    	}
    }

    /**
     * 更新PROSUMER中LOGIN_TIME,BLANK_TYPE,REGISTER_SERVICE值
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateProsumerAutoLoginData(AuthenticationService self, Prosumer prosumer) {
        sqlMapClientTemplate.update("Auth.updateProsumerAutoLoginData", prosumer);
    }

    /**
     * 取得使用者資訊，條件為user_id= imei, user_uid=imei, user_name=imei, imei=imei / login_time != null / token != null
     */
    Prosumer getProsumerByImeiForVpad(String imei) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("imei", imei);
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByImeiForVpad", map);
    }
    /**
     * 取得使用者資訊，條件為token
     */
    public Prosumer getProsumerByAccount(String token) {
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByToken", new cn.vstore.appserver.model.Account(token, null,null));
    }
    
    /**
     * 取得使用者資訊，條件為resetToken
     */
    public Prosumer getProsumerByPwdForm(String userId, String storeId, String resetToken) {
    	HashMap<String, Object> hm = new HashMap<String, Object>();
    	hm.put("PwdResetToken", resetToken);
    	hm.put("StoreId", storeId);
    	hm.put("UserId", userId);
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByResetToken", hm);
    }
    
//    /**
//     * 取得使用者資訊，條件為account物件即token,iccid,imei
//     */
//    public Prosumer getProsumerByAccount(cn.vstore.appserver.model.Account account) {
//        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByAccount", account);
//    }
//
//    /**
//     * 取得使用者資訊
//     */
//    public Prosumer getProsumerByAccount(String token, String iccid, String imei) {
//        return getProsumerByAccount(new cn.vstore.appserver.model.Account(token, iccid, imei));
//    }

//    /**
//     * 取得使用者資訊，條件為iccid / imei / login_time != null
//     */
//    Prosumer getProsumerByImeiAndIccid(String iccid, String imei) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("iccid", iccid);
//        map.put("imei", imei);
//        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByImeiAndIccid", map);
//    }

    /**
     * 存取 Prosumer
     * 
     * @param self : AuthenticationService
     * @param prosumer : Prosumer物件
     * @param isUsed : true則update， false則insert
     * @param cspCode : CSP 回傳的 code
     * @param cspDesc : CSP 回傳的 description
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOpUpdateProsumer(AuthenticationService self, Prosumer prosumer, boolean isUsed) {
        // 如果已有之前登入過的資訊, 則update TOKEN 和 iccid 和 imei 和LOGIN_TIME
        if (isUsed) {
            // Update PROSUMER : 更新PROSUMER資訊 
            sqlMapClientTemplate.update("Auth.updateProsumer", prosumer);
        } else {
            // Insert PROSUMER : 儲存PROSUMER資訊
            sqlMapClientTemplate.update("Auth.insertProsumer", prosumer);
        }

        updateProsumerLog(prosumer);
    }
    
    //更新Prosumer
    public void updateProsumerLog(Prosumer prosumer){
    	{
            // Update PROSUMER_LOG IS_DELETE to 1
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userUid", prosumer.getUserUid());
            sqlMapClientTemplate.update("Auth.updateProsumerLogIsDelete", map);
        }

        {
            // 儲存PROSUMER_LOG資訊, IS_DELETE為0(成功)
            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("code", (cspCode != null) ? cspCode.trim() : null);
//            map.put("des", (cspDesc != null) ? cspDesc.trim() : null);
            map.put("userId", (prosumer.getUserId() != null) ? prosumer.getUserId().trim() : null);
            map.put("username", (prosumer.getUserName() != null) ? prosumer.getUserName().trim()
                    : null);
            map.put("token", (prosumer.getToken() != null) ? prosumer.getToken().trim() : null);
            map.put("iccid", (prosumer.getIccid() != null) ? prosumer.getIccid().trim() : null);
            map.put("imei", (prosumer.getImei() != null) ? prosumer.getImei().trim() : null);
            map.put("blankType", prosumer.getBlankType());
            map.put("userUid", prosumer.getUserUid());
//            map.put("registerService", new Integer(prosumer.getRegisterService()));
//            map.put("password", prosumer.getPassword());
            sqlMapClientTemplate.update("Auth.insertProsumerLog", map);
        }
    }

    @Transactional(propagation = Propagation.NEVER)
    public StoreInfo getStore(String store){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("storePkgName", store);
        return (StoreInfo) sqlMapClientTemplate.queryForObject("Auth.getStore", map);
    }
    /**
     * 取得使用者資訊，條件為userId
     */
    public Prosumer getProsumerByUserId(String userId) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("userId", userId);
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByUserId", map);
    }
    
    /**
     * 取得使用者資訊，條件為userId,storeId
     */
    public Prosumer getProsumerByUserIdAndStoreId(String userId,String storeId) {
        HashMap<String , Object> hm = new HashMap<String, Object>();
    	hm.put("USERID", userId);
    	hm.put("STOREID", storeId);
    	return (Prosumer)sqlMapClientTemplate.queryForObject("Auth.getProsumerByUserIdAndStoreId",hm);
    }
    
    /**
     * 取得使用者資訊，條件為userId, password
     */
    Prosumer getProsumerByUserIdAndPassword(String userId, String pwd) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("userId", userId);
    	map.put("password", pwd);
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByUserIdAndPassword", map);
    }
    
    /**
     * 取得使用者資訊，條件為userId, password
     */
    Prosumer getProsumerByUserIdAndPasswordAndStoreId(String userId, String pwd, BigDecimal storeId) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("userId", userId);
    	map.put("password", pwd);
    	map.put("storeId", storeId);
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByUserIdAndPasswordAndStoreId", map);
    }
    
    /**
     * 取得使用者資訊，條件為nickname
     */
    Prosumer getProsumerByNickname(String nickname) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("nickname", nickname);
        return (Prosumer) sqlMapClientTemplate.queryForObject("Auth.getProsumerByNickname", map);
    }

    /**
     * 檢查是否註冊Zero service
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public ServiceResult<Boolean> registerZeroService(AuthenticationService self, String userUid,
        String userId, String iccid, String imei) {
        // 向csp註冊Zero service
        ServiceResult<Boolean> cspResult = cspService.registerZeroService(userUid, userId);

        // insert PROSUMER_REGISTER_LOG
        self.insertProsumerRegisterLog(self, cspResult.getCode(), cspResult.getDes(), userUid, userId, iccid, imei);

        return new ServiceResult<Boolean>(cspResult.getResult(), cspResult.getData());
    }

    /**
     * Insert PROSUMER_REGISTER_LOG : 儲存註冊資訊
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertProsumerRegisterLog(AuthenticationService self, String cpsCode, String cspDesc,
        String userUid, String userId, String iccid, String imei) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("retCode", (cpsCode != null) ? cpsCode.trim() : null);
        map.put("retMsg", (cspDesc != null) ? cspDesc.trim() : null);
        map.put("userid", (userId != null) ? userId.trim().toUpperCase() : null);
        map.put("iccid", (iccid != null) ? iccid.trim() : null);
        map.put("imei", (imei != null) ? imei.trim() : null);
        map.put("userUid", userUid);
        sqlMapClientTemplate.insert("Auth.insertProsumerRegisterLog", map);
    }
    
    
    /**
     * 更新pwd和resetToken
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updatePwdAndPwdResetToken( Prosumer prosumer ) {
        return sqlMapClientTemplate.update("Auth.updatePwdAndPwdResetToken", prosumer);
    }
    
    
    /**
     * 更新PROSUMER
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateProsumer( Prosumer prosumer ) {
        return sqlMapClientTemplate.update("Auth.updateProsumer", prosumer);
    }
    
    
    
}