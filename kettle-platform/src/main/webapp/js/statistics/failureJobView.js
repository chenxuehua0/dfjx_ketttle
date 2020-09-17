function showDataStatisticsFailureJob(secondGuidePanel) {
	secondGuidePanel.removeAll(true);
	var windowHTML = "<div><div id='toolbar3' style='position: absolute; top:10px; right:5px; z-index:999' ></div> <div id='failureJob' style='height:100%;width:100%;display:inline-block; position: absolute;'></div></div>";

	var viewModulePanel = new Ext.Panel({
		title : "<font size='3px' >30天部门的数据总量统计</font>",
		width : 1100,
		height : '100%',
		html : windowHTML,
		autoScroll : true
	});
	
	secondGuidePanel.add(viewModulePanel);
	secondGuidePanel.doLayout();

	getTbarForFailureJob();
	
	if (document.getElementById('failureJob')) {
		var deptVal = Ext.getCmp("failureJobDeptNm").getValue();
		moduleViewFailureJob('全部', deptVal);
	}

}

function getTbarForFailureJob() {
	var detpCombobox = failureJobDdeptSelection();

	var toolBar = new Ext.Toolbar({
		renderTo : 'toolbar3',
		items : [
			detpCombobox
		]
	});
	return toolBar;
}

//任务类型下拉选择框
function failureJobDdeptSelection() {
	var storeProvince = new Ext.data.JsonStore({
		autoLoad : true,
		url : "/dataStatistics/getDeptsSelect.do",
		fields : [ {
			name : 'dept_nm'
		}, {
			name : 'dept_nm_desc'
		} ]
	});

	var detpCombobox = new Ext.form.ComboBox({
		id : "failureJobDeptNm",
		triggerAction : "all",
		store : storeProvince,
		displayField : "dept_nm_desc",
		valueField : "dept_nm",
		emptyText : "部门",
		mode : "local",
		listeners : {
			//index是被选中的下拉项在整个列表中的下标 从0开始
			'select' : function(combo, record, index) {
				var deptVal = Ext.getCmp("failureJobDeptNm").getValue();
				var text = Ext.getCmp('failureJobDeptNm').getRawValue();
				moduleViewFailureJob(text, deptVal);
			}
		}
	});
	
	return detpCombobox;
}

function moduleViewFailureJob(text, deptVal) {
	var option = {
		title : {
			text : text + '--24小时作业失败分布',
			x : 'center',
			y : 0,
			textStyle : {
				color : '#B4B4B4',
				fontSize : 16,
				fontWeight : 'normal'
			}
		},
		tooltip : {
			trigger : 'axis',
		},
		xAxis : {
			data : [],
			axisLabel : {
				inside : false,
				textStyle : {
					color : 'black'
				}
			},
			axisTick : {
				show : false
			},
			axisLine : {
				show : false
			},
			z : 10
		},
		yAxis : {
            name: '次',
			splitLine : {
				show : true,
				lineStyle : {
					type : 'dashed',
					color : '#eee'
				}
			},
			axisLine : {
				show : false
			},
			axisTick : {
				show : false
			},
			axisLabel : {
				textStyle : {
					color : '#999'
				}
			}
		},
		dataZoom : [ {
			type : 'inside'
		}],
		series : [ {
			type : 'bar',
			itemStyle : {
				color : new echarts.graphic.LinearGradient(
					0, 0, 0, 1,
					[ {
						offset : 0,
						color : '#83bff6'
					}, {
						offset : 0.5,
						color : '#188df0'
					}, {
						offset : 1,
						color : '#188df0'
					} ]
				)
			},
			emphasis : {
				itemStyle : {
					color : new echarts.graphic.LinearGradient(
						0, 0, 0, 1,
						[ {
							offset : 0,
							color : '#2378f7'
						}, {
							offset : 0.7,
							color : '#2378f7'
						}, {
							offset : 1,
							color : '#83bff6'
						} ]
					)
				}
			},
			data : []
		}
		]
	};

	queryFailureJob(deptVal, option);
}

/**
 * 查下数据
 */
function queryFailureJob(deptVal, option) {
	Ext.Ajax.request({
		url : "/dataStatistics/getFailureJob.do",
		params : {
			deptNm : deptVal
		},
		success : function(response, config) {
			var result = Ext.decode(response.responseText);
			showFailureJob(result, option);
		},
		failure : failureResponse
	});
}

/**
 * 显示图表
 */
function showFailureJob(result, option) {
	//基于准备好的dom，初始化echarts实例
	var myChart = echarts.init(document.getElementById('failureJob'));
	var dataAxis = result.dataAxis;
	var data = result.data;
	var yMax = 500;
	var dataShadow = [];
	for (var i = 0; i < data.length; i++) {
		dataShadow.push(yMax);
	}

	option.xAxis.data = dataAxis;
	option.series[0].data = data;

	//option.series[0].data = dataShadow;

	var zoomSize = 6;
	myChart.on('click', function(params) {
		myChart.dispatchAction({
			type : 'dataZoom',
			startValue : dataAxis[Math.max(params.dataIndex - zoomSize / 2, 0)],
			endValue : dataAxis[Math.min(params.dataIndex + zoomSize / 2, data.length - 1)]
		});
	});

	//使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
}