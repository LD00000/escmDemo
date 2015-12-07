<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script>
	function submitMenuForm(action) {
		var $menuForm = $("#menuForm");
		
		$menuForm.attr("action", action)
		$menuForm.submit();
	}
</script>
<form id="menuForm" action="" method="post">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>
<div class="easyui-panel" style="padding: 5px;">
	<a href="#" class="easyui-menubutton" data-options="menu:'#sql'">SQL管理</a>
	<a href="#" class="easyui-menubutton" data-options="menu:'#publish'">版本发布</a>
<sec:authorize access="hasRole('ROLE_ADMIN')">
	<a href="#" class="easyui-menubutton" data-options="menu:'#project'">项目管理</a>
</sec:authorize>
	<a href="javascript:void(0)" class="easyui-menubutton" data-options="plain:true" onclick="submitMenuForm('/logout.htm')">注销</a>
</div>
<div id="sql" style="width: 100px;">
	<sec:authorize access="hasAnyRole('ROLE_DEV','ROLE_ADMIN')">
	<div><a href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="submitMenuForm('/secure/sql/main.htm')">管理</a></div>
	</sec:authorize>
	<sec:authorize access="hasRole('ROLE_ADMIN')">
	<div><a href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="submitMenuForm('/secure/sql/authorization.htm')">审核</a></div>
	</sec:authorize>
	<div><a href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="submitMenuForm('/secure/sql/search.htm')">查询</a></div>
</div>
<div id="publish" style="width:200px;">
	<sec:authorize access="hasRole('ROLE_ADMIN')">
	<div>录入SVN Revision Number</div>
	</sec:authorize>
	<div>版本发布</div>
	<div>查询</div>
</div>
<sec:authorize access="hasRole('ROLE_ADMIN')">
<div id="project" style="width:100px;">
	<div><a href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="submitMenuForm('/secure/project/main.htm')">管理</a></div>
</div>
</sec:authorize>