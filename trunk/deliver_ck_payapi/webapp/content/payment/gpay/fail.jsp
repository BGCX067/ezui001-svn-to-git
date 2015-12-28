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
    ret: 'fail',
}
</c:set>
<c:set var="jsonValue">${sti:filterBlankForJsonData(jsonString)}</c:set>
${jsonValue}
<%
logger.info((String)pageContext.getAttribute("jsonValue"));
%>