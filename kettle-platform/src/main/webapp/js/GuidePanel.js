var timeIntervalByTaskControl="";
var loginUserName="";		//当前登录的用户名
var loginUserSlavePower="";	//当前登录的用户对节点的权限
var loginUserTaskGroupPower="";	//当前登录的用户对任务组的权限
var loginUserType="";			//当前登录的用户类型
var belongToUserGroup="";		//所属用户组

//给新创建的任务/转换分配任务组
function assignedTaskGroupByCreate(taskName,type,secondGuidePanel){
	Ext.Ajax.request({
		url:"/task/getJobOrTransByName.do",
		success:function(response,config){
			var result=Ext.decode(response.responseText);
			var grid="";
			if(type=="job"){
				var jobId=result.jobId;
				var jobName=taskName;
				var jobPath=result.directoryName;
				grid=generateAllTaskGroupPanel(jobId,jobPath,jobName,"create");
			}else{
				var transId=result.transformationId;
				var transName=result.name;
				var transPath=result.directoryName;
				grid=AllTaskGroupPanel(transId,transPath,transName,"create");
			}
			var assignedWindowByCreate=new Ext.Window({
				id:"assignedWindowByCreate",
				title:"任务组分配",
				bodyStyle:"background-color:white",
				width:455,
				height:570,
				listeners:{
					"close":function(){
						if(type=="trans"){
							Ext.getCmp("bodyPanelForTrans").enable();
							Ext.getCmp("westTreePanelForTrans").enable();
						}else{
							Ext.getCmp("jobBodyPanel").enable();
							Ext.getCmp("jobWestTreePanel").enable();
						}
					}
				},
				items:[
					grid
				]
			});
			assignedWindowByCreate.show(secondGuidePanel);
		},
		params:{taskName:taskName,type:type}
	})
}

function repositryOpenJob(secondGuidePanel,path,text){
	try {
		Ext.getBody().mask('正在加载，请稍后...', 'x-mask-loading');
		Ext.Ajax.request({
			url: GetUrl('repository/open.do'),
			timeout: 120000,
			params: {path: path, type: 'job'},
			method: 'POST',
			success: function(response, opts) {
				try {
					var jobComponentTree = new Ext.tree.TreePanel({
						id:"jobWestTreePanel",
						region: 'west',
						split: true,
						width: 200,
						title: '核心对象',
						useArrows: true,
						root: new Ext.tree.AsyncTreeNode({text: 'root'}),
						loader: new Ext.tree.TreeLoader({
							dataUrl: GetUrl('system/jobentrys.do')
						}),
						enableDD:true,
						ddGroup:'TreePanelDDGroup',
						autoScroll: true,
						animate: false,
						rootVisible: false,
						tbar:[
							new Ext.form.TextField({
								width:150,
								emptyText:'请输入关键字检索',
								enableKeyEvents: true,
								listeners:{
									keyup:function(node, event) {
										findByKeyWordFiler(node, event);
									},
									scope: this
								}
							})
						]
					});

					var treeFilter = new Ext.tree.TreeFilter(jobComponentTree, {
						clearBlank : true,
						autoClear : true
					});
					var timeOutId  = null;
					var hiddenPkgs = [];
					var findByKeyWordFiler = function(node, event) {

						clearTimeout(timeOutId);// 清除timeOutId
						jobComponentTree.expandAll();// 展开树节点
						// 为了避免重复的访问后台，给服务器造成的压力，采用timeOutId进行控制，如果采用treeFilter也可以造成重复的keyup
						timeOutId = setTimeout(function() {
							// 获取输入框的值
							var text = node.getValue();
							// 根据输入制作一个正则表达式，'i'代表不区分大小写
							var re = new RegExp(Ext.escapeRe(text), 'i');
							// 先要显示上次隐藏掉的节点
							Ext.each(hiddenPkgs, function(n) {
								n.ui.show();
							});
							hiddenPkgs = [];
							if (text != "") {
								treeFilter.filterBy(function(n) {
									// 只过滤叶子节点，这样省去枝干被过滤的时候，底下的叶子都无法显示
									return !n.isLeaf() || re.test(n.text);
								});
								// 如果这个节点不是叶子，而且下面没有子节点，就应该隐藏掉
								jobComponentTree.root.cascade(function(n) {
									if(n.id!='0'){
										if(!n.isLeaf() &&judge(n,re)==false&& !re.test(n.text)){
											hiddenPkgs.push(n);
											n.ui.hide();
										}
									}
								});
							} else {
								treeFilter.clear();
								return;
							}
						}, 500);
					}

					// 过滤不匹配的非叶子节点或者是叶子节点
					var judge =function(n,re){
						var str=false;
						n.cascade(function(n1){
							if(n1.isLeaf()){
								if(re.test(n1.text)){ str=true;return; }
							} else {
								if(re.test(n1.text)){ str=true;return; }
							}
						});
						return str;
					};

					var graphPanel = Ext.create({repositoryId: path, region: 'center',id:'jobBodyPanel'}, 'JobGraph');
					secondGuidePanel.add({
						layout: 'border',
						items: [jobComponentTree, graphPanel]
					});
					secondGuidePanel.doLayout();
					activeGraph = graphPanel;
					var xmlDocument = mxUtils.parseXml(decodeURIComponent(response.responseText));
					var decoder = new mxCodec(xmlDocument);
					var node = xmlDocument.documentElement;
					var graph = graphPanel.getGraph();
					decoder.decode(node, graph.getModel());
					graphPanel.fireEvent('load');
				} finally {
					Ext.getBody().unmask();
				}
			},
			failure: failureResponse
		});

	} finally {
		Ext.getBody().unmask();
	}
}

