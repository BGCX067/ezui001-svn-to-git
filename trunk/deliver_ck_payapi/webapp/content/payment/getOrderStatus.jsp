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
        pkg: '${orderInfo.app.pkg}',
        version: ${orderInfo.app.version},
        title: '${sti:escapeJavaScript(orderInfo.app.title)}',
        icon: '${orderInfo.app.icon}',
        provider: '<fmt:message key='provider.title' bundle='${applicationBundle}' ><fmt:param >${sti:escapeJavaScript(orderInfo.app.provider)}</fmt:param></fmt:message>',
        price: <fmt:formatNumber value="${orderInfo.payInfo.amount}" pattern="0.##"/>,
        priceType: ${orderInfo.payInfo.myPriceType},
        priceText: '<fmt:message key='priceText.${orderInfo.payInfo.myPriceType}' bundle='${applicationBundle}' ><fmt:param ><fmt:formatNumber value="${orderInfo.payInfo.amount}" pattern="0.##"/></fmt:param></fmt:message>',
        <c:if test="${not empty orderInfo.payInfo.status}">
        payStatus: ${orderInfo.payInfo.status},
        </c:if>
        <c:if test="${not empty orderInfo.payInfo.payTime}">
        orderTime: '<fmt:formatDate value="${orderInfo.payInfo.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/>',
        </c:if>
        <c:if test="${not empty orderInfo.payInfo.rightStartDate}">
        rightStartDate: '<fmt:formatDate value="${orderInfo.payInfo.rightStartDate}" pattern="yyyy-MM-dd"/>',
        </c:if>
        <c:if test="${not empty orderInfo.payInfo.rightEndDate}">
        rightEndDate: '<fmt:formatDate value="${orderInfo.payInfo.rightEndDate}" pattern="yyyy-MM-dd"/>',
        </c:if>
        payType: ${orderInfo.payInfo.paymentType},
        orderId: ${orderInfo.payInfo.id},
        orderNo: '${orderInfo.payInfo.orderNo}'
    }
}
</c:set>
<c:set var="jsonValue">${sti:filterBlankForJsonData(jsonString)}</c:set>
${jsonValue}
<%
logger.info((String)pageContext.getAttribute("jsonValue"));
%>