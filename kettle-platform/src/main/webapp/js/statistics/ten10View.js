function showDataStatisticsTenView(secondGuidePanel) {
	secondGuidePanel.removeAll(true);
	var windowHTML = "<div id='ten' style='height:90%;width:90%;padding-top: 30px;display:inline-block; position:relative;'></div>";

	var viewModulePanel = new Ext.Panel({
		title : "<font size='3px'>前十数据量部门统计</font>",
		width : 1100,
		height : '100%',
		html : windowHTML,
		autoScroll : true,
	});
	secondGuidePanel.add(viewModulePanel);
	secondGuidePanel.doLayout();
	if (document.getElementById('ten')) {
		moduleViewDataTen('', '');
	}

}

function moduleViewDataTen(deptVal, sdate) {
	var years = [];
	var jdData = []
	var data = [];

	var option = {
		baseOption : {
			backgroundColor : 'white', //背景颜色
			timeline : {
				data : years,
				axisType : 'category',
				autoPlay : true,
				playInterval : 1500, //播放速度
				left : '5%',
				right : '5%',
				bottom : '0%',
				width : '90%',
				label : {
					normal : {
						textStyle : {
							color : 'red',
						}
					},
					emphasis : {
						textStyle : {
							color : 'red'
						}
					}
				},
				symbolSize : 10,
				lineStyle : {
					color : '#red'
				},
				checkpointStyle : {
					borderColor : '#red',
					borderWidth : 2
				},
				controlStyle : {
					showNextBtn : true,
					showPrevBtn : true,
					normal : {
						color : '#ff8800',
						borderColor : '#ff8800'
					},
					emphasis : {
						color : 'red',
						borderColor : 'red'
					}
				},
			},
			title: {
	        },
			tooltip : {
				'trigger' : 'axis'
			},
			calculable : true,
			grid : {
				left: '10%',
				right : '10%',
				 bottom: 100
			},
			label : {
				normal : {
					textStyle : {
						color : function(params, Index) { // 标签国家字体颜色
							var colorarrays = [ '#6bc0fb', '#7fec9d', '#84e4dd', '#749f83',
								'#ca8622', '#bda29a', '#aa96da', '#fcbad3', '#f38181', '#fce38a'
							];
							return colorarrays[jdData[0].indexOf(params)];
						}
					}
				}
			},
			yAxis : [ {
				nameGap : 50,
				'type' : 'category',
				interval : 50,
				data : '',
				nameTextStyle : {
					color : 'red'
				},
				axisLabel : { //提示
					textStyle : {
						color : function(params, Index) { // 标签国家字体颜色
							var colorarrays = [ '#6bc0fb', '#7fec9d', '#84e4dd', '#749f83',
								'#ca8622', '#bda29a', '#aa96da', '#fcbad3',
								'#f38181', '#fce38a'
							];
							return colorarrays[jdData[0].indexOf(params)];
						},
					},
				},
				axisLine : {
					lineStyle : {
						color : 'balck' //Y轴颜色
					},
				},
				splitLine : {
					show : false,
					lineStyle : {
						color : 'balck'
					}
				},
			} ],
			xAxis : [ {
				type : 'value',
				name : '',
				splitNumber : 8, //轴线个数
				nameTextStyle : {
					color : 'balck'
				},
				axisLine : {
					lineStyle : {
						color : '#ffa597' //X轴颜色
					}
				},
				axisLabel : {
					formatter : '{value} '
				},
				splitLine : {
					show : true,
					lineStyle : {
						color : '#fedd8b'
					}
				},
			} ],
			series : [ {
				name : '',
				type : 'bar',
				markLine : {
					label : {
						normal : {
							show : false
						}
					},
					lineStyle : {
						normal : {
							color : 'red',
							width : 3
						}
					},
				},
				label : {
					normal : {
						show : true,
						position : 'right', //数值显示在右侧
						formatter : '{c}'
					}
				},
				itemStyle : {
					normal : {
						color : function(params) {
							var colorList = [ '#6bc0fb', '#7fec9d', '#84e4dd', '#749f83',
								'#ca8622', '#bda29a', '#aa96da', '#fcbad3',
								'#f38181', '#fce38a'
							];
							return colorList[jdData[0].indexOf(params.name)];
						},
					}
				},
			}],
			animationEasingUpdate : 'quinticInOut',
			animationDurationUpdate : 1500, //动画效果
		},
		options : []
	};

	var myChart = echarts.init(document.getElementById('ten'));
	Ext.Ajax.request({
		url : "/dataStatistics/getDataTen.do",
		params : {
		},
		success : function(response, config) {
			var result = Ext.decode(response.responseText);

			years = result.years
			option.baseOption.timeline.data = years;
			
			jdData = result.jdData;
			data = result.data;
			for (var n = 0; n < years.length; n++) {
				var res = [];
				for (j = 0; j < data[n].length; j++) {
					res.push({
						name : jdData[n][j],
						value : data[n][j]
					});

				}

				res.sort(function(a, b) {
					return a.value - b.value;
				});

				var res1 = [];
				var res2 = [];
				
				for (t = 0; t < res.length; t++) {
					res1[t] = res[t].name;
					res2[t] = res[t].value;
				}

				option.options.push({
					title: {
						text: years[n] + '前十数据接入状况',
						x : 'center',
						y : 0,
						textStyle : {
							color : '#B4B4B4',
							fontSize : 16,
							fontWeight : 'normal'
						}
					},
					yAxis : {
						data : res1,
					},
					series : [ {
						data : res2
					}]
				});
			}
			//使用刚指定的配置项和数据显示图表。
			myChart.setOption(option);
		},
		failure : failureResponse
	});
}