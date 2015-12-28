<%--
  $Id$
--%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sti" uri="http://www.sti.com.tw/jsp/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());%>
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename="/applicationMessage" var="applicationBundle"/>
<c:set var="jsonString" >
{
    ret: '${ret}',
    order: 
    {
    pkg:'${orderInfo.pkgname}',
    propsId:'${orderInfo.itemid}',
    userID:'${orderInfo.userid}',
    propsType:'${orderInfo.propstype}',
    num:'${orderInfo.nums}',
    provider:'${orderInfo.merusername}',
    price:<fmt:formatNumber value="${orderInfo.amount}" pattern="0.##"/>,
    priceType:'${orderInfo.buytype}',
    <c:if test="${not empty orderInfo.orderstatus}">payStatus:'${orderInfo.orderstatus}',</c:if>
    orderId:'${orderInfo.id}',
    orderNo:'${orderInfo.orderno}',
    <c:if test="${not empty orderInfo.createdate}">orderTime:'<fmt:formatDate value="${orderInfo.createdate}" pattern="yyyy-MM-dd HH:mm:ss"/>'</c:if>
    },
    backUrl:'${backUrl}'
    
}
</c:set>
<c:set var="jsonValue">${sti:filterBlankForJsonData(jsonString)}</c:set>
${jsonValue}
<%
logger.info((String)pageContext.getAttribute("jsonValue"));
%>