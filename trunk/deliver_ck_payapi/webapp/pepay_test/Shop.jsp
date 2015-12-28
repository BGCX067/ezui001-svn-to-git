<%@page import="cn.vstore.appserver.api.support.PePayUtil"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@page import="java.security.*"%>
<%!public String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
		
	}%>
<%
	//以下為範例參數
	String sysTrustCode = "KZn3H4YH7t";
	String shopTrustCode = "48Q7nOiial";
	String shopId = "PPS_140270";
	String orderId = "99098716";
	String cOrderItem = "VtionAppTest";
	//商品名稱需作url encode
	String orderItemUrlEncode = java.net.URLEncoder.encode(cOrderItem, "big5");
	int amount = 1;
	String currency = "TWD";
	String paySelectUrl = "http://gate.pepay.com.tw/pepay/payselect_amt.php";

	String checkCode = PePayUtil.getPePayCheckCode(orderId, String.valueOf(amount));
%>
<form method="POST" action="<%=paySelectUrl%>">
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
			<td>商品名稱:</td>
			<td><%=orderItemUrlEncode%></td>
			<td><input type="hidden" name="ORDER_ITEM" value="<%=orderItemUrlEncode%>"></td>
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