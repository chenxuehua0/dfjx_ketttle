function totalView(secondGuidePanel) {
	secondGuidePanel.removeAll(true);
	var windowHTML = "<div style='height:400px;position: relative;'><div id='toolbar1' style='position: absolute; top:10px; right:10px; z-index:999' ></div> <div id='day30' style='height:100%;width:100%;display:inline-block; '></div></div>";
	windowHTML += "<div id='ten' style='height:400px;width:100%;padding-top: 30px;'></div>";
	windowHTML += "<div style='height:400px;position: relative;padding-top: 60px;'><div id='toolbar2' style='position: absolute; top:10px; right:10px; z-index:999' ></div> <div id='volatility' style='height:100%;width:100%;display:inline-block; '></div></div>";
	windowHTML += "<div style='height:400px;position: relative;padding-top: 30px;'><div id='toolbar3' style='position: absolute; top:10px; right:5px; z-index:999' ></div> <div id='failureJob' style='height:100%;width:100%;display:inline-block; position: absolute;'></div></div>";
	var viewModulePanel = new Ext.Panel({
		title : "<font size='3px' >数据统计表</font>",
		width : 1100,
		height : '100%',
		html : windowHTML,
		autoScroll : true
	});
	secondGuidePanel.add(viewModulePanel);
	secondGuidePanel.doLayout();

	getTbarForDay30('', '');
	moduleViewData30('', '');

	moduleViewDataTen('', '');

	getTbarForVolatility();
	moduleViewVolatility('', '全部');

	getTbarForFailureJob();
	moduleViewFailureJob('全部', '');
}