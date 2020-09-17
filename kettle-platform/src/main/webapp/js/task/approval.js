//展示信息
function showApprovalPanel(secondGuidePanel) {
	
	//为表格添加一行复选框用于选择行
	var sm = new Ext.grid.CheckboxSelectionModel();
	//列模型
	var cm = new Ext.grid.ColumnModel([
		new Ext.grid.RowNumberer(),
		sm,
		{
			header : "组件ID",
			dataIndex : "userId",
			align : "center",
			hidden : true
		},
		{
			header : "任务名称",
			dataIndex : "name",
			align : "center"
		},
		{
			header : "源数据源名称",
			dataIndex : "source_data_name",
			align : "center"
		},
		{
			header : "源表",
			dataIndex : "source_table_name",
			align : "center"
		},
		{
			header : "目标数据源名称",
			dataIndex : "target_data_name",
			align : "center"
		},
		{
			header : "目标表",
			dataIndex : "target_table_name",
			align : "center"
		},
		{
			header : "任务描述",
			dataIndex : "desc",
			align : "center"
		},
		{
			header : "申请人",
			dataIndex : "create_user_name",
			align : "center"
		},
		{
			header : "申请时间",
			dataIndex : "application_date",
			align : "center",
			renderer : function(v) {
				if (v == null || v == '') {
					v = '-';
				}
				return v
			}
		},
		{
			header : "审批人",
			dataIndex : "approval_user_name",
			align : "center",
			renderer : function(v) {
				if (v == null || v == '') {
					v = '-';
				}
				return v
			}
		},
		{
			header : "审批时间",
			dataIndex : "approval_date",
			align : "center",
			renderer : function(v) {
				if (v == null || v == '') {
					v = '-';
				}
				return v
			}
		},
		{
			header : "状态",
			dataIndex : "status_name",
			align : "center"
		},
		{
			header : "操作",
			width : 280,
			dataIndex : "",
			menuDisabled : true,
			align : "center",
			renderer : function(value, metadata, record, rowIndex, colIndex, store) {
				var id = record.data.id;
				var status = record.data.status;
				if (status == '1') {
					return "<img src='../../ui/images/i_delete.png' class='imgCls' onclick='approvalDataPush(\"" + id + "\", \"3\")' title='驳回'/>&nbsp;&nbsp;" +
						"<img src='../../ui/images/i_ok.png' class='imgCls' onclick='approvalDataPush(\"" + id + "\", \"2\")' title='审批'/>&nbsp;&nbsp;";
				} else {
					return "-";
				}
			}
		}
	]);

	//Record定义记录结果
	var human = Ext.data.Record.create([
		{
			name : "id",
			type : "string",
			mapping : "ID"
		},
		{
			name : "name",
			type : "string",
			mapping : "NAME"
		},
		{
			name : "source_data_name",
			type : "string",
			mapping : "SOURCE_DATA_NAME"
		},
		{
			name : "source_table_name",
			type : "string",
			mapping : "SOURCE_TABLE_NAME"
		},
		{
			name : "target_data_name",
			type : "string",
			mapping : "TARGET_DATA_NAME"
		},
		{
			name : "target_table_name",
			type : "string",
			mapping : "TARGET_TABLE_NAME"
		},
		{
			name : "status",
			type : "string",
			mapping : "STATUS"
		},,
		{
			name : "status_name",
			type : "string",
			mapping : "STATUS_NAME"
		},
		{
			name : "desc",
			type : "string",
			mapping : "DESCRIPTION"
		},
		{
			name : "create_user_name",
			type : "string",
			mapping : "CREATE_USER_NAME"
		},
		{
			name : "create_date",
			type : "string",
			mapping : "CREATE_DATE"
		},
		{
			name : "application_date",
			type : "string",
			mapping : "APPLICATION_DATE"
		},
		{
			name : "approval_user_name",
			type : "string",
			mapping : "APPROVAL_USER_NAME"
		},
		{
			name : "approval_date",
			type : "string",
			mapping : "APPROVAL_DATE"
		}
	]);

	var proxy = new Ext.data.HttpProxy({
		url : "/dataPush/getApprovalList.do"
	});

	var reader = new Ext.data.JsonReader({
		totalProperty : "totalProperty",
		root : "root"
	}, human);

	var store = new Ext.data.Store({
		proxy : proxy,
		reader : reader,
		listeners : {
			"beforeload" : function(store) {
				var inputNameTo = "";
				var typeTo = "";
				var taskUserId = "";
				if (Ext.getCmp("taskNameField"))
					inputNameTo = Ext.getCmp("taskNameField").getValue();
				if (Ext.getCmp("approvalTaskStatus"))
					typeTo = Ext.getCmp("approvalTaskStatus").getValue();
				if (Ext.getCmp("taskUserCombox"))
					taskUserId = Ext.getCmp("taskUserCombox").getValue();

				store.baseParams = {
					status : typeTo,
					name : inputNameTo,
					userId : taskUserId
				}
			}
		}
	})

	store.load({
		params : {
			start : 0,
			limit : 15
		}
	});

	var inputTaskName = "";
	if (Ext.getCmp("taskNameField"))
		inputTaskName = Ext.getCmp("taskNameField").getValue();
	//搜索框
	var taskNameField = new Ext.form.TextField({
		id : "taskNameField",
		name : "taskNameField",
		fieldLabel : "任务名称",
		width : 150,
		value : '',
		emptyText : "请输入任务名称..",
		value : inputTaskName,
	});

	var status = "";
	if (Ext.getCmp("approvalTaskStatus"))
		status = Ext.getCmp("approvalTaskStatus").getValue();
	var statusCombobox = approvalStatusCombobox(status);

	var selectUser = "";
	if (Ext.getCmp("taskUserCombox"))
		selectUser = Ext.getCmp("taskUserCombox").getValue();
	var taskUserComb = taskUserCombobox(selectUser);

	var grid = new Ext.grid.GridPanel({
		id : "approvalPanel",
		title : "<font size = '3px' >资源审批管理</font>",
		height : 470,
		cm : cm, //列模型
		sm : sm,
		store : store,
		viewConfig : {
			forceFit : true //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度
		},
		closable : true,
		tbar : new Ext.Toolbar({
			buttons : [
				statusCombobox,
				taskUserComb,
				taskNameField, "-",
				{
					iconCls : "searchCls",
					tooltip : '搜索',
					handler : function() {
						showApprovalPanel(secondGuidePanel);
					}
				}
			]
		}),
		bbar : new Ext.PagingToolbar({
			cls : "bgColorCls",
			store : store,
			pageSize : 15,
			displayInfo : true
		})
	});

	secondGuidePanel.removeAll(true);
	secondGuidePanel.add(grid);
	secondGuidePanel.doLayout();
}

