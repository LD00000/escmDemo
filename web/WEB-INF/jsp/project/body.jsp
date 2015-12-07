<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table id="dataGrid" class="easyui-datagrid" style="width: auto; height: auto">
	<thead>
		<tr>
			<th field="id" >项目简称</th>
			<th field="name">项目名称</th>
			<th field="createUser">制单人编码</th>
			<th field="createName">制单人名称</th>
			<th field="createDate" formatter="dateFormatter">制单日期</th>
		</tr>
	</thead>
</table>
<div id="toolbar">
	<a href="javascript:void(0)" class="easyui-linkbutton"
		iconCls="icon-add" plain="true" onclick="newProject()">新增</a>
	<a href="javascript:void(0)" class="easyui-linkbutton"
		iconCls="icon-edit" plain="true" onclick="editProject()">编辑</a>
	<a href="javascript:void(0)" class="easyui-linkbutton"
		iconCls="icon-remove" plain="true" onclick="destroyProject()">删除</a>
</div>

<div id="projectDialog" class="easyui-dialog"
	style="width: 400px; height: 280px; padding: 10px 20px" closed="true"
	buttons="#projectDialog-buttons">
	<div class="ftitle">项目信息</div>
	<form id="projectForm" method="post">
		<div class="fitem">
			<label>项目简称:</label> <input name="id" class="easyui-textbox"
				required="true">
		</div>
		<div class="fitem">
			<label>项目名称:</label> <input name="name" class="easyui-textbox"
				required="true">
		</div>
		<div class="fitem">
			<label>是否启用</label> <select class="easyui-combobox" name="enable">
				<option value="1">是</option>
				<option value="0">否</option>
			</select>
		</div>
	</form>
</div>
<div id="projectDialog-buttons">
	<a href="javascript:void(0)" class="easyui-linkbutton c6"
		iconCls="icon-ok" onclick="saveProject()" style="width: 90px">Save</a>
	<a href="javascript:void(0)" class="easyui-linkbutton"
		iconCls="icon-cancel"
		onclick="javascript:$('#projectDialog').dialog('close')"
		style="width: 90px">Cancel</a>
</div>
<script type="text/javascript">
	$(function() {
		$("#dataGrid").datagrid({
			pageSize : 50, 
			pageList : [ 10, 20, 50, 100 ],
			nowrap : false, // 当数据长度超出列宽时是否自动截取
			toolbar : "#toolbar", // 在添加 增添、删除、修改操作的按钮要用到这个
			url : "/secure/project/main.htm?json=1&${_csrf.parameterName}=${_csrf.token}",
			loadMsg : "数据装载中......",
			singleSelect : true,// 为true时只能选择单行
			rownumbers : true, // 显示行数
			fitColumns : true, // 允许表格自动缩放，以适应父容器
			pagination : true, // 分页
			loadFilter : function(data) {
				return {total : data.total,
						rows : data.rows}
			}
		})
	})
	
	function dateFormatter(value,row,index){ // 日期格式化
		var unixTimestamp = new Date(value.time);
		return unixTimestamp.toLocaleString();
	}
	
	var url;
	function newProject() {
		$('#projectDialog').dialog('open').dialog('center').dialog('setTitle',
				'新增项目');
		$('#projectForm').form('clear');
		url = '/secure/project/insert.htm';
	}
	function editProject() {
		var row = $('#dataGrid').datagrid('getSelected');
		if (row) {
			$('#projectDialog').dialog('open').dialog('center').dialog(
					'setTitle', '编辑项目');
			$('#projectForm').form('load', row);
			url = '/secure/project/update.htm';
		} else {
			$.messager.alert("项目",'请选择要编辑的项目!', 'warning');
		}
	}
	function saveProject() {
		$('#projectForm').form('submit', {
			url : url,
			onSubmit : function(param) {
				param.${_csrf.parameterName} = '${_csrf.token}';
				param.json = 1;
			},
			success : function(data) {
				setTimeout(function() {
					$('#projectDialog').dialog('close'); // close the dialog
					$('#dataGrid').datagrid('reload'); // reload the user data
				}, 100);
			}
		});
	}
	function destroyProject() {
		var row = $('#dataGrid').datagrid('getSelected');
		if (row) {
			$.messager.confirm('Confirm', '请确认是否删除该项目?', function(r) {
				if (r) {
					$.post(
						'/secure/project/delete.htm?json=1&${_csrf.parameterName}=${_csrf.token}',
						{
							id : row.id
						}, 
						function(result) {
							if (result.success) {
								$('#dataGrid').datagrid('reload'); // reload the user data
							} else {
								$.messager.alert("项目",'删除失败:' + result.message, 'error');
							}
						}, 
						'json')
					.error(function(e) {
						$.messager.alert("项目",'删除失败:' + e, 'error');
					})
				}
			});
		} else {
			$.messager.alert("项目",'请选择要删除的项目!', 'warning');
		}
	}
</script>
<style type="text/css">
#projectForm {
	margin: 0;
	padding: 10px 30px;
}

.ftitle {
	font-size: 14px;
	font-weight: bold;
	padding: 5px 0;
	margin-bottom: 10px;
	border-bottom: 1px solid #ccc;
}

.fitem {
	margin-bottom: 5px;
}

.fitem label {
	display: inline-block;
	width: 80px;
}

.fitem input {
	width: 160px;
}
</style>