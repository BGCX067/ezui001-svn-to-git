<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ page import="java.security.*"%>
<%@ page import="cn.vstore.appserver.api.support.PePayUtil"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%
	//以下為範例參數
	String sysTrustCode = "KZn3H4YH7t";
	String shopTrustCode = "48Q7nOiial";
	String shopId = "PPS_140270";
	String orderId = request.getParameter("orderId");
	if(StringUtils.isBlank(orderId)) {
		orderId = "1002012121700000414";
	}
	String cOrderItem = "VtionAppTest";
	//商品名稱需作url encode
	String orderItemUrlEncode = java.net.URLEncoder.encode(cOrderItem, "big5");
	int amount = 1;
	String currency = "TWD";
	String sessId = "testSess";
	String prodId = "testProd";
	String userId = "zusan";
	String paySelectUrl = "http://gate.pepay.com.tw/pepay/payselect_amt.php";
	String billId = "testBill";
	String dataId = "testData";
	String payType = "testPayType";
	String sourceAmount = "1";
	String dataCode = "testDataCode";
	String tradeCode = "testTradeCode";
	String cDate = "20121219";
	String cTime = "101010";
	String billDate = "20121220";
	String billTime = "111111";
	String date = "20121221";
	String time = "121212";

	String checkCode = PePayUtil.getPePayCheckCode(orderId, String.valueOf(amount), sessId, prodId, userId);
%>
<form method="POST" action="http://210.202.111.104:8080/payapi/api/integrate/pepay/receive02">
	<table>
		<tr>
			<td>廠商代碼:</td>
			<td><%=shopId%></td>
			<td><input type="hidden" name="SHOP_ID" value="<%=shopId%>"></td>
		</tr>
		<tr>
			<td>訂單編號:</td>
			<td><%=orderId%></td>
			<td><input type="hidden" name="ORDER_ID" value="<%=orderId%>"></td>
		</tr>
		<tr>
			<td>交易代碼:</td>
			<td><%=sessId%></td>
			<td><input type="hidden" name="SESS_ID" value="<%=sessId%>"></td>
		</tr>
		<tr>
			<td>金流代碼:</td>
			<td><%=prodId%></td>
			<td><input type="hidden" name="PROD_ID" value="<%=prodId%>"></td>
		</tr>
		<tr>
			<td>消費金額:</td>
			<td><%=amount%></td>
			<td><input type="hidden" name="AMOUNT" value="<%=amount%>"></td>
		</tr>
		<tr>
			<td>幣別:</td>
			<td><%=currency%></td>
			<td><input type="hidden" name="CURRENCY" value="<%=currency%>"></td>
		</tr>
<!-- 		<tr> -->
<!-- 			<td>自訂參數:</td> -->
<%-- 			<td><%=cShopParaUrlEncode%></td> --%>
<%-- 			<td><input type="text" name="SHOP_PARA" size="50" value="<%=cShopParaUrlEncode%>"></td> --%>
<!-- 		</tr> -->
		<tr>
			<td>消費者儲值碼:</td>
			<td><%=userId%></td>
			<td><input type="hidden" name="USER_ID" value="<%=userId%>"></td>
		</tr>
		<tr>
			<td>金流交易代碼:</td>
			<td><%=billId%></td>
			<td><input type="hidden" name="BILL_ID" value="<%=billId%>"></td>
		</tr>
		<tr>
			<td>消費者交易代碼:</td>
			<td><%=dataId%></td>
			<td><input type="hidden" name="DATA_ID" value="<%=dataId%>"></td>
		</tr>
		<tr>
			<td>PEPAY 消費類型:</td>
			<td><%=payType%></td>
			<td><input type="hidden" name="PAY_TYPE" value="<%=payType%>"></td>
		</tr>
		<tr>
			<td>原始輸入金額:</td>
			<td><%=sourceAmount%></td>
			<td><input type="hidden" name="SOURCE_AMOUNT" value="<%=sourceAmount%>"></td>
		</tr>
		<tr>
			<td>資料碼:</td>
			<td><%=dataCode%></td>
			<td><input type="hidden" name="DATA_CODE" value="<%=dataCode%>"></td>
		</tr>
		<tr>
			<td>金流交易結果碼:</td>
			<td><%=tradeCode%></td>
			<td><input type="hidden" name="TRADE_CODE" value="<%=tradeCode%>"></td>
		</tr>
		<tr>
			<td>建立交易日期::</td>
			<td><%=cDate%></td>
			<td><input type="hidden" name="CDATE" value="<%=cDate%>"></td>
		</tr>
		<tr>
			<td>建立交易時間:</td>
			<td><%=cTime%></td>
			<td><input type="hidden" name="CTIME" value="<%=cTime%>"></td>
		</tr>
		<tr>
			<td>帳單日期:</td>
			<td><%=billDate%></td>
			<td><input type="hidden" name="BILL_DATE" value="<%=billDate%>"></td>
		</tr>
		<tr>
			<td>帳單時間:</td>
			<td><%=billTime%></td>
			<td><input type="hidden" name="BILL_TIME" value="<%=billTime%>"></td>
		</tr>
		<tr>
			<td>完成交易日期:</td>
			<td><%=date%></td>
			<td><input type="hidden" name="DATE" value="<%=date%>"></td>
		</tr>
		<tr>
			<td>完成交易時間:</td>
			<td><%=time%></td>
			<td><input type="hidden" name="TIME" value="<%=time%>"></td>
		</tr>
		<tr>
			<td>檢查碼:</td>
			<td><%=checkCode%></td>
			<td><input type="hidden" name="CHECK_CODE" value="<%=checkCode%>"></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" name="BTN01" value="送出"></td>
		</tr>
	</table>
</form>