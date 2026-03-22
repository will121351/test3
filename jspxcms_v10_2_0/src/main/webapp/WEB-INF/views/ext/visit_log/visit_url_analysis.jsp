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
        $(function () {
            $("#sortHead").headSort();
        });

        function confirmDelete() {
            return confirm("<s:message code='confirmDelete'/>");
        }

        function optSingle(opt) {
            if (Cms.checkeds("ids") === 0) {
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
            if (Cms.checkeds("ids") === 0) {
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
            if (Cms.checkeds("ids") === 0) {
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
    <h1><s:message code="visitLog.urlAnalysis" />
        <small>(<s:message code="totalElements" arguments="${fn:length(list)}" />)</small>
    </h1>
</div>
<div class="content">
    <div class="box box-primary">
        <div class="box-body table-responsive">
            <form class="form-inline ls-search" action="url_analysis.do" method="get">
                <div id="radio" class="form-group">
                    <input type="radio" id="radioLast30Day" onclick="location.href='url_analysis.do?period=last30Day';"<c:if test="${empty period || period eq 'last30Day'}"> checked="checked"</c:if>>
                    <label for="radioLast30Day"><s:message code="visitLog.trafficAnalysis.last30Day" /></label>
                    <input type="radio" id="radioLast1Year" onclick="location.href='url_analysis.do?period=last1Year';"<c:if test="${period eq 'last1Year'}"> checked="checked"</c:if>>
                    <label for="radioLast1Year"><s:message code="visitLog.trafficAnalysis.last1Year" /></label>
                    <input type="radio" id="radioLast3Year" onclick="location.href='url_analysis.do?period=last3Year';"<c:if test="${period eq 'last3Year'}"> checked="checked"</c:if>>
                    <label for="radioLast3Year"><s:message code="visitLog.trafficAnalysis.last3Year" /></label>
                </div>
                <script>$("#radio").buttonset();</script>
            </form>
            <form action="url_analysis.do" method="post">
                <table id="pagedTable" class="table table-condensed table-bordered table-hover ls-tb">
                    <thead>
                    <tr class="ls_table_th">
                        <th width="30">#</th>
                        <th><s:message code="visitLog.url" /></th>
                        <th><s:message code="visitLog.pv" /></th>
                        <th><s:message code="visitLog.uv" /></th>
                        <th><s:message code="visitLog.ip" /></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="bean" varStatus="status" items="${pagedList.content}">
                        <tr>
                            <td>${status.count}</td>
                            <td><a href="<c:out value='${bean.name}'/>" target="_blank" style="color:blue;"><c:out value="${bean.name}" /></a></td>
                            <td align="right"><c:out value="${bean.views}" /></td>
                            <td align="right"><c:out value="${bean.uniqueViews}" /></td>
                            <td align="right"><c:out value="${bean.ipViews}" /></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <c:if test="${fn:length(pagedList.content) le 0}">
                    <div class="ls-norecord"><s:message code="recordNotFound" /></div>
                </c:if>
            </form>
            <form action="url_analysis.do" method="get" class="ls-page">
                <input type="hidden" name="period" value="<c:out value='${period}'/>" />
                <tags:pagination pagedList="${pagedList}" />
            </form>
        </div>
    </div>
</div>
</body>
</html>