package org.seaboxdata.systemmng.service.system;

import java.util.List;
import java.util.Map;

/**
 */
public interface DataStatisticsService {

	public List<Map<String, Object>> getDay30(String deptNm, String date) throws Exception;

	public List<Map<String, Object>> getDeptsSelect() throws Exception;

	public List<Map<String, Object>> getCurrentTen(String date) throws Exception;

	public List<Map<String, Object>> getLastMonthTen(String date) throws Exception;

	public List<Map<String, Object>> getTwoMonthesAgoTen(String date) throws Exception;

	public List<Map<String, Object>> getDay5(String deptNm, String date) throws Exception;

	public List<Map<String, Object>> getFailureJob(String deptNm) throws Exception;
}