function repositryOpenTrans(secondGuidePanel,path,text){
	try {
		Ext.getBody().mask('正在加载，请稍后...', 'x-mask-loading');
		Ext.Ajax.request({
			url: GetUrl('repository/open.do'),
			timeout: 120000,
			params: {path: path, type: 'transformation'},
			method: 'POST',
			success: function(response, opts) {
				try {
					var transComponentTree = new Ext.tree.TreePanel({
						id:"westTreePanelForTrans",
						region: 'west',
						split: true,
						width: 200,
						title: '核心对象',
						useArrows: true,
						root: new Ext.tree.AsyncTreeNode({text: 'root'}),
						loader: new Ext.tree.TreeLoader({
							dataUrl: GetUrl('system/steps.do')
						}),
						enableDD:true,
						ddGroup:'TreePanelDDGroup',
						autoScroll: true,
						animate: false,
						rootVisible: false,
						tbar:[
							new Ext.form.TextField({
								width:150,
								emptyText:'请输入关键字检索',
								enableKeyEvents: true,
								listeners:{
									keyup:function(node, event) {
										findByKeyWordFiler(node, event);
									},
									scope: this
								}
							})
						]
					});

					var treeFilter = new Ext.tree.TreeFilter(transComponentTree, {
						clearBlank : true,
						autoClear : true
					});
					var timeOutId  = null;
					var hiddenPkgs = [];
					var findByKeyWordFiler = function(node, event) {

						clearTimeout(timeOutId);// 清除timeOutId
						transComponentTree.expandAll();// 展开树节点
						// 为了避免重复的访问后台，给服务器造成的压力，采用timeOutId进行控制，如果采用treeFilter也可以造成重复的keyup
						timeOutId = setTimeout(function() {
							// 获取输入框的值
							var text = node.getValue();
							// 根据输入制作一个正则表达式，'i'代表不区分大小写
							var re = new RegExp(Ext.escapeRe(text), 'i');
							// 先要显示上次隐藏掉的节点
							Ext.each(hiddenPkgs, function(n) {
								n.ui.show();
							});
							hiddenPkgs = [];
							if (text != "") {
								treeFilter.filterBy(function(n) {
									// 只过滤叶子节点，这样省去枝干被过滤的时候，底下的叶子都无法显示
									return !n.isLeaf() || re.test(n.text);
								});
								// 如果这个节点不是叶子，而且下面没有子节点，就应该隐藏掉
								transComponentTree.root.cascade(function(n) {
									if(n.id!='0'){
										if(!n.isLeaf() &&judge(n,re)==false&& !re.test(n.text)){
											hiddenPkgs.push(n);
											n.ui.hide();
										}
									}
								});
							} else {
								treeFilter.clear();
								return;
							}
						}, 500);
					}

					// 过滤不匹配的非叶子节点或者是叶子节点
					var judge =function(n,re){
						var str=false;
						n.cascade(function(n1){
							if(n1.isLeaf()){
								if(re.test(n1.text)){ str=true;return; }
							} else {
								if(re.test(n1.text)){ str=true;return; }
							}
						});
						return str;
					};

					var graphPanel = Ext.create({repositoryId: path, region: 'center',id:'bodyPanelForTrans'}, 'TransGraph');
					secondGuidePanel.add({
						layout: 'border',
						items: [transComponentTree, graphPanel]
					});
					secondGuidePanel.doLayout();
					activeGraph = graphPanel;
					var xmlDocument = mxUtils.parseXml(decodeURIComponent(response.responseText));
					var decoder = new mxCodec(xmlDocument);
					var node = xmlDocument.documentElement;
					var graph = graphPanel.getGraph();
					decoder.decode(node, graph.getModel());
					graphPanel.fireEvent('load');
				} finally {
					Ext.getBody().unmask();
				}
			},
			failure: failureResponse
		});
	} finally {
		Ext.getBody().unmask();
	}
}

