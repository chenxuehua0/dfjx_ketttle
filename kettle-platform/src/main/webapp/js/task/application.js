//展示信息
function showApplicationPanel(secondGuidePanel) {
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
				if(v == null || v == ''){
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
				if(v == null || v == ''){
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
				if(v == null || v == ''){
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
			renderer : function(value , metadata , record ,rowIndex , colIndex , store ) {
				var id = record.data.id;
				var status = record.data.status;
				if (status== '0' || status== '3') {
					return "<img src='../../ui/images/i_delete.png' class='imgCls' onclick='deleteDataPush(\""+ id +"\")' title='删除'/>&nbsp;&nbsp;" +
						"<img src='../../ui/images/i_editor.png' class='imgCls' onclick='updateDataPush()' title='修改'/>&nbsp;&nbsp;" +
						"<img src='../../ui/images/i_ok.png' class='imgCls' onclick='submitDataPush(\""+ id +"\", \"1\")' title='提交'/>&nbsp;&nbsp;";
				} if (status== '1') {//撤回
					return "-";
				} else {
					return "-";
				}
			}
		}
	]);

	var proxy = new Ext.data.HttpProxy({
		url : "/dataPush/getList.do"
	});
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
				if(Ext.getCmp("taskName"))
					inputNameTo=Ext.getCmp("taskName").getValue();
			    if(Ext.getCmp("taskStatus"))
			    	typeTo=Ext.getCmp("taskStatus").getValue();

				store.baseParams = {
					status : typeTo,
					name : inputNameTo
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

	var inputName = "";
	if(Ext.getCmp("taskName"))
		inputName=Ext.getCmp("taskName").getValue();
	//搜索框
	var usernameField = new Ext.form.TextField({
		id : "taskName",
		name : "taskName",
		fieldLabel : "任务名称",
		width : 150,
		value : '',
		emptyText : "请输入任务名称..",
		value: inputName,
	});

	var type="";
    if(Ext.getCmp("taskStatus"))
    	type=Ext.getCmp("taskStatus").getValue();
	var userTypeCom = statusCombobox(type);

	
	var grid = new Ext.grid.GridPanel({
		id : "applicationPanel",
		title : "<font size = '3px' >资源申请管理</font>",
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
				userTypeCom,
				usernameField, "-",
				{
					iconCls : "searchCls",
					tooltip : '搜索',
					handler : function() {
						showApplicationPanel(secondGuidePanel);
					}
				},
				{
					iconCls : "addCls",
					tooltip : '新增',
					handler : function() {
						createApplication();
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

//修改
function updateDataPush() {
	var grid = Ext.getCmp("applicationPanel");
	var secondGuidePanel = Ext.getCmp("secondGuidePanel");
	//获取被选中行的数据
	var record = grid.getSelectionModel().getSelected();

	var id = record.get("id");
	var name = record.get("name");
	var desc = record.get("desc");
	
	var source_data_name = record.get("source_data_name");
	var source_table_name = record.get("source_table_name");
	var target_data_name = record.get("target_data_name");
	var target_table_name = record.get("target_table_name");
	var updateForm = generateApplicationField(name, desc, source_data_name, source_table_name, target_data_name, target_table_name);
	//生成修改窗口
	var updateWindow = new Ext.Window({
		title : "修改",
		modal : true,
		bodyStyle : "background-color:white",
		width : 400,
		height : 320,
		items : [
			updateForm
		],
		bbar : new Ext.Toolbar({
			buttons : [ '->',
				{
					text : "提交",
					handler : function() {
						//表单所有控件作为提交参数
						var idHidden = new Ext.form.Hidden({
							name:"id",
					        value:id
						});
						updateForm.items.add(idHidden);
						updateForm.doLayout();
						updateForm.baseParams = updateForm.getForm().getValues();
						if (updateForm.getForm().isValid()) {
							updateForm.getForm().submit({
								url: "/dataPush/update.do",
								success : function(form, action) {
									Ext.Msg.show({
										title : '提示信息',
										msg : action.result.msg,
										buttons : Ext.Msg.OK,
										icon : Ext.Msg.INFO //注意此处为INFO  
									});
									updateWindow.close();
									showApplicationPanel(secondGuidePanel);
								},
								failure: function(form, action){
									Ext.Msg.show({
										title : '提示信息',
										msg : action.result.msg,
										buttons : Ext.Msg.OK,
										icon : Ext.Msg.ERROR //注意此处为INFO  
									});
									
								}
							})
						} else {
							Ext.Msg.show({
								title : '提示信息',
								msg : '表单存在不规范填写,请再次确认后提交!',
								buttons : Ext.Msg.OK,
								icon : Ext.Msg.WARNING //注意此处为INFO  
							});
						}
					}
				}
			]
		})
	});
	
	updateWindow.show(grid);
}

//添加
function createApplication() {
	var grid = Ext.getCmp("applicationPanel");

	var f = generateApplicationField('', '', '', '', '', '');
	//用户类型选择窗口
	var applicationWindowForAdd = new Ext.Window({
		title : "申请信息填写",
		modal : true,
		bodyStyle : "background-color:white",
		width : 400,
		height : 320,
		items : [ f ],
		bbar : new Ext.Toolbar({
			buttons : [ '->', {
				text : "确认",
				handler : function() {
					if (f.getForm().isValid()) {
						//获取表单填写的信息
						Ext.Ajax.request({
							url : "/dataPush/add.do",
							params : {
								sourceData : f.getForm().findField("sourceDataCombobox").getValue(),
								sourceTable : f.getForm().findField("sourceTableCombobox").getValue(),
								targetData : f.getForm().findField("targetDataCombobox").getValue(),
								targetTable : f.getForm().findField("targetTableCombobox").getValue(),
								desc : f.getForm().findField("description").getValue(),
								name : f.getForm().findField("name").getValue()
							},
							success : function(response, config) {
								var result = Ext.decode(response.responseText);
								if (result.success) {
									applicationWindowForAdd.close();
									Ext.Msg.show({
										title : '提示信息',
										msg : '添加成功',
										buttons : Ext.Msg.OK,
										icon : Ext.Msg.INFO
									});
									var secondGuidePanel = Ext.getCmp("secondGuidePanel");
									showApplicationPanel(secondGuidePanel);
								} else {
									Ext.Msg.show({
										title : '提示信息',
										msg : result.msg,
										buttons : Ext.Msg.OK,
										icon : Ext.Msg.WARNING
									});
								}
							},
							failure : failureResponse
						})

					} else {
						Ext.MessageBox.alert("faile", "表单存在不规范填写!");
					}
				}
			}
			]
		})
	})

	applicationWindowForAdd.show(grid);
}


/*
 * 
 */
function generateApplicationField(name, desc, source_data_name, source_table_name, target_data_name, target_table_name) {
	var itemArray = new Array();
	//用户描述
	var nameInput = new Ext.form.TextField({
		id : "name",
		name : "name",
		fieldLabel : "任务名称",
		width : 280,
		allowBlank : false,
		value : name
	});

	itemArray.push(nameInput);

	itemArray.push(sourceDataSelect(source_data_name));
	itemArray.push(sourceTableSelect(source_table_name));
	itemArray.push(targetDataSelect(target_data_name));
	itemArray.push(targetTableSelect(target_table_name));

	//用户描述
	var descriptionInput = new Ext.form.TextArea({
		id : "description",
		name : "description",
		fieldLabel : "任务描述",
		width : 280,
		height : 100,
		value : desc
	});

	itemArray.push(descriptionInput);

	//表单
	var applicationForm = new Ext.form.FormPanel({
		width : 400,
		height : 350,
		frame : true,
		labelWidth : 80,
		labelAlign : "right",
		items : [
			{
				layout : "form", //从上往下布局
				items : itemArray
			}
		]
	});
	return applicationForm;
}

//下拉框
function sourceDataSelect(target_table_name) {
	var proxy = new Ext.data.HttpProxy({
		url : "/common/getDatabases.do"
	});

	var record = Ext.data.Record.create([ {
		name : "name",
		type : "String",
		mapping : "name"
	}
	]);

	var reader = new Ext.data.JsonReader({}, record);

	var store = new Ext.data.Store({
		proxy : proxy,
		reader : reader
	});

	var sourceCombobox = new Ext.form.ComboBox({
		id : "sourceDataCombobox",
		name : "sourceDataCombobox",
		fieldLabel : '源数据源',
		triggerAction : "all",
		store : store,
		displayField : "name",
		valueField : "name",
		allowBlank : false,
		mode : "remote",
		emptyText : "源数据源选择",
		width : 280,
		listeners : {
			select : function(combo, record, index) {
				var s = Ext.getCmp('sourceTableCombobox');
				s.setValue('');
				s.setRawValue('');
				s.store.proxy = new Ext.data.HttpProxy({
					url : '/dataPush/explorer.do'
				});
				s.store.baseParams.databaseInfo = combo.value;
				s.store.load();
			}
		},
	})
	if (target_table_name != '') {
		sourceCombobox.setValue(target_table_name);
		sourceCombobox.setRawValue(target_table_name);
	}
	return sourceCombobox;
}

//下拉框
function sourceTableSelect(source_table_name) {
	var proxy = new Ext.data.HttpProxy({
		url : "/dataPush/explorer.do"
	});

	var record = Ext.data.Record.create([
		{
			name : "nodeId",
			type : "String",
			mapping : "nodeId"
		},
		{
			name : "text",
			type : "String",
			mapping : "text"
		}
	]);

	var reader = new Ext.data.JsonReader({}, record);

	var sourceTableStore = new Ext.data.Store({
		proxy : proxy,
		reader : reader,
		baseParams : {
			databaseInfo : ''
		},
	});

	var sourceTableCombobox = new Ext.form.ComboBox({
		id : "sourceTableCombobox",
		name : "sourceTableCombobox",
		fieldLabel : '源表',
		triggerAction : "all",
		store : sourceTableStore,
		displayField : "text",
		valueField : "nodeId",
		mode : "remote",
		emptyText : "源表选择",
		allowBlank : false,
		width : 280
	});

	if (source_table_name != "") {
		sourceTableCombobox.setValue(source_table_name);
		sourceTableCombobox.setRawValue(source_table_name);
	}
	return sourceTableCombobox;
}
//下拉框
function targetDataSelect(target_data_name) {
	var proxy = new Ext.data.HttpProxy({
		url : "/common/getDatabases.do"
	});

	var record = Ext.data.Record.create([ {
		name : "name",
		type : "String",
		mapping : "name"
	}
	]);

	var reader = new Ext.data.JsonReader({}, record);

	var store = new Ext.data.Store({
		proxy : proxy,
		reader : reader
	});

	var targetCombobox = new Ext.form.ComboBox({
		id : "targetDataCombobox",
		name : "targetDataCombobox",
		fieldLabel : '目标数据源',
		triggerAction : "all",
		store : store,
		displayField : "name",
		valueField : "name",
		mode : "remote",
		allowBlank : false,
		emptyText : "目标数据源选择",
		width : 280,
		listeners : {
			select : function(combo, record, index) {
				var s = Ext.getCmp('targetTableCombobox');
				s.setValue('');
				s.setRawValue('');
				s.store.proxy = new Ext.data.HttpProxy({
					url : '/dataPush/explorer.do'
				});
				s.store.baseParams.databaseInfo = combo.value;
				s.store.load();
			}
		},
	});
	if (target_data_name != "") {
		targetCombobox.setValue(target_data_name);
		targetCombobox.setRawValue(target_data_name);
	}
	return targetCombobox;
}
//下拉框
function targetTableSelect(target_table_name) {
	var proxy = new Ext.data.HttpProxy({
		url : "/dataPush/explorer.do"
	});

	var record = Ext.data.Record.create([ {
		name : "nodeId",
		type : "String",
		mapping : "nodeId"
	}, {
		name : "text",
		type : "String",
		mapping : "text"
	}
	]);

	var reader = new Ext.data.JsonReader({}, record);

	var store = new Ext.data.Store({
		proxy : proxy,
		reader : reader,
		baseParams : {
			databaseInfo : ''
		},
	});

	var targetTableCombobox = new Ext.form.ComboBox({
		id : "targetTableCombobox",
		name : "targetTableCombobox",
		fieldLabel : '目标表',
		triggerAction : "all",
		store : store,
		displayField : "text",
		valueField : "nodeId",
		allowBlank : false,
		emptyText : "目标表选择",
		width : 280
	});
	if (target_table_name != "") {
		targetTableCombobox.setValue(target_table_name);
		targetTableCombobox.setRawValue(target_table_name);
	}
	return targetTableCombobox;
}

/**
 * 
 */
function submitDataPush(id, status) {
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
						showApplicationPanel(secondGuidePanel);
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

/**
 * 
 */
function deleteDataPush(id) {
	Ext.Msg.confirm('提示信息', '确认要删除这条信息吗？', function(op) {
		if (op == 'yes') {
			Ext.Ajax.request({
				url : "/dataPush/delete.do",
				params : {
					id : id
				},
				success : function(response, config) {
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.show({
							title : '提示信息',
							msg : '删除成功！',
							buttons : Ext.Msg.OK,
							icon : Ext.Msg.INFO //注意此处为INFO  
						});
						var secondGuidePanel = Ext.getCmp("secondGuidePanel");
						showApplicationPanel(secondGuidePanel);
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
	});
}

/**
 * 下拉框
 */
function statusCombobox(type) {
	var statusChooseCom = new Ext.form.ComboBox({
		id : "taskStatus",
		triggerAction : "all",
		store : new Ext.data.JsonStore({
			fields : [ 'id', 'name' ],
			data : [{
					id : '', name : '全部'
				}, {
					id : '0', name : '待提交'
				}, {
					id : '1', name : '待审核'
				}, {
					id : '2', name : '审核通过'
				}, {
					id : '3', name : '驳回'
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
				showApplicationPanel(secondGuidePanel);
			}
		}
	})
	if (type != undefined && type != "") {
		statusChooseCom.setValue(type);
		statusChooseCom.setRawValue(type);
	}
	return statusChooseCom;
}