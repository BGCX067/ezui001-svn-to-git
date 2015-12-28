<%@page contentType="text/html;charset=big5" language="java" %>
<%@page import="java.security.*"%>
<%!
	public String md5(String s){
		char hexDigits[] = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd','e', 'f'};
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
		}
		catch (Exception e){
			return null;
		}
	}
%>
<%
	/*
	0:成功
	20001:檢查碼不符
	20002:資料庫連結失敗
	20003:用戶不存在
	20004:CallerID尚未註冊
	20099:其他失敗
	*/

	String cShopID=request.getParameter("SHOP_ID");
	String cUserID=request.getParameter("ORDER_ID");
	String cCallerID=request.getParameter("CALLER_ID");
	String cCheckCode=request.getParameter("CHECK_CODE");
	
	int nRes=0;
	String cOutput="";
// 	String cUserID="";
	String cShopPara="";
	String cShopParaUrlEncode="";
	String cSysTrustCode="aaaa1234";		//系統信任碼
	String cShopTrustCode="bbbb5678";		//廠商信任碼
	
	String cTmp=cSysTrustCode + "#" + cShopID + "#" + cUserID + "#" + cShopTrustCode;
	String cTrustCode=md5(cTmp);
	
	if( !cCheckCode.equals(cTrustCode) ){
		nRes=20001;
	}else{
		/*
		Data Check
		
		$nRes:
		0:用戶存在
		20002:資料庫連結失敗
		20003:用戶不存在
		20004:CallerID尚未註冊
		*/
		
		cShopPara="aaa#bbb#ccc";
		/*
		廠商自訂參數(如廠商需要額外夾帶其他資料可由此帶入)
		對shoppara作url encode，最後廠商收到後再自行用urldecode解碼
		*/
		cShopParaUrlEncode=java.net.URLEncoder.encode(cShopPara,"big5");
	}
	
	/*
	輸出結果RES_CODE
	（如有自訂參數則再加上&SHOP_PARA=xxx（做Url Encode）
	*/
	cOutput="&RES_CODE=" + nRes + "&SHOP_PARA=" + cShopParaUrlEncode + "&";
	out.print(cOutput);
%>