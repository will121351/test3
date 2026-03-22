<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fnx" uri="http://java.sun.com/jsp/jstl/functionsx"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="f" uri="http://www.jspxcms.com/tags/form"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/WEB-INF/views/head.jsp"/>
<script>
$(function() {
    $("#validForm").validate();
	$("#sortHead").headSort();
	<shiro:hasPermission name="ext:visit_stat:edit">
	$("#pagedTable tbody tr").dblclick(function(eventObj) {
		var nodeName = eventObj.target.nodeName.toLowerCase();
		if(nodeName!="input"&&nodeName!="select"&&nodeName!="textarea") {
			location.href=$("#edit_opt_"+$(this).attr("beanid")).attr('href');
		}
	});
	</shiro:hasPermission>
});
function confirmDelete() {
	return confirm("<s:message code='confirmDelete'/>");
}
function optSingle(opt) {
	if(Cms.checkeds("ids")===0) {
		alert("<s:message code='pleaseSelectRecord'/>");
		return false;
	}
	if(Cms.checkeds("ids")>1) {
		alert("<s:message code='pleaseSelectOne'/>");
		return false;
	}
	var id = $("input[name='ids']:checkbox:checked").val();
	location.href=$(opt+id).attr("href");
}
function optMulti(form, action, msg) {
	if(Cms.checkeds("ids")===0) {
		alert("<s:message code='pleaseSelectRecord'/>");
		return false;
	}
	if(msg && !confirm(msg)) {
		return false;
	}
	form.action=action;
	form.submit();
	return true;
}
function optDelete(form) {
	if(Cms.checkeds("ids")===0) {
		alert("<s:message code='pleaseSelectRecord'/>");
		return false;
	}
	if(!confirmDelete()) {
		return false;
	}
	form.action='delete.do';
	form.submit();
	return true;
}
</script>
</head>
<body class="skin-blue content-body">
<jsp:include page="/WEB-INF/views/commons/show_message.jsp"/>
<div class="content-header">
    <h1><s:message code="visitStat.management"/> - <s:message code="list"/> <small>(<s:message code="totalElements" arguments="${pagedList.totalElements}"/>)</small></h1>
</div>
<div class="content">
    <div class="box box-primary">
        <div class="box-body table-responsive">
            <form class="form-inline ls-search" action="list.do" method="get">
                <div class="form-group">
                    <label><s:message code="beginTime"/></label>
                    <f:text class="form-control input-sm" name="search_GTE_time_Date" value="${search_GTE_time_Date[0]}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:120px;"/>
                </div>
                <div class="form-group">
                    <label><s:message code="endTime"/></label>
                    <f:text class="form-control input-sm" name="search_LTE_time_Date" value="${search_LTE_time_Date[0]}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" style="width:120px;"/>
                </div>
                <button class="btn btn-default btn-sm" type="submit"><s:message code="search"/></button>
            </form>
            <form method="post">
                <tags:search_params/>
                <div class="btn-toolbar ls-btn-bar">
                    <div class="btn-group">
                        <shiro:hasPermission name="ext:visit_log:view">
                            <button class="btn btn-default" type="button" onclick="return optSingle('#view_opt_');"><s:message code="view"/></button>
                        </shiro:hasPermission>
                    </div>
                    <div class="btn-group">
                        <shiro:hasPermission name="ext:visit_log:delete">
                            <button class="btn btn-default" type="button" onclick="return optDelete(this.form);"><s:message code="delete"/></button>
                        </shiro:hasPermission>
                    </div>
                </div>
                <table id="pagedTable" class="table table-condensed table-bordered table-hover ls-tb">
                    <thead id="sortHead" pagesort="<c:out value='${page_sort[0]}' />" pagedir="${page_sort_dir[0]}" pageurl="list.do?page_sort={0}&page_sort_dir={1}&${searchstringnosort}">
                    <tr class="ls_table_th">
                        <th width="25"><input type="checkbox" onclick="Cms.check('ids',this.checked);"/></th>
                        <th width="90"><s:message code="operate"/></th>
                        <th width="30" class="ls-th-sort"><span class="ls-sort" pagesort="id">ID</span></th>
                        <th width="160" class="ls-th-sort"><span class="ls-sort" pagesort="date"><s:message code="visitStat.date"/></span></th>
                        <th class="ls-th-sort"><span class="ls-sort" pagesort="type"><s:message code="visitStat.type"/></span></th>
                        <th class="ls-th-sort"><span class="ls-sort" pagesort="name"><s:message code="visitStat.name"/></span></th>
                        <th class="ls-th-sort"><span class="ls-sort" pagesort="views"><s:message code="visitStat.views"/></span></th>
                        <th class="ls-th-sort"><span class="ls-sort" pagesort="uniqueViews"><s:message code="visitStat.uniqueViews"/></span></th>
                        <th class="ls-th-sort"><span class="ls-sort" pagesort="ipViews"><s:message code="visitStat.ipViews"/></span></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="bean" varStatus="status" items="${pagedList.content}">
                        <tr beanid="${bean.id}">
                            <td><input type="checkbox" name="ids" value="${bean.id}"/></td>
                            <td align="center">
                                <shiro:hasPermission name="ext:visit_log:view">
                                    <a id="view_opt_${bean.id}" href="view.do?id=${bean.id}&position=${pagedList.number*pagedList.size+status.index}&${searchstring}" class="ls-opt"><s:message code="view"/></a>
                                </shiro:hasPermission>
                                <shiro:hasPermission name="ext:visit_log:delete">
                                    <a href="delete.do?ids=${bean.id}&${searchstring}" onclick="return confirmDelete();" class="ls-opt"><s:message code="delete"/></a>
                                </shiro:hasPermission>
                            </td>
                            <td><c:out value="${bean.id}"/></td>
                            <td align="center"><fmt:formatDate value="${bean.date}" pattern="yyyy-MM-dd"/></td>
                            <td><c:out value="${bean.type}"/></td>
                            <td><c:out value="${bean.name}"/></td>
                            <td><c:out value="${bean.views}"/></td>
                            <td><c:out value="${bean.uniqueViews}"/></td>
                            <td><c:out value="${bean.ipViews}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <c:if test="${fn:length(pagedList.content) le 0}">
                    <div class="ls-norecord"><s:message code="recordNotFound"/></div>
                </c:if>
            </form>
            <form action="list.do" method="get" class="ls-page">
                <tags:search_params excludePage="true"/>
                <tags:pagination pagedList="${pagedList}"/>
            </form>
        </div>
    </div>
</div>
</body>
</html>