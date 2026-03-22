<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fnx" uri="http://java.sun.com/jsp/jstl/functionsx" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="f" uri="http://www.jspxcms.com/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/views/head.jsp" />
    <script>
        function confirmDelete() {
            return confirm("<s:message code='confirmDelete'/>");
        }

        function optSingle(opt) {
            if (Cms.checkeds("ids") == 0) {
                alert("<s:message code='pleaseSelectRecord'/>");
                return false;
            }
            if (Cms.checkeds("ids") > 1) {
                alert("<s:message code='pleaseSelectOne'/>");
                return false;
            }
            var id = $("input[name='ids']:checkbox:checked").val();
            location.href = $(opt + id).attr("href");
        }

        function optMulti(form, action, msg) {
            if (Cms.checkeds("ids") == 0) {
                alert("<s:message code='pleaseSelectRecord'/>");
                return false;
            }
            if (msg && !confirm(msg)) {
                return false;
            }
            form.action = action;
            form.submit();
            return true;
        }

        function optDelete(form) {
            if (Cms.checkeds("ids") == 0) {
                alert("<s:message code='pleaseSelectRecord'/>");
                return false;
            }
            if (!confirmDelete()) {
                return false;
            }
            form.action = 'delete.do';
            form.submit();
            return true;
        }
    </script>
</head>
<body class="skin-blue content-body">
<jsp:include page="/WEB-INF/views/commons/show_message.jsp" />
<div class="content-header">
    <h1><s:message code="visitLog.trafficAnalysis" /></h1>
</div>
<div class="content">
    <div class="box box-primary">
        <div class="box-body table-responsive">
            <form class="form-inline ls-search" action="traffic_analysis.do" method="get">
                <div id="radio" class="form-group">
                    <input type="radio" id="radioToday" onclick="location.href='traffic_analysis.do?period=today';"<c:if test="${period eq 'today'}"> checked="checked"</c:if> /><label for="radioToday"><s:message code="visitLog.trafficAnalysis.today" /></label>
                    <input type="radio" id="radioLast30Day" onclick="location.href='traffic_analysis.do?period=last30Day';"<c:if test="${empty period || period eq 'last30Day'}"> checked="checked"</c:if> /><label for="radioLast30Day"><s:message code="visitLog.trafficAnalysis.last30Day" /></label>
                    <input type="radio" id="radioLast1Year" onclick="location.href='traffic_analysis.do?period=last1Year';"<c:if test="${period eq 'last1Year'}"> checked="checked"</c:if> /><label for="radioLast1Year"><s:message code="visitLog.trafficAnalysis.last1Year" /></label>
                    <input type="radio" id="radioLast3Year" onclick="location.href='traffic_analysis.do?period=last3Year';"<c:if test="${period eq 'last3Year'}"> checked="checked"</c:if> /><label for="radioLast3Year"><s:message code="visitLog.trafficAnalysis.last3Year" /></label>
                    <input type="radio" id="radioAll" onclick="location.href='traffic_analysis.do?period=all';"<c:if test="${period eq 'all'}"> checked="checked"</c:if> /><label for="radioAll"><s:message code="visitLog.trafficAnalysis.all" /></label>
                </div>
                <script>$("#radio").buttonset();</script>
            </form>
            <div id="chart" style="margin-top:20px;padding-right:15px;height:300px;"></div>
            <script>
                var chart = echarts.init(document.getElementById('chart'));
                var option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['<s:message code="visitLog.pv"/>', '<s:message code="visitLog.uv"/>', '<s:message code="visitLog.ip"/>']
                    },
                    grid: {
                        left: '2%',
                        right: '3%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: [
                            <c:forEach var="bean" varStatus="status" items="${list}">
                            <c:choose>
                            <c:when test="${groupBy=='hour'}">'<fmt:formatDate value="${bean.date}" pattern="HH"/>H'</c:when>
                            <c:when test="${groupBy=='month'}">'<fmt:formatDate value="${bean.date}" pattern="yyyy-MM"/>'</c:when>
                            <c:otherwise>'<fmt:formatDate value="${bean.date}" pattern="MM-dd"/>'</c:otherwise>
                            </c:choose>
                            <c:if test="${!status.last}">, </c:if>
                            </c:forEach>
                        ]
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name: '<s:message code="visitLog.pv"/>',
                            type: 'line',
                            data: [
                                <c:forEach var="bean" varStatus="status" items="${list}">
                                <c:out value="${bean.views}"/><c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            ]
                        },
                        {
                            name: '<s:message code="visitLog.uv"/>',
                            type: 'line',
                            data: [
                                <c:forEach var="bean" varStatus="status" items="${list}">
                                <c:out value="${bean.uniqueViews}"/><c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            ]
                        },
                        {
                            name: '<s:message code="visitLog.ip"/>',
                            type: 'line',
                            data: [
                                <c:forEach var="bean" varStatus="status" items="${list}">
                                <c:out value="${bean.ipViews}"/><c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            ]
                        }
                    ]
                };
                chart.setOption(option);
            </script>
            <div id="minuteChart" style="margin-top:20px;padding-right:15px;height:300px;"></div>
            <script type="text/javascript">
                var minuteChart = echarts.init(document.getElementById('minuteChart'));
                var option = {
                    title: {
                        text: '<s:message code="visitLog.trafficAnalysis.last30Minute"/>'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: ['<s:message code="visitLog.pv"/>', '<s:message code="visitLog.uv"/>', '<s:message code="visitLog.ip"/>']
                    },
                    grid: {
                        left: '2%',
                        right: '3%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: [
                            <c:forEach var="bean" varStatus="status" items="${minuteList}">
                            '<fmt:formatDate value="${bean.date}" pattern="HH:mm"/>'<c:if test="${!status.last}">, </c:if>
                            </c:forEach>
                        ]
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name: '<s:message code="visitLog.pv"/>',
                            type: 'line',
                            data: [
                                <c:forEach var="bean" varStatus="status" items="${minuteList}">
                                <c:out value="${bean.views}"/><c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            ]
                        },
                        {
                            name: '<s:message code="visitLog.uv"/>',
                            type: 'line',
                            data: [
                                <c:forEach var="bean" varStatus="status" items="${minuteList}">
                                <c:out value="${bean.uniqueViews}"/><c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            ]
                        },
                        {
                            name: '<s:message code="visitLog.ip"/>',
                            type: 'line',
                            data: [
                                <c:forEach var="bean" varStatus="status" items="${minuteList}">
                                <c:out value="${bean.ipViews}"/><c:if test="${!status.last}">, </c:if>
                                </c:forEach>
                            ]
                        }
                    ]
                };
                minuteChart.setOption(option);
            </script>
        </div>
    </div>
</div>
</body>
</html>