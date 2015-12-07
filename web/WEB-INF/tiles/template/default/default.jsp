<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<html>
<head>
	<meta charset="UTF-8">
	<title>三维EC事业部ESCM管理系统</title>
	<link rel="stylesheet" type="text/css" href="/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="/themes/icon.css">
	<script type="text/javascript" src="/js/jquery.min.js"></script>
	<script type="text/javascript" src="/js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="/js/easyui-lang-zh_CN.js"></script>
</head>
<body>
	<body class="easyui-layout">
	<div data-options="region:'north',border:false" style="height:60px;padding:10px">
		<tiles:insertAttribute name="menu" />
	</div>
	<!-- <div data-options="region:'west',split:true,title:'West'" style="width:150px;padding:10px;">west content</div> -->
	<!-- <div data-options="region:'east',split:true,collapsed:true,title:'East'" style="width:100px;padding:10px;">east region</div> -->
	<div data-options="region:'south',border:false" style="height:50px;padding:10px;">south region</div>
	<div data-options="region:'center',title:'<tiles:getAsString name="title"/>'">
		<tiles:insertAttribute name="body" />
	</div>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</body>
</html>