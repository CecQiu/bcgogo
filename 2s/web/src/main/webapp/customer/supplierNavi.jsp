<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<div class="mainTitles">
    <div class="cusTitle">
        <c:choose>
            <c:when test="${currPage==\"searchSupplier\"}">供应商列表</c:when>
            <c:otherwise>推荐供应商</c:otherwise>
        </c:choose>
    </div>
</div>