<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<title>EC部门ESCM管理系统</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/themes/icon.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/easyui-lang-zh_CN.js"></script>
<style type="text/css">
body {
	text-align: center
}

.div {
	margin: 0 auto;
	width: 400px;
	height: 100px;
}
</style>
</head>
<body style="text-align: center;">
	<div class="easyui-panel" title="LOG IN:"
		style="margin: 0 auto; width: 400px">
		<div style="padding: 10px 60px 20px 60px">
			<form id="loginForm" action="j_spring_security_check" method="post">
				<table cellpadding="5">
				<c:if test="${not empty fail}">
					<tr>${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</tr>
				</c:if>
				<c:if test="${not empty logout}">
					<tr>登出成功！</tr>
				</c:if>
					<tr>
						<td>用户名:</td>
						<td><input class="easyui-validatebox textbox" type="text" name="id" data-options="required:true"></input></td>
					</tr>
					<tr>
						<td>密码:</td>
						<td><input class="easyui-validatebox textbox" type="password" name="password" data-options="required:true"></input></td>
					</tr>
				</table>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</form>
			<div style="text-align: center; padding: 5px">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">登录</a>
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">清空</a>
			</div>
		</div>
	</div>
<style scoped="scoped">
.textbox {
	height: 20px;
	margin: 0;
	padding: 0 2px;
	box-sizing: content-box;
}
</style>
	<script>
		function submitForm() {
			$('#loginForm').submit();
		}
		function clearForm() {
			$('#loginForm').form('clear');
		}
	</script>
</body>
</html>
