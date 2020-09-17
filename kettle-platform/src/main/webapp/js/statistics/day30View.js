function showDataStatistics30View(secondGuidePanel) {
	secondGuidePanel.removeAll(true);
	var windowHTML = "<div><div id='toolbar1' style='position: absolute; top:10px; right:5px; z-index:999' ></div> <div id='day30' style='height:100%;width:100%;display:inline-block; position: absolute;'></div></div>";

	var viewModulePanel = new Ext.Panel({
		title : "<font size='3px' >30天部门的数据总量统计</font>",
		width : 1100,
		height : '100%',
		html : windowHTML,
		autoScroll : true
	});
	secondGuidePanel.add(viewModulePanel);
	secondGuidePanel.doLayout();

	getTbarForDay30();
	if (document.getElementById('day30')) {
		var deptVal = Ext.getCmp("deptNm").getValue();
		moduleViewData30('');
	}

}

function getTbarForDay30() {
	var detpCombobox = deptSelection();

	var toolBar = new Ext.Toolbar({
		renderTo : 'toolbar1',
		items : [
			detpCombobox
		]
	});
	return toolBar;
}

//任务类型下拉选择框
function deptSelection() {
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
		id : "deptNm",
		triggerAction : "all",
		store : storeProvince,
		displayField : "dept_nm_desc",
		valueField : "dept_nm",
		emptyText : "部门",
		mode : "local",
		listeners : {
			//index是被选中的下拉项在整个列表中的下标 从0开始
			'select' : function(combo, record, index) {
				var deptVal = Ext.getCmp("deptNm").getValue();

				moduleViewData30(deptVal);
			}
		}
	});
	return detpCombobox;
}

function moduleViewData30(deptVal) {
	var option = {
		title : {
			text : '30日数据量走势',
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
			backgroundColor : 'rgba(0,0,0,0.6)',
			formatter : function(params) {
				let str = params[0].name + ' ' + params[0].data.deptNmDesc + '</br>';
				params.forEach(item => {
					if (item.seriesName === '数据总量') {
						item.marker = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#FF8000;"></span>';
						str += item.marker + item.seriesName + ' : ' + item.data.value + '</br>';
					} else if (item.seriesName === '数据容量') {
						// 柱状图渐变时设置marker
						item.marker = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:#6C50F3;"></span>';
						str += item.marker + item.seriesName + ' : ' + item.data.value;
					}
				});
				return str;
			}
		},
		legend : {
			// 修改legend的高度宽度
			itemHeight : 5,
			itemWidth : 24,
			data : [ {
				name : '数据总量',
				icon : 'rect' // ledend的icon
			}, {
				name : '数据容量',
				icon : 'rect'
			}
			],
			textStyle : {
				color : '#B4B4B4'
			},
			top : '7%',
			// 选择关闭的legend
			selected : {
			}
		},
		grid : {
			left: '10%',
			right : '10%',
		},
		xAxis : [ {
			data : [], //x轴
			boundaryGap : true,
			axisLine : {
				lineStyle : {
					color : '#B4B4B4'
				}
			},
			axisTick : {
				show : false
			}
		} ],
		yAxis : [ {
			name : '数据总量(万条)',
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
					color : '#333'
				},
				formatter : '{value} '
			}
		}, {
			name : '数据容量(G)',
			nameLocation : 'middle',
			nameTextStyle : {
				padding : [ 50, 4, 5, 6 ]
			},
			splitLine : {
				show : false
			},
			axisLine : {
				show : false
			},
			axisTick : {
				show : false
			},
			axisLabel : {
				textStyle : {
					color : '#333'
				},
				formatter : '{value} '
			}
		} ],
		series : [ {
			name : '数据总量',
			type : 'line',
			smooth : true,
			showSymbol : true,
			// 矢量画五角星
			symbol : 'path://M150 0 L80 175 L250 75 L50 75 L220 175 Z',
			symbolSize : 12,
			yAxisIndex : 0,
			areaStyle : {
				normal : {
					color : new echarts.graphic.LinearGradient(0, 0, 0, 1, [ {
						offset : 0,
						color : 'rgba(250,180,101,0.3)'
					}, {
						offset : 1,
						color : 'rgba(250,180,101,0)'
					}
					]),
					shadowColor : 'rgba(250,180,101,0.2)',
					shadowBlur : 20
				}
			},
			itemStyle : {
				normal : {
					color : '#FF8000'
				}
			},
			// data中可以使用对象，value代表相应的值，另外可加入自定义的属性
			data : []
		}, {
			name : '数据容量',
			type : 'bar',
			barWidth : 30,
			yAxisIndex : 1,
			itemStyle : {
				normal : {
					color : new echarts.graphic.LinearGradient(0, 0, 0, 1, [ {
						offset : 0,
						color : 'rgba(108,80,243,0.3)'
					}, {
							offset : 1,
							color : 'rgba(108,80,243,0)'
						}
					]),
					//柱状图圆角
					barBorderRadius : [ 30, 30, 0, 0 ],
				}
			},
			data : []
		} ]
	};

	var myChart = echarts.init(document.getElementById('day30'));
	Ext.Ajax.request({
		url : "/dataStatistics/getDay30.do",
		params : {
			deptNm : deptVal
		},
		success : function(response, config) {
			var result = Ext.decode(response.responseText);
			option.xAxis[0].data = result.date;
			option.series[0].data = result.qty;
			option.series[1].data = result.spa;
			myChart.setOption(option);
		},
		failure : failureResponse
	});
}