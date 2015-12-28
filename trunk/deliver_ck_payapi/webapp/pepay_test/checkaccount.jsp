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
	0:���\
	20001:�ˬd�X����
	20002:��Ʈw�s������
	20003:�Τᤣ�s�b
	20004:CallerID�|�����U
	20099:��L����
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
	String cSysTrustCode="aaaa1234";		//�t�ΫH���X
	String cShopTrustCode="bbbb5678";		//�t�ӫH���X
	
	String cTmp=cSysTrustCode + "#" + cShopID + "#" + cUserID + "#" + cShopTrustCode;
	String cTrustCode=md5(cTmp);
	
	if( !cCheckCode.equals(cTrustCode) ){
		nRes=20001;
	}else{
		/*
		Data Check
		
		$nRes:
		0:�Τ�s�b
		20002:��Ʈw�s������
		20003:�Τᤣ�s�b
		20004:CallerID�|�����U
		*/
		
		cShopPara="aaa#bbb#ccc";
		/*
		�t�Ӧۭq�Ѽ�(�p�t�ӻݭn�B�~���a��L��ƥi�Ѧ��a�J)
		��shoppara�@url encode�A�̫�t�Ӧ����A�ۦ��urldecode�ѽX
		*/
		cShopParaUrlEncode=java.net.URLEncoder.encode(cShopPara,"big5");
	}
	
	/*
	��X���GRES_CODE
	�]�p���ۭq�Ѽƫh�A�[�W&SHOP_PARA=xxx�]��Url Encode�^
	*/
	cOutput="&RES_CODE=" + nRes + "&SHOP_PARA=" + cShopParaUrlEncode + "&";
	out.print(cOutput);
%>