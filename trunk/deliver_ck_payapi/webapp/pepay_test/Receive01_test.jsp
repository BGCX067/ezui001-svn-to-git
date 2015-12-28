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
	String paySelectUrl = "http://gate.pepay.com.tw/pepay/payselect_amt.php";

	String checkCode = PePayUtil.getPePayCheckCode(orderId, String.valueOf(amount));
%>
<form method="POST" action="http://210.202.111.104:8080/payapi/api/integrate/pepay/receive01">
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
			<td>檢查碼:</td>
			<td><%=checkCode%></td>
			<td><input type="hidden" name="CHECK_CODE" value="<%=checkCode%>"></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" name="BTN01" value="送出"></td>
		</tr>
	</table>
</form>