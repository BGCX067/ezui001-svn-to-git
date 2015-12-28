package cn.vstore.appserver.service;

import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.smart.appstore.server.api.Constants;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

//import cn.vstore.appserver.model.Application;
//import cn.vstore.appserver.model.PaymentInformation;
//import cn.vstore.appserver.model.Prosumer;
import cn.vstore.appserver.model.Recommender;
import cn.vstore.appserver.model.RecommenderApkList;
//import cn.vstore.appserver.model.UserPayment;
import cn.vstore.appserver.service.ResultCode.CommonCode;
import cn.vstore.appserver.service.ResultCode.RecommenderCode;
/**
 * 29.	Web 達人(31) PC Web通用
 * @author user
 *
 */
@Service("RecommenderService")
public class RecommenderService {
	
	private static final Logger logger = LoggerFactory.getLogger(RecommenderService.class);
	
	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;
	
//	@Autowired
//	private AuthenticationService authenticationService;
	
	@Autowired
	private ConstantService constantService;
	
//	@Autowired
//	private PaymentService paymentService;
	
	public ServiceResult<Recommender> getRecommender(String id,BigDecimal storeId){
		logger.info("getRecommender parameter id = " + id);
		Recommender recommender = null;
		try {
//			沒有ID，就直接取第一筆
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("storeId", storeId);
			if(id!=null){
				map.put("id", id);
				recommender = (Recommender)sqlMapClientTemplate.queryForObject("Recommender.getRecommender", map);			
			}else{
				recommender = (Recommender)sqlMapClientTemplate.queryForObject("Recommender.getRecommenderNoId",map);
			}
			if(recommender==null){
				return new ServiceResult<Recommender>(RecommenderCode.No_RECOMMENDER, recommender);
			}
			return new ServiceResult<Recommender>(CommonCode.SUCCESS, recommender);
		} catch (Throwable e) {
			return new ServiceResult<Recommender>(CommonCode.SERVICE_FAIL, null, e);
		}
	}
	/**
	 * 30.	Web 達人推薦app(32) PC Web通用
	 * @param storeId
	 * @param pageNo
	 * @param psize
	 * @return
	 */
	public ServiceResult<PagedRecommenderApkList> getRecommenderApkListApi(BigDecimal storeId, int pageNo, int psize){
		try {
			int pageSize = 0;
			// 每頁評論的最大筆數
			if(psize!=0){
				pageSize = psize;
			}else{
				pageSize = constantService.getRecommenderApkListPageSize();
			}		
//			權限驗證
//			Prosumer prosumer = authenticationService.getProsumerByAccount(token);
//			String userId =null;
//			String userUid =null;
//			if(prosumer!=null){
//				userId = prosumer.getUserId();
//				userUid = prosumer.getUserUid();
//				logger.debug("userId : " + userId + "userUid : " + userUid);
				
//		取出recommenderApkList總筆數
				int startNo = (pageNo - 1) * pageSize;
				int endNo = pageNo * pageSize;
				Map<String, Object> countMap = new HashMap<String, Object>();
				countMap.put("storeId", storeId);
				Integer returnCount = (Integer) sqlMapClientTemplate.queryForObject("Recommender.getRecommenderApkListCount", countMap);
				if (returnCount == null || returnCount.intValue() < 1) {
					PagedRecommenderApkList pagedRecommenderApkList = new PagedRecommenderApkList(true, new RecommenderApkList[0], returnCount);
					return new ServiceResult<PagedRecommenderApkList>(CommonCode.SUCCESS, pagedRecommenderApkList);
				}
//      取出recommenderApkList列表
				int recommenderApkCount = returnCount.intValue();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("startNo", startNo);
				map.put("endNo", pageSize);
				map.put("storeId", storeId);
				List<RecommenderApkList> recommenderApkList = sqlMapClientTemplate.queryForList("Recommender.getRecommenderApkList", map);
				// 取得hostpath
	            String hostPath = constantService.getHostPath();
	            if(recommenderApkList!=null){
	            	for(RecommenderApkList r : recommenderApkList){
	                    // icon url前加上hostPath
	                    if (StringUtils.isNotBlank(r.getIcon()) && r.getIcon().charAt(0) == '/')
	                        r.setIcon(hostPath + r.getIcon());
	            	}
	            }
				
//				if(!recommenderApkList.isEmpty()){
//					for(RecommenderApkList r : recommenderApkList){
//						String priceType = r.getPriceType();
//				取得付費資訊
//						List<String> appPkgList = new ArrayList<String>();
//						appPkgList.add(r.getPkg());
//						List<PaymentInformation> paymentInformationList = paymentService.getPaymentInformations(paymentService, userId, imei, userUid, appPkgList, storeId);
//						
//						boolean isPayment = false;
//						if(paymentInformationList.isEmpty()){
//							isPayment = true;
//							for(PaymentInformation paymentInfo : paymentInformationList){
//								String myPriceType = paymentInfo.getMyPriceType();
//								int payStatus = paymentInfo.getStatus();
//								
////						如果是測試用假付款成功status=6,回傳到手機的status改為2(付款成功)
//								if(payStatus == UserPayment.STATUS_PAYMENT_TEST_SUCCESS){
//									payStatus = UserPayment.STATUS_PAYMENT_SUCCESS;
////						如果是測試用假訂單取消status=7,回傳到手機的status改為5(訂單取消)
//								}else if(payStatus == UserPayment.STATUS_PAYMENT_TEST_CANCEL){
//									payStatus = UserPayment.STATUS_PAYMENT_FAIL;
//								}
////						如果myPriceType=3且付費myPriceType改為1,沒付費改為0
//								if(myPriceType != null && myPriceType.length() > 0 && myPriceType.trim().equals(Application.PRICE_INNAPP_PURCHASE)){
//									r.setPriceType(Application.PRICE_TYPE_ONCE);
//								}else{
//									r.setPriceType(myPriceType);
//								}
//								
//								r.setPayStatus(payStatus);
//								
//								if(myPriceType!=null && myPriceType.equals("2") && payStatus==5){
//									try {
//										Date endTime = paymentInfo.getRightEndDate();
//										if(endTime != null){
//											
//										}
//									} catch (Exception e) {
//									}
//								}
//							}
//						}
						
//				如果priceType=3且付費priceType改為1,沒付費改為0，priceType=4的priceType改為0
//				for(RecommenderApkList r : recommenderApkList){
//					String priceType = r.getPriceType();				
//					if(priceType != null && priceType.length() > 0 && priceType.trim().equals(Application.PRICE_INNAPP_PURCHASE)){
//						if(isPayment == true){
//							r.setPriceType(Application.PRICE_TYPE_ONCE);
//						}else{
//							r.setPriceType(Application.PRICE_TYPE_FREE);
//						}
//					}else if(priceType != null && priceType.length() > 0 && priceType.trim().equals(Application.PRICE_INNAPP_CAR)){
//						r.setPriceType(Application.PRICE_TYPE_FREE);
//					}else{
//						r.setPriceType(priceType);
//					}
//				}
//					}
//					
//				}
				
				boolean isEnd = pageNo * pageSize >= recommenderApkCount;
				RecommenderApkList[] recommenderApkLists = recommenderApkList.toArray(new RecommenderApkList[recommenderApkList.size()]);
				PagedRecommenderApkList pagedRecommenderApkList = new PagedRecommenderApkList(isEnd, recommenderApkLists, returnCount);
				
				return new ServiceResult<PagedRecommenderApkList>(CommonCode.SUCCESS, pagedRecommenderApkList);
//			}else{
//				return new ServiceResult<PagedRecommenderApkList>(CommonCode.NOT_IN_BLANK_LIST);
//			}
		} catch (Throwable e) {
			return new ServiceResult<PagedRecommenderApkList>(CommonCode.SERVICE_FAIL,null,e);
		}
	}
}
