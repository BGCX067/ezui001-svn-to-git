<%--
  $Id$
--%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="cn.vstore.appserver.form.PePayForm"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sti" uri="http://www.sti.com.tw/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	PePayForm pePayForm = (PePayForm) request.getAttribute("pePayForm");
	int resCode = pePayForm.getResCode().getCode();
	String userId = pePayForm.getUserId();
	String returnUrl = pePayForm.getReturnUrl();
	String shopPara = pePayForm.getShopPara();
	if(StringUtils.isBlank(returnUrl) && StringUtils.isBlank(shopPara)) {
		out.print("USER_ID=" + userId + "&RES_CODE=" + resCode);
	} else if (StringUtils.isBlank(shopPara)){
		out.print("USER_ID=" + userId + "&RES_CODE=" + resCode + "&RET_URL=" + returnUrl);
	} else if (StringUtils.isBlank(returnUrl)){
		out.print("USER_ID=" + userId + "&SHOP_PARA=" + shopPara + "&RES_CODE=" + resCode);
	} else {
		out.print("USER_ID=" + userId + "&SHOP_PARA=" + shopPara + "&RET_URL=" + returnUrl + "&RES_CODE=" + resCode);
	}
%>