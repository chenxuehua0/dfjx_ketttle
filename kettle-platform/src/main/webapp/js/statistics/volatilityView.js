function showDataStatisticsVolatilityView(secondGuidePanel) {
	secondGuidePanel.removeAll(true);
	var windowHTML = "<div><div id='toolbar2' style='position: absolute; top:10px; right:5px; z-index:999' ></div> <div id='volatility' style='height:100%;width:100%;display:inline-block; position: absolute;'></div></div>";

	var viewModulePanel = new Ext.Panel({
		title : "<font size='3px' >5日数据波动性</font>",
		width : 1100,
		height : '100%',
		html : windowHTML,
		autoScroll : true
	});
	secondGuidePanel.add(viewModulePanel);
	secondGuidePanel.doLayout();

	getTbarForVolatility();
	if (document.getElementById('volatility')) {
		var deptVal = Ext.getCmp("deptNm1").getValue();
		moduleViewVolatility(deptVal, '全部');
	}

}

function getTbarForVolatility() {
	var detpCombobox = deptSelection1();

	var toolBar = new Ext.Toolbar({
		renderTo : 'toolbar2',
		items : [
			detpCombobox
		]
	});
	return toolBar;
}

//任务类型下拉选择框
function deptSelection1() {
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
		id : "deptNm1",
		triggerAction : "all",
		store : storeProvince,
		displayField : "dept_nm_desc",
		valueField : "dept_nm",
		emptyText : "部门",
		mode : "local",
		listeners : {
			//index是被选中的下拉项在整个列表中的下标 从0开始
			'select' : function(combo, record, index) {
				var deptVal = Ext.getCmp("deptNm1").getValue();
				var text = Ext.getCmp('deptNm1').getRawValue();
				moduleViewVolatility(deptVal, text);
			}
		}
	});
	return detpCombobox;
}

function moduleViewVolatility(deptVal, text) {
	var option = {
		backgroundColor : "#fff",
		tooltip : {
			trigger : 'axis'
		},
		title : {
			text : text + '-5日数据波动',
			x : 'center',
			y : 0,
			textStyle : {
				color : '#B4B4B4',
				fontSize : 16,
				fontWeight : 'normal'
			}
		},
		grid : {
			left: '10%',
			right : '10%'
		},
		xAxis : {
			axisLine : {
				show : false
			},
			axisTick : {
				show : false
			},
			axisLabel : {
				interval : 0,
				show : true
			},
			data : []
		},
		yAxis : {
			axisLine : {
				show : false,
			},
			axisTick : {
				show : false
			},
			splitLine : {
				show : true,
				lineStyle : {
					type : 'dashed',
					color : '#eee'
				}
			}
		},
		series : [ {
			type : 'line',
			// smooth: true,
			symbol : 'circle',
			symbolSize : 25,
			lineStyle : {
				normal : {
					width : 4,
					shadowColor : 'rgba(155, 18, 184, .3)',
					shadowBlur : 10,
					shadowOffsetY : 20,
					shadowOffsetX : 20,
					color : new echarts.graphic.LinearGradient(
						0, 0, 1, 0,
						[ {
							offset : 0,
							color : 'rgba(255, 255, 255,1)'
						}, {
							offset : 0.1,
							color : 'rgba(255, 75, 172,1)'
						}, {
							offset : 0.9,
							color : 'rgba(155, 18, 184,1)'
						}, {
							offset : 1,
							color : 'rgba(255, 255, 255,1)'
						}, ]
					)
				}
			},
			itemStyle : {
				color : '#fff',
				borderColor : "#7c1fa2",
				borderWidth : 4,
			},

			data : []
		}, {
			name : '',
			type : 'lines',
			coordinateSystem : 'cartesian2d',
			zlevel : 1,
			smooth : true,
			symbol : 'circle',
			symbolSize : 25,
			effect : {
				show : true,
				smooth : false,
				period : 2,
				symbolSize : 8
			},
			lineStyle : {
				normal : {
					color : '#f00',
					width : 0,
					opacity : 0,
					curveness : 0,
				}
			},
			data : []
		}
		]
	};

	var myChart = echarts.init(document.getElementById('volatility'));
	Ext.Ajax.request({
		url : "/dataStatistics/getDay5.do",
		params : {
			deptNm : deptVal
		},
		success : function(response, config) {
			var result = Ext.decode(response.responseText);
			var xcategory = result.xcategory;
			var low = result.low;
			var lowLine = [];
			var zrUtil = echarts.util;

			zrUtil.each(xcategory, function(item, index) {
				lowLine.push([ {
					coord : [ index, low[index] ]
				}, {
					coord : [ index + 1, low[index + 1] ]
				} ]);
			});

			option.xAxis.data = xcategory;
			option.series[0].data = low;
			option.series[1].data = lowLine;

			myChart.setOption(option);
		},
		failure : failureResponse
	});
}