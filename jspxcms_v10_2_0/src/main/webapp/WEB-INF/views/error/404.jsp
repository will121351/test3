<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%response.setStatus(404);%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="renderer" content="webkit">
<title>404-页面不存在</title>
<style type="text/css">
html,body,h1,h2,h3,h4,h5,h6,hr,p,blockquote,dl,dt,dd,ul,ol,li,pre,form,fieldset,legend,th,td,img,div,table{margin:0;padding:0;}
html,body{font:12px/1.5 tahoma,arial,\5b8b\4f53;color:#2d374b;}
h1,h2,h3,h4,h5,h6{font-size:100%;}
ul,ol{list-style:none;}
fieldset,img{border:0;}

strong{font-weight:bold;}
.type{padding:0 10px 0;color:#5b4f5b;}
.code{font-size:48px;}
.title{font-size:32px;}
.messages{padding:10px 10px 0;font-size:16px;}
.message{line-height:30px;}
</style>
</head>
<body>
<h1 class="type"><strong class="code">404</strong> <strong class="title">您访问的页面不存在</strong></h1>
<div class="messages">
	<%-- <p class="message">URL: ${requestScope["javax.servlet.forward.request_uri"]}</p> --%>
	<c:if test="${!empty requestScope['javax.servlet.error.message']}"><p class="message">信息：${requestScope["javax.servlet.error.message"]}</p></c:if>
</div>
<!-- 1111111111111111111111111111111111111111111111111111111111111111111111 -->
<!-- 1111111111111111111111111111111111111111111111111111111111111111111111 -->
<!-- 1111111111111111111111111111111111111111111111111111111111111111111111 -->
<!-- 1111111111111111111111111111111111111111111111111111111111111111111111 -->
<!-- 1111111111111111111111111111111111111111111111111111111111111111111111 -->
</body>
</html>