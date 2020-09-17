package org.seaboxdata.platform.controller.system;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.seaboxdata.ext.utils.JSONObject;
import org.seaboxdata.systemmng.service.system.DataStatisticsService;
import org.seaboxdata.systemmng.utils.common.StringDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONArray;

/**
 * 
 * @author zhaozm
 *
 */
@Controller
@RequestMapping(value="/dataStatistics")
public class DataStatisticsController {
    @Autowired
    protected DataStatisticsService dataStatisticsService;

    /**
     * 获取30天部门的数据总量统计
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value="/getDay30")
    @ResponseBody
    protected void getDay30(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{
            String deptNm = request.getParameter("deptNm");
			String date = StringDateUtil.getSystemTime();
			
			String maxDate = dataStatisticsService.getMaxDate(deptNm);
			
            List<Map<String, Object>> list=dataStatisticsService.getDay30(deptNm, maxDate);

            List<Object> dateList = new ArrayList<Object>();
            List<Map<String, Object>> qtyList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> spaList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> exchgQtyList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> map : list) {
            	dateList.add(map.get("statt_dt"));//日期
            	
            	Map<String, Object> qtyMap = new HashMap<String, Object>();
            	qtyMap.put("value", map.get("tbl_data_qty"));
            	qtyMap.put("deptNmDesc", map.get("dept_nm_desc"));
            	qtyList.add(qtyMap);
            	
            	
            	Map<String, Object> spaMap = new HashMap<String, Object>();
            	spaMap.put("value", map.get("tbl_data_spa"));
            	spaMap.put("deptNmDesc", map.get("dept_nm_desc"));
            	spaList.add(spaMap);
            	
            	Map<String, Object> exchgQtyMap = new HashMap<String, Object>();
            	exchgQtyMap.put("value", map.get("tbl_day_data_exchg_qty"));
            	exchgQtyMap.put("deptNmDesc", map.get("dept_nm_desc"));
            	exchgQtyList.add(exchgQtyMap);
            	
			}
            
            JSONObject result=new JSONObject();

            result.put("date",dateList);
            result.put("qty",qtyList);
            result.put("spa",spaList);
            result.put("exchgQty",exchgQtyList);

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result.toString());
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @RequestMapping(value="/getDay5")
    @ResponseBody
    protected void getDay5(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{
            String deptNm = request.getParameter("deptNm");
			String date = StringDateUtil.getSystemTime();
			String maxDate = dataStatisticsService.getMaxDate(deptNm);
			List<Map<String, Object>> list = dataStatisticsService.getDay5(deptNm, maxDate);

            List<Object> xcategoryList = new ArrayList<Object>();
            List<Object> lowList = new ArrayList<Object>();
            for (Map<String, Object> map : list) {
            	xcategoryList.add(map.get("statt_dt"));//日期
            	lowList.add(map.get("tbl_day_data_exchg_qty"));
			}
            
            JSONObject result=new JSONObject();

            result.put("xcategory",xcategoryList);
            result.put("low",lowList);

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result.toString());
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }    
    
    @RequestMapping(value="/getFailureJob")
    @ResponseBody
    protected void getFailureJob(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{
            String deptNm = request.getParameter("deptNm");
            String maxDate = dataStatisticsService.get24MaxDate(deptNm);
			List<Map<String, Object>> list = dataStatisticsService.getFailureJob(deptNm, maxDate);

            List<Object> dataAxisList = new ArrayList<Object>();
            List<Object> dataList = new ArrayList<Object>();
            for (Map<String, Object> map : list) {
            	dataAxisList.add(map.get("job_fail_hr"));//日期
            	dataList.add(map.get("fail_count"));
			}
            
            JSONObject result=new JSONObject();

            result.put("dataAxis",dataAxisList);
            result.put("data",dataList);

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result.toString());
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }        
    
    @RequestMapping(value="/getDataTen")
    @ResponseBody
    protected void getDataTen(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{
			String date = StringDateUtil.getSystemTime();
			
			String maxDate = dataStatisticsService.getMaxDate("");
            List<Map<String, Object>> currentLlist=dataStatisticsService.getCurrentTen(maxDate);
            List<Map<String, Object>> lastMonthLlist=dataStatisticsService.getLastMonthTen(maxDate);
            List<Map<String, Object>> twoMonthesAgoList=dataStatisticsService.getTwoMonthesAgoTen(maxDate);

            List<Object> yearsList = new ArrayList<Object>();
            List<Object> jdDataList = new ArrayList<Object>();
            List<Object> dataList = new ArrayList<Object>();
            
            List<Object> twoMonthesAgoMonthJdData = new ArrayList<Object>();
            List<Object> twoMonthesAgoMonthData = new ArrayList<Object>();
            if(twoMonthesAgoList.size() >0 ) {
            	yearsList.add(twoMonthesAgoList.get(0).get("statt_dt"));
            	
            	jdDataList.add(twoMonthesAgoMonthJdData);
                dataList.add(twoMonthesAgoMonthData);
            }
            
            List<Object> lastMonthJdData = new ArrayList<Object>();
            List<Object> lastMonthData = new ArrayList<Object>();
            if(lastMonthLlist.size() >0 ) {
            	yearsList.add(lastMonthLlist.get(0).get("statt_dt"));

                jdDataList.add(lastMonthJdData);
                dataList.add(lastMonthData);
            }

            List<Object> currentMonthJdData = new ArrayList<Object>();
            List<Object> currentMonthData = new ArrayList<Object>();
            if(currentLlist.size() >0 ) {
            	yearsList.add(currentLlist.get(0).get("statt_dt"));

                jdDataList.add(currentMonthJdData);
                dataList.add(currentMonthData);
            }
            
            for (Map<String, Object> map : twoMonthesAgoList) {
            	twoMonthesAgoMonthJdData.add(map.get("dept_nm_desc"));
            	twoMonthesAgoMonthData.add(map.get("tbl_data_qty"));
			}
            
            for (Map<String, Object> map : lastMonthLlist) {
            	lastMonthJdData.add(map.get("dept_nm_desc"));
            	lastMonthData.add(map.get("tbl_data_qty"));
			}
            
            for (Map<String, Object> map : currentLlist) {
            	currentMonthJdData.add(map.get("dept_nm_desc"));
            	currentMonthData.add(map.get("tbl_data_qty"));
			}
            
            JSONObject result=new JSONObject();

            result.put("years",yearsList);
            result.put("jdData",jdDataList);
            result.put("data",dataList);

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result.toString());
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }    
    
    /**
     * 获取部门下拉框
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value="/getDeptsSelect")
    @ResponseBody
    protected void getDeptsSelect(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{

            List<Map<String, Object>> list=dataStatisticsService.getDeptsSelect();
            
            String json = JSONArray.fromObject(list).toString();

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(json);
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }    
}
