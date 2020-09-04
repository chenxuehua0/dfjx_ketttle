package org.seaboxdata.systemmng.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataStatisticsDao {

	List<Map<String, Object>> getDay30(@Param("deptNm") String deptNm, @Param("sDate") String date) throws Exception;

	List<Map<String, Object>> getDeptsSelect() throws Exception;

	List<Map<String, Object>> getCurrentTen(@Param("sDate") String date) throws Exception;

	List<Map<String, Object>> getLastMonthTen(@Param("sDate") String date) throws Exception;

	List<Map<String, Object>> getTwoMonthesAgoTen(@Param("sDate") String date) throws Exception;

	List<Map<String, Object>> getDay5(@Param("deptNm") String deptNm, @Param("sDate") String date) throws Exception;

	List<Map<String, Object>> getFailureJob(@Param("deptNm") String deptNm) throws Exception;

}