GuidePanel = Ext.extend(Ext.Panel,{
	border:false,
	initComponent: function() {
		var fristGuidePanel="";
		loginUserName=document.getElementById("loginUsername").value;
		loginUserSlavePower=document.getElementById("slavePowerHidden").value;
		loginUserTaskGroupPower=document.getElementById("taskGroupPowerHidden").value;
		loginUserType=document.getElementById("userTypeHidden").value;
		belongToUserGroup=document.getElementById("belongToUserGroup").value;

		fristGuidePanel = new Ext.tree.TreePanel({
			useArrows: true,
			region: 'west',
			width: 200,
			split: true,
			loader : new Ext.tree.TreeLoader({
				dataUrl : 'resource/queryResource.do',
				baseParams : {}
			}),
			root : new Ext.tree.AsyncTreeNode({
				id:'0',
				text: '根节点',
			}),
			rootVisible: false,
			enableDD:true,
			ddGroup:'TreePanelDDGroup',
			autoScroll: true,
			animate: false,
			listeners: {
				afterrender: function(node) {
				}
			}
		});

		var secondGuidePanel = new Ext.Panel({
			region:'center',
			layout:'fit',
			id: 'secondGuidePanel'
			// border: false,
			// bodyStyle : 'background:#CCC'
		});

		fristGuidePanel.on('click', function(node, e) {
			if(timeIntervalByTaskControl!=""){
				clearInterval(timeIntervalByTaskControl);
				timeIntervalByTaskControl="";
			}
			if(moduleViewInterval!=""){
				clearInterval(moduleViewInterval);
				moduleViewInterval="";
			}
			
			var alias = node.attributes.alias;
			if(alias == 'newTrans') {
				//设置文本框格式
				var dlg = Ext.Msg.getDialog();
				var t = Ext.get(dlg.body).select('.ext-mb-input');
				t.each(function (el) {
					el.dom.type = "text";
				});
				secondGuidePanel.removeAll(true);
				Ext.Msg.prompt('系统提示', '请输入转换名称:', function(btn, text){
				    if (btn == 'ok' && text != '') {
				    	
				    	var result = is_forbid(text);
				    	
				    	if(!result){
				    		Ext.Msg.show({  
				        	    title:'提示信息',  
				        	    msg: '名称包含非法字符！',  
				        	    buttons: Ext.Msg.OK,  
				        	    icon: Ext.Msg.INFO 
				        	});
				    		
				    		return ;
				    	}
				    	
				    	
						var taskGroupPanel=getAllTaskGroupBeforeCreate();
						var addTaskGroupWindow=new Ext.Window({
							title:"分配任务组",
							bodyStyle:"background-color:white",
							width:450,
							modal:true,
							height:550,
							items:[
								taskGroupPanel
							],
							tbar:new Ext.Toolbar({buttons:[
								{
									text:"下一步",
									handler:function(){
										var view=taskGroupPanel.getView();
										var rsm=taskGroupPanel.getSelectionModel();
										var taskGroupNameArray=new Array();
										for(var i=0;i<view.getRows().length;i++) {
											if(rsm.isSelected(i)){
												taskGroupNameArray.push(taskGroupPanel.getStore().getAt(i).get("taskGroupName"));
											}
										}
										if(taskGroupNameArray.length>0){
											addTaskGroupWindow.close();
											Ext.getBody().mask('正在创建转换，请稍后...');
											Ext.Ajax.request({
												url: GetUrl('repository/createTrans.do'),
												method: 'POST',
												params: {dir: '/',transName:text,taskGroupArray:taskGroupNameArray},
												success: function(response) {
													var result=Ext.decode(response.responseText);
													if(result.success==false){
														Ext.getBody().unmask();
														Ext.MessageBox.alert("创建失败",result.message);
													}else{
														var path = Ext.decode(response.responseText).message;
														repositryOpenTrans(secondGuidePanel,path,text)
													}
												},
												failure: failureResponse
											});
										}else{
											Ext.MessageBox.alert("提示","请选择任务组");
											return;
										}
									}
								}
							]})
						});
						addTaskGroupWindow.show(secondGuidePanel);
				    }
				});
			}
			else if(alias == 'newJob')
			{
				secondGuidePanel.removeAll(true);
				//设置文本框格式
				var dlg = Ext.Msg.getDialog();
				var t = Ext.get(dlg.body).select('.ext-mb-input');
				t.each(function (el) {
					el.dom.type = "text";
				});
				Ext.Msg.prompt('系统提示', '请输入作业名称:', function(btn, text){
				    if (btn == 'ok' && text != '') {
			    		var result = is_forbid(text);
				    	
				    	if(!result){
				    		Ext.Msg.show({  
				        	    title:'提示信息',  
				        	    msg: '名称包含非法字符！',  
				        	    buttons: Ext.Msg.OK,  
				        	    icon: Ext.Msg.INFO 
				        	});
				    		
				    		return ;
				    	}
				    	
						var taskGroupPanel=getAllTaskGroupBeforeCreate();
						var addTaskGroupWindow=new Ext.Window({
							title:"分配任务组",
							bodyStyle:"background-color:white",
							width:450,
							modal:true,
							height:550,
							items:[
								taskGroupPanel
							],
							tbar:new Ext.Toolbar({buttons:[
								{
									text:"下一步",
									handler:function(){
										var view=taskGroupPanel.getView();
										var rsm=taskGroupPanel.getSelectionModel();
										var taskGroupNameArray=new Array();
										for(var i=0;i<view.getRows().length;i++) {
											if(rsm.isSelected(i)){
												taskGroupNameArray.push(taskGroupPanel.getStore().getAt(i).get("taskGroupName"));
											}
										}
										if(taskGroupNameArray.length>0){
											addTaskGroupWindow.close();
											Ext.getBody().mask('正在创建作业，请稍后...');
											Ext.Ajax.request({
												url: GetUrl('repository/createJob.do'),
												method: 'POST',
												params: {dir: '/', jobName: text,taskGroupArray:taskGroupNameArray},
												success: function(response) {
													var result=Ext.decode(response.responseText);
													if(result.success==false){
														Ext.getBody().unmask();
														Ext.MessageBox.alert("创建失败",result.message);
													}else{
														var path = Ext.decode(response.responseText).message;
														repositryOpenJob(secondGuidePanel,path,text);
													}

												},
												failure: failureResponse
											});
										}else{
											Ext.MessageBox.alert("提示","必须为该作业分配至少一个任务组");
											return;
										}
									}
								}
							]})
						});
						addTaskGroupWindow.show(secondGuidePanel);
				    }
				});
			}else if(alias == 'jobMonitor') {
				generateJobPanel(secondGuidePanel);
			}else if(alias == 'transMonitor') {
				generateTrans(secondGuidePanel);
			}else if(alias == 'schedulerMonitor') {
				generateSchedulerMonitorPanel(secondGuidePanel);
			}else if(alias == 'taskMonitoring'){
				secondGuidePanel.removeAll(true);
				secondGuidePanel.add(showTaskControlPanel());
				secondGuidePanel.doLayout();
				timeIntervalByTaskControl=setInterval("refreshControlPanel()",5000);
			}else if(alias == 'slaveMonitor'){
				slaveManager(secondGuidePanel);
			}else if(alias == 'slaveMonitoring'){
				showSlaveMonitorPanel(secondGuidePanel);
			}else if(alias == 'taskGroupMonitor'){
				showTaskGroupPanel(secondGuidePanel);
			}else if(alias == 'userMonitor'){
				showUserPanel(secondGuidePanel);
			}else if(alias == 'userGroupMonitor'){
				generateUserGroupPanel(secondGuidePanel);
			}else if(alias == 'taskLog'){
				showHistoryLogPanel(secondGuidePanel);
			}else if(alias == 'platformMonitor'){
				showModuleView(secondGuidePanel);
			} else if(alias == 'dataStatistics'){
				totalView(secondGuidePanel);
				//showDataStatisticsFailureJob(secondGuidePanel);
				//showDataStatisticsVolatilityView(secondGuidePanel)
				//showDataStatisticsTenView(secondGuidePanel)
				//showDataStatistics30View(secondGuidePanel);
			} else if(alias == 'resourceApplication') {//申请
				showApplicationPanel(secondGuidePanel);
			} else if(alias == 'resourceApproval') {//审批
				new Ext.data.JsonStore({
					storeId: 'taskUserStore',
					fields: ['id', 'name'],
					proxy: new Ext.data.HttpProxy({
						url: '/dataPush/getUsers.do',
						method: 'POST'
					})
				}).load();
				showApprovalPanel(secondGuidePanel);
			}
		});

		this.items = [fristGuidePanel, secondGuidePanel];
		GuidePanel.superclass.initComponent.call(this);

		/** 默认显示平台概况 */
		showModuleView(secondGuidePanel);
	}
});

//正则
function trimTxt(txt){
	return txt.replace(/(^\s*)|(\s*$)/g, "");
}

