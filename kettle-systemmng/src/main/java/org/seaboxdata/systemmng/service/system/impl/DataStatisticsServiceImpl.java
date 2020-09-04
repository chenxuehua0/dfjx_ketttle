package org.seaboxdata.systemmng.service.system.impl;

import java.util.List;
import java.util.Map;

import org.seaboxdata.systemmng.dao.DataStatisticsDao;
import org.seaboxdata.systemmng.service.system.DataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataStatisticsServiceImpl implements DataStatisticsService {

	@Autowired
	private DataStatisticsDao dataStatisticsDao;

	@Override
	public List<Map<String, Object>> getDay30(String deptNm, String date) throws Exception {
		return dataStatisticsDao.getDay30(deptNm, date);
	}

	@Override
	public List<Map<String, Object>> getDeptsSelect() throws Exception {
		return dataStatisticsDao.getDeptsSelect();
	}

	@Override
	public List<Map<String, Object>> getCurrentTen(String date) throws Exception {
		return dataStatisticsDao.getCurrentTen(date);
	}

	@Override
	public List<Map<String, Object>> getLastMonthTen(String date) throws Exception {
		return dataStatisticsDao.getLastMonthTen(date);
	}

	@Override
	public List<Map<String, Object>> getTwoMonthesAgoTen(String date) throws Exception {
		return dataStatisticsDao.getTwoMonthesAgoTen(date);
	}

	@Override
	public List<Map<String, Object>> getDay5(String deptNm, String date) throws Exception {
		return dataStatisticsDao.getDay5(deptNm, date);
	}

	@Override
	public List<Map<String, Object>> getFailureJob(String deptNm) throws Exception {
		return dataStatisticsDao.getFailureJob(deptNm);
	}

}
