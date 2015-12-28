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
	20001:�t�ӰѼƶǰe����
	20002:�ˬd�X���~
	20003:�t�Ӹ�Ʈw�s������
	20004:�t�Ӹ�Ʈw�g�J����
	20099:��L����
	*/

	String cShopID=request.getParameter("SHOP_ID");
	String cOrderID=request.getParameter("ORDER_ID");
	String nAmount=request.getParameter("AMOUNT");
	String cCurrency=request.getParameter("CURRENCY");
	String cSessID=request.getParameter("SESS_ID");
	String cProdID=request.getParameter("PROD_ID");
	String cCheckCode=request.getParameter("CHECK_CODE");
	
	int nRes=0;
	String cOutput="";
	String cUserID="";
	String cShopPara="";
	String cShopParaUrlEncode="";
	String cRetUrl="";
	String cSysTrustCode="aaaa1234";		//�t�ΫH���X
	String cShopTrustCode="bbbb5678";		//�t�ӫH���X
	
	String cTmp=cSysTrustCode + "#" + cShopID + "#" + cOrderID + "#" + nAmount + "#" + cSessID + "#" + cProdID + "#" + cShopTrustCode;
	String cTrustCode=md5(cTmp);
	
	if(!cCheckCode.equals(cTrustCode)){
		nRes=20002;
	}else{
		//�d�߼t�Ӹӵ�ORDER_ID�����O���x�ȽXUSER_ID�]���]��0910123456�^
		cUserID="0910123456";
		cShopPara="aaa#bbb#ccc";
		//��shoppara�@url encode�A�̫�t�Ӧ����A�ۦ��urldecode�ѽX
		cShopParaUrlEncode=java.net.URLEncoder.encode(cShopPara,"big5");
		cRetUrl="http://www.xxx.com/xxx.jsp";
	}
	
	//��X���������USER_ID�ε��GRES_CODE
	//�]�p���ۭq�Ѽƫh�A�[�W&SHOP_PARA=xxx�]�@UrlEncode�^�B
	//������G�^�Ǻ��}�h�A�[�WRET_URL=http://www.xxx.com/xxx.jsp�^
	cOutput="USER_ID=" + cUserID + "&RES_CODE=" + nRes + "&SHOP_PARA=" + cShopParaUrlEncode + "&RET_URL=" + cRetUrl + "&";
	
	out.print(cOutput);
%>