/**
 * 
 */
function approvalDataPush(id, status) {
	Ext.Msg.confirm('提示信息', '确认要提交这条信息吗？', function(op) {
		if (op == 'yes') {
			Ext.Ajax.request({
				url : "/dataPush/operateStatus.do",
				params : {
					id : id,
					status : status
				},
				success : function(response, config) {
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.show({
							title : '提示信息',
							msg : '提交成功！',
							buttons : Ext.Msg.OK,
							icon : Ext.Msg.INFO //注意此处为INFO  
						});
						var secondGuidePanel = Ext.getCmp("secondGuidePanel");
						showApprovalPanel(secondGuidePanel);
					} else {
						Ext.Msg.show({
							title : '提示信息',
							msg : result.msg,
							buttons : Ext.Msg.OK,
							icon : Ext.Msg.ERROR //注意此处为INFO  
						});
					}
				},
				failure : failureResponse
			})
		}
	})
}


//用户组选择下拉框
function taskUserCombobox(selectUser) {

	var taskUserCom = new Ext.form.ComboBox({
		id : "taskUserCombox",
		triggerAction : "all",
		store : Ext.StoreMgr.get('taskUserStore'),
		displayField : "name",
		valueField : "id",
		mode : "local",
		emptyText : "用户选择..",
		listeners : {
			'select' : function(combo, record, index) {
				var secondGuidePanel = Ext.getCmp("secondGuidePanel");
				showApprovalPanel(secondGuidePanel);
			}
		}
	})
	if (selectUser != undefined && selectUser != "") {
		taskUserCom.setValue(selectUser);
		taskUserCom.setRawValue(selectUser);
	}

	return taskUserCom;
}

/**
 * 下拉框
 */
function approvalStatusCombobox(type) {
	var statusChooseCom = new Ext.form.ComboBox({
		id : "approvalTaskStatus",
		triggerAction : "all",
		store : new Ext.data.JsonStore({
			fields : [ 'id', 'name' ],
			data : [ {
				id : '',
				name : '全部'
			}, {
				id : '1',
				name : '待审核'
			}, {
				id : '2',
				name : '审核通过'
			}, {
				id : '3',
				name : '驳回'
			}
			]
		}),
		displayField : 'name',
		valueField : 'id',
		mode : "local",
		autoLoad : true,
		emptyText : "请选择任务状态..",
		listeners : {
			'select' : function(combo, record, index) {
				var secondGuidePanel = Ext.getCmp("secondGuidePanel");
				showApprovalPanel(secondGuidePanel);
			}
		}
	})
	if (type != undefined && type != "") {
		statusChooseCom.setValue(type);
		statusChooseCom.setRawValue(type);
	}
	return statusChooseCom;
}