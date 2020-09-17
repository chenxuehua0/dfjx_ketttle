package org.seaboxdata.platform.controller.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.util.Utils;
import org.seaboxdata.ext.utils.RepositoryUtils;
import org.seaboxdata.systemmng.bean.DatabaseNode;
import org.seaboxdata.systemmng.entity.UserGroupAttributeEntity;
import org.seaboxdata.systemmng.entity.UserGroupEntity;
import org.seaboxdata.systemmng.service.system.DataPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.json.JSONUtil;

/**
 * @see 数据推送
 * 
 * @author 管理员
 *
 */
@Controller
@RequestMapping(value = "/dataPush")
public class DataPushController {
	@Autowired
	private DataPushService dataPushService;
	
	/**
	 * 
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/getList")
	@ResponseBody
	protected void getList(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			Integer start = Integer.valueOf(request.getParameter("start"));
			Integer limit = Integer.valueOf(request.getParameter("limit"));
			
			String result = dataPushService.getList(start, limit, request);

			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/getApprovalList")
	@ResponseBody
	protected void getApprovalList(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			Integer start = Integer.valueOf(request.getParameter("start"));
			Integer limit = Integer.valueOf(request.getParameter("limit"));
			
			String result = dataPushService.getApprovalList(start, limit, request);

			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}	
	
	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("DatabaseController",
			LoggingObjectType.DATABASE, null);

	/**
	 * 
	 * @param response
	 * @param request
	 * @param databaseInfo
	 * @throws IOException
	 * @throws KettleException
	 * @throws SQLException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/explorer")
	@ResponseBody
	protected void explorer(HttpServletResponse response, HttpServletRequest request, @RequestParam String databaseInfo)
			throws IOException, KettleException, SQLException {
		DatabaseMeta databaseMeta = null;
		System.out.println(databaseInfo);
		if (StringUtils.isEmpty(databaseInfo)) {
			return;
		}

		List<DatabaseMeta> databseList = RepositoryUtils.getRepository().readDatabases();
		for (DatabaseMeta ci : databseList) {
			if (ci.getName().equalsIgnoreCase(databaseInfo)) {
				databaseMeta = ci;
				break;
			}
		}

		List<DatabaseNode> resultList = new ArrayList<DatabaseNode>();

		Database db = new Database(loggingObject, databaseMeta);
		try {
			db.connect();

			Map<String, Map<String, String>> tableMap = getTableMap(databaseMeta, db);

			List<String> tableKeys = new ArrayList<String>(tableMap.keySet());
			Collections.sort(tableKeys);
			for (String table : tableKeys) {
				Map<String, String> subMap = tableMap.get(table);
				String tableText = table;
				if (!StringUtils.isEmpty(subMap.get("remarks"))) {
					tableText += " 【" + subMap.get("remarks") + "】";
				}
				DatabaseNode dbNode = DatabaseNode.initNode(tableText, table, "datatable", true);
				dbNode.setSchema(subMap.get("schema"));
				resultList.add(dbNode);
			}

			
			String result = JSONUtil.parseArray(resultList).toString();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(result);
			out.flush();
			out.close();
		} finally {
			db.disconnect();
		}
	}

	/**
	 * 
	 * @param databaseMeta
	 * @param db
	 * @return
	 * @throws KettleDatabaseException
	 */
	public Map<String, Map<String, String>> getTableMap(DatabaseMeta databaseMeta, Database db)
			throws KettleDatabaseException {
		String schemaname = db.environmentSubstitute(databaseMeta.getUsername()).toUpperCase();
		;

		Map<String, Map<String, String>> tableMap = new HashMap<String, Map<String, String>>();
		ResultSet alltables = null;
		try {

			alltables = db.getDatabaseMetaData().getTables(null, schemaname, null, databaseMeta.getTableTypes());
			while (alltables.next()) {
				String cat = "";
				try {
					cat = alltables.getString("TABLE_CAT");
				} catch (Exception e) {
					// ignore
				}

				String schema = "";
				try {
					schema = alltables.getString("TABLE_SCHEM");
				} catch (Exception e) {
					// ignore
				}

				if (Utils.isEmpty(schema)) {
					schema = cat;
				}

				String table = alltables.getString("TABLE_NAME");

				// 获取表注释
				String remarks = alltables.getString("REMARKS");

				Map<String, String> subMap = new HashMap<String, String>();
				subMap.put("remarks", remarks);
				subMap.put("schema", schema);

				tableMap.put(table, subMap);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (alltables != null) {
					alltables.close();
				}
			} catch (SQLException e) {
				throw new KettleDatabaseException(
						"Error closing resultset after getting views from schema [" + schemaname + "]", e);
			}
		}

		return tableMap;
	}

	/**
	 * 
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	protected void add(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			UserGroupAttributeEntity userInfo = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
			
			// 添加 - -返回结果
			Map<String, Object> resultMap = dataPushService.add(userInfo, request);
			
			String json = JSONUtil.parseObj(resultMap).toString();
	
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("添加失败!");
		}
	}
	
    //删除
    @RequestMapping(value="/delete")
    @ResponseBody
    protected void delete(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try {
            String id=request.getParameter("id");
            Map<String, Object> resultMap = dataPushService.delete(id);
            
            String json = JSONUtil.parseObj(resultMap).toString();
            
            response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @RequestMapping(value="/update")
    @ResponseBody
    protected void update(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try {
            Map<String, Object> resultMap = dataPushService.update(request);
            
            String json = JSONUtil.parseObj(resultMap).toString();
            
            response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }	    
    
    /**
     * 
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value="/operateStatus")
    @ResponseBody
    protected void operateStatus(HttpServletResponse response,HttpServletRequest request) throws Exception{
    	UserGroupAttributeEntity userInfo = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
    	try {
    		String userId = userInfo.getUserName();
    		
    		String id=request.getParameter("id");
    		String status=request.getParameter("status");
    		Map<String, Object> resultMap = dataPushService.operateStatus(id, status, userId);
    		
    		String json = JSONUtil.parseObj(resultMap).toString();
    		
    		response.setContentType("text/html;charset=utf-8");
    		PrintWriter out = response.getWriter();
    		out.write(json);
    		out.flush();
    		out.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new Exception(e.getMessage());
    	}
    }    
    
    /**
     * 
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value="/getUsers")
    @ResponseBody
    protected void getUsers(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{
            UserGroupAttributeEntity attr=(UserGroupAttributeEntity)request.getSession().getAttribute("userInfo");
            String userGroupId = attr.getUserGroupId();
            
			List<Map> resultList = dataPushService.getUsers(userGroupId);
  
            String result = JSONUtil.parseArray(resultList).toString();

            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }    
}
