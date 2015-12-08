<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理—店面列表</title>
    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
  <script type="text/javascript" src="js/searchDefault.js"></script>
    <script type="text/javascript">
        $().ready(function () {
            $("#time").html( new Date().toLocaleString() + ' 星期' + '日一二三四五六'.charAt(new Date().getDay()) );
            setInterval( function(){
                $('#time').html( new Date().toLocaleString()+' 星期'+'日一二三四五六'.charAt(new Date().getDay()) );
            }, 1000);
        });
</script>
</head>

<body>
<div class="main">
    <!--头部-->
    <%@include file="/WEB-INF/views/header.jsp" %>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
       <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <div class="rightTitle">
                    <div class="rightLeft"></div>
                </div>
                <!--代理商-->
                <div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">多店面</div>
                    <div class="timeRight"></div>
                </div>
                <!--代理商结束-->
                <!--table-->
                <table cellpadding="0" cellspacing="0" id="" class="clear">
                    <col width="53">
                    <col width="150">
                    <col width="150">
                    <col width="200">
                    <col width="200">
                    <col width="95">
                    <thead>
                    <tr>
                        <th>NO</th>
                        <th>店铺名</th>
                        <th>店主</th>
                        <th>联系方式</th>
                        <th>地址</th>
                        <th style=" background-image:none;"></th>
                    </tr>
                    </thead>
                    <tbody>


                    <c:forEach var="shoplt" items="${shoplist}" varStatus="status">
                        <c:choose>
                            <c:when test="${(status.index + 1) % 2 != 0}">
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td>${shoplt.name}</td>
                                    <td>${shoplt.legalRep}</td>
                                    <td>${shoplt.mobile}</td>
                                    <td>${shoplt.address}</td>
                                    <td><a href="beshop.do?method=shopaudit&shopId=${shoplt.id}">点击详情</a></td>
                                </tr>
                            </c:when>
                            <c:when test="${(status.index + 1) % 2 == 0}">
                                <tr class="agent_bg">
                                    <td>${status.index + 1}</td>
                                    <td>${shoplt.name}</td>
                                    <td>${shoplt.legalRep}</td>
                                    <td>${shoplt.mobile}</td>
                                    <td>${shoplt.address}</td>
                                    <td><a href="beshop.do?method=shopaudit&shopId=${shoplt.id}">点击详情</a></td>
                                </tr>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                    </tbody>
                </table>
                <!--table结束-->
                <!--分页-->
                <div class="i_leftBtn">
                    <div class="fist_page"></div>
                    <div class="">1</div>
                    <div class="i_leftCountHover">2</div>
                    <div class="">3</div>
                    <div class="">4</div>
                    <div class="">5</div>
                    <div class="last_page"></div>
                </div>
            </div>
            <!--分页结束-->
            <!--内容结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
</body>
</html>