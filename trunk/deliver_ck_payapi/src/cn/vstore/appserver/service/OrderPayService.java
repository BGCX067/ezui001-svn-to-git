package cn.vstore.appserver.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.vstore.appserver.api.payment.unionpay.SignBy;
import cn.vstore.appserver.api.payment.unionpay.XmlDefinition;
import cn.vstore.appserver.api.payment.unionpay.XmlHttpConnection;
import cn.vstore.appserver.api.payment.yijiupay.OrderFor19payUtil;
import cn.vstore.appserver.form.payment.SelectOrderPayForm;
import cn.vstore.appserver.model.VMemberInfo;
import cn.vstore.appserver.model.VOrderPayInfo;
import cn.vstore.appserver.service.ResultCode.CommonCode;

@Service("OrderPayService")
public class OrderPayService {

	private static final Logger logger = LoggerFactory
			.getLogger(OrderPayService.class);

	@Autowired
	private SqlMapClientTemplate sqlMapClientTemplate;

	/**
	 * 购买道具
	 * 
	 * @param selectOrderPayForm
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceResult<VOrderPayInfo> getSZFNewOrderInfo(
			SelectOrderPayForm selectOrderPayForm) {
		// 插入订单数据
		VOrderPayInfo orderPayInfo = new VOrderPayInfo();

		orderPayInfo.setMerchantsid(selectOrderPayForm.getMerchantsid());// 商户ID
		orderPayInfo.setProductid(selectOrderPayForm.getProductid());// 设置神州付的privatekey
		orderPayInfo.setChannelid(selectOrderPayForm.getChannelid());// 设置神州付的deskey
		orderPayInfo.setVersion(selectOrderPayForm.getVersion());// 版本
		orderPayInfo.setVerifytype(selectOrderPayForm.getVerifytype());// 加密类型(为1时，只MD5校验)
		orderPayInfo.setMerusername(selectOrderPayForm.getMerusername());// 商户用户名
		orderPayInfo.setMeruseremail(selectOrderPayForm.getMeruseremail());// 商户邮箱
		orderPayInfo.setPrivatefield(selectOrderPayForm.getPrivatefield());// 商户私有数据
		orderPayInfo.setOrderno(selectOrderPayForm.getOrderno());// 订单号码
		orderPayInfo.setCardno(selectOrderPayForm.getCardno());// 卡号
		orderPayInfo.setCardpwd(selectOrderPayForm.getCardpwd());// 卡密
		orderPayInfo.setCardtype(selectOrderPayForm.getCardtype());// 卡类型
		orderPayInfo.setCardinfo(selectOrderPayForm.getCardinfo());// 卡加密信息
		orderPayInfo.setOrderstatus("0");// 订单状态(0未支付2支付成功)
		if (!"".equals(selectOrderPayForm.getDenomination()) && selectOrderPayForm.getDenomination() != null) {
			orderPayInfo.setDenomination(Double.valueOf(selectOrderPayForm .getDenomination()));// 卡面额
		}
		if (!"".equals(selectOrderPayForm.getAmount()) && selectOrderPayForm.getAmount() != null) {
			orderPayInfo.setAmount(Double.valueOf(selectOrderPayForm.getAmount()));// 支付金额
		}
		orderPayInfo.setItemid(selectOrderPayForm.getItemid());// 道具ID
		// orderPayInfo.setTimestamp(selectOrderPayForm.getTimestamp());//时间字符串
		orderPayInfo.setCreatedate(selectOrderPayForm.getCreatedate());// 创建日期
		orderPayInfo.setPkgname(selectOrderPayForm.getPackageName());// 包名
		orderPayInfo.setUserid(selectOrderPayForm.getUserID());// 用户ID
		if (!"".equals(selectOrderPayForm.getMemid()) && selectOrderPayForm.getMemid() != null) {
			orderPayInfo.setMemid(selectOrderPayForm.getMemid());// 会员ID
		}
		orderPayInfo.setItemid(selectOrderPayForm.getItemid());// 道具ID
		orderPayInfo.setPropstype(selectOrderPayForm.getPropsType());//道具类型
		orderPayInfo.setNums(selectOrderPayForm.getNums());//道具数量
		orderPayInfo.setPaytype(selectOrderPayForm.getPaytype());// 购买方式
		Long resultInt = (Long) sqlMapClientTemplate.insert(
				"v_order_pay_info.ibatorgenerated_insertSelective",
				orderPayInfo);
		// if(resultInt > 0){
		// 插入订单日志
		// }
		logger.debug("order increment id=" + resultInt);
		logger.debug("insert order id=" + selectOrderPayForm.getOrderno()
				+ " and amount=" + selectOrderPayForm.getAmount());
		return new ServiceResult<VOrderPayInfo>(CommonCode.SUCCESS,
				orderPayInfo);

	}

	/**
	 * 更新神州付付费成功后的订单信息
	 * 
	 * @param self
	 * @param status
	 * @param florderId
	 * @param cardStatus
	 * @param orderId
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateOrderStatusByOrderId(int status, String orderno) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("status", status);
		hm.put("orderno", orderno);
		sqlMapClientTemplate.update("v_order_pay_info.updateStatusByOrderNo", hm);
	}

	/**
	 * 查询订单支付结果信息
	 * 
	 * @param self
	 * @param status
	 * @param florderId
	 * @param cardStatus
	 * @param orderId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public VOrderPayInfo selectOrderStatusByOrderId(String orderno) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("orderno", orderno);
		return (VOrderPayInfo) sqlMapClientTemplate.queryForObject(
				"v_order_pay_info.selectOrderStatusByOrderId", hm);
	}

	/**
	 * 查询会员信息
	 * 
	 * @param self
	 * @param status
	 * @param florderId
	 * @param cardStatus
	 * @param orderId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public VMemberInfo selectMemInfoByLoginId(String loginId) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("loginId", loginId);
		return (VMemberInfo) sqlMapClientTemplate.queryForObject(
				"v_member_info.getMemInfoByLoginId", hm);
	}

	/**
	 * 91pay下单
	 * 
	 * @param selectPaymentForm
	 * @param priceType
	 * @param requestUrl
	 * @param responseUrl
	 * @return
	 */
	public ServiceResult<VOrderPayInfo> orderFor19pay(
			SelectOrderPayForm selectPaymentForm, String priceType,
			String requestUrl, String responseUrl) {
		if (!this.procssOrderFor19pay(selectPaymentForm, priceType, requestUrl,
				responseUrl)) {
			logger.debug("Fail to check sign.");
			return new ServiceResult<VOrderPayInfo>(CommonCode.SERVICE_FAIL);
		} else {
			return this.getSZFNewOrderInfo(selectPaymentForm);
		}
	}

	/**
	 * 印联下单
	 * 
	 * @param selectPaymentForm
	 * @param priceType
	 * @param requestUrl
	 * @param responseUrl
	 * @return
	 */
	public ServiceResult<VOrderPayInfo> getNewOrder(String merchantOrderDesc,
			String checkSignUrl, String merchantPublicCe, String backUrl,
			SelectOrderPayForm selectPaymentForm, String privateKeyFileName,
			String privateKeyAlias, String privateKeyPassword, boolean test) {
		if (!checkSignForUnionPay(merchantOrderDesc, checkSignUrl,
				merchantPublicCe, backUrl, selectPaymentForm,
				privateKeyFileName, privateKeyAlias, privateKeyPassword, test)) {
			logger.debug("Fail to check sign.");
			return new ServiceResult<VOrderPayInfo>(CommonCode.SERVICE_FAIL);
		} else {
			return this.getSZFNewOrderInfo(selectPaymentForm);
		}
	}

	/**
	 * 91pay处理订单信息
	 * 
	 * @param selectPaymentForm
	 * @param priceType
	 * @param requestUrl
	 * @param responseUrl
	 * @return
	 */
	private boolean procssOrderFor19pay(SelectOrderPayForm selectPaymentForm,
			String priceType, String requestUrl, String responseUrl) {
		String order_date = new SimpleDateFormat("yyyyMMdd")
				.format(selectPaymentForm.getCreatedate());
		try {
			// 生成sign
			String sign = OrderFor19payUtil.createOriginalSign(
					selectPaymentForm.getVersion(),
					selectPaymentForm.getMerchantsid(), order_date,
					selectPaymentForm.getOrderno(),
					selectPaymentForm.getAmount(), priceType,
					selectPaymentForm.getCardno(),
					selectPaymentForm.getCardpwd(),
					selectPaymentForm.getCardtype(),
					selectPaymentForm.getChannelid(),
					selectPaymentForm.getPrivateKey());
			// 生成查询参数
			String paramContent = OrderFor19payUtil.createRequestParams(
					selectPaymentForm.getVersion(),
					selectPaymentForm.getMerchantsid(), order_date,
					selectPaymentForm.getOrderno(),
					selectPaymentForm.getAmount(), priceType,
					selectPaymentForm.getCardno(),
					selectPaymentForm.getCardpwd(),
					selectPaymentForm.getCardtype(),
					selectPaymentForm.getChannelid(), sign, "",
					selectPaymentForm.getUserID(), "", "", "", "", responseUrl);
			StringBuffer responseMessage = null;
			java.net.URLConnection connection = null;
			java.net.URL reqUrl = null;
			OutputStreamWriter reqOut = null;
			InputStream in = null;
			BufferedReader br = null;
			// String param = paramContent;

			responseMessage = new StringBuffer();
			reqUrl = new java.net.URL(requestUrl);
			connection = reqUrl.openConnection();
			connection.setDoOutput(true);
			connection.setConnectTimeout(300 * 1000);
			connection.setReadTimeout(300 * 1000);

			reqOut = new OutputStreamWriter(connection.getOutputStream());

			reqOut.write(paramContent);
			reqOut.flush();

			int charCount = -1;
			in = connection.getInputStream();
			br = new BufferedReader(new InputStreamReader(in, "GBK"));
			while ((charCount = br.read()) != -1) {
				responseMessage.append((char) charCount);
			}
			in.close();
			reqOut.close();
			String res = responseMessage.toString();
			// 成功收到验签结果报文
			if (res != null && res.indexOf("<result>P</result>") != -1) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 银联处理订单信息
	 * 
	 * @param merchantOrderDesc
	 * @param checkSignUrl
	 * @param merchantPublicCe
	 * @param backUrl
	 * @param selectPaymentForm
	 * @param privateKeyFileName
	 * @param privateKeyAlias
	 * @param privateKeyPassword
	 * @param test
	 * @return
	 */
	private boolean checkSignForUnionPay(String merchantOrderDesc,
			String checkSignUrl, String merchantPublicCe, String backUrl,
			SelectOrderPayForm selectPaymentForm, String privateKeyFileName,
			String privateKeyAlias, String privateKeyPassword, boolean test) {
		// 使用银联付费时，需将订单资讯送交银联前置进行验签
		String merchantOrderId = selectPaymentForm.getOrderno();
		String merchantOrderTime = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(selectPaymentForm.getCreatedate());
		String merchantOrderAmt = String.valueOf(Math.round(Double
				.parseDouble(selectPaymentForm.getAmount()) * 100D));
		String originalsign7 = XmlDefinition.CreateOriginalSign7(
				selectPaymentForm.getMerusername(),
				selectPaymentForm.getMerchantsid(), merchantOrderId,
				merchantOrderTime, merchantOrderAmt, merchantOrderDesc, "");
		logger.info("original string:" + originalsign7);
		SignBy getSign = new SignBy();
		String xmlSign7;
		// 读取私钥档案
		try {
			InputStream PrivateSign = getClass().getClassLoader()
					.getResourceAsStream(privateKeyFileName);
			xmlSign7 = getSign.createSign(originalsign7, privateKeyAlias,
					privateKeyPassword, PrivateSign);
			logger.info("signed string:" + xmlSign7);
		} catch (Exception e) {
			logger.debug("获取私钥加密过程异常，请检查私钥档案");
			e.printStackTrace();
			return false;
		}
		// 订单提交的报文
		String Submit = XmlDefinition.SubmitOrder(test,
				selectPaymentForm.getMerusername(),
				selectPaymentForm.getMerchantsid(), merchantOrderId,
				merchantOrderTime, merchantOrderAmt, merchantOrderDesc, "",
				backUrl, xmlSign7, merchantPublicCe);
		XmlHttpConnection Xml_SetHttp = new XmlHttpConnection(checkSignUrl,
				6000);
		if (Xml_SetHttp.sendMsg(Submit) && // 提交报文成功
				Xml_SetHttp.getReMeg() != null && // 成功收到验签结果报文
				Xml_SetHttp.getReMeg().indexOf("<respCode>0000</respCode>") != -1) {// 验签成功
			return true;
		} else {
			return false;// 验签失败
		}

	}

}