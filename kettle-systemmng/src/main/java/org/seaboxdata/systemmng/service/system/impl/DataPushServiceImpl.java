package org.seaboxdata.systemmng.service.system.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seaboxdata.systemmng.bean.PageforBean;
import org.seaboxdata.systemmng.dao.DataPushDao;
import org.seaboxdata.systemmng.entity.UserGroupAttributeEntity;
import org.seaboxdata.systemmng.service.system.DataPushService;
import org.seaboxdata.systemmng.utils.common.StringDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import net.sf.json.JSONObject;

/**
 * 
 * @author 管理员
 *
 */
@Service
public class DataPushServiceImpl implements DataPushService {
	@Autowired
	private DataPushDao dataPushDao;

	/**
	 * 
	 */
	@Override
	public String getList(int start, int limit, HttpServletRequest request) throws Exception {
		// 获取当前用户所在的用户组
		UserGroupAttributeEntity userAttribute = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
		
		String status = request.getParameter("status");
		String name = request.getParameter("name");
		String userGroupName = userAttribute.getUserGroupId();
		String username = userAttribute.getUserName();
		Integer userType = userAttribute.getUserType();
		
		// 获取用户集合总记录数
		List<Map<String, Object>> list = new ArrayList<>();
		Integer count = dataPushDao.getCount(userGroupName, username, userType, status, name);
		list = dataPushDao.getList(start, limit, userGroupName, username, userType, status, name);

		PageforBean bean = new PageforBean();
		bean.setRoot(list);
		bean.setTotalProperty(count);
		return JSONObject.fromObject(bean, StringDateUtil.configJson("yyyy-MM-dd HH:mm:ss")).toString();
	}
	
	/**
	 * 
	 */
	@Override
	public String getApprovalList(int start, int limit, HttpServletRequest request) throws Exception {
		// 获取当前用户所在的用户组
		UserGroupAttributeEntity userAttribute = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
		
		String status = request.getParameter("status");
		String name = request.getParameter("name");
		String userId = request.getParameter("userId");
		
		String userGroupName = userAttribute.getUserGroupId();
		
		// 获取用户集合总记录数
		List<Map<String, Object>> list = new ArrayList<>();
		Integer count = dataPushDao.getApprovalCount(userGroupName, status, name, userId);
		list = dataPushDao.getApprovalList(start, limit, userGroupName, status, name, userId);
		
		PageforBean bean = new PageforBean();
		bean.setRoot(list);
		bean.setTotalProperty(count);
		return JSONObject.fromObject(bean, StringDateUtil.configJson("yyyy-MM-dd HH:mm:ss")).toString();
	}

	@Override
	public Map<String, Object> add(UserGroupAttributeEntity userInfo, HttpServletRequest request) throws Exception {
		String userId = userInfo.getUserName();
		String userGroupId = userInfo.getUserGroupId();
		// 接收参数
		String name = request.getParameter("name");
		String desc = request.getParameter("desc");
		String sourceData = request.getParameter("sourceData");
		String sourceTable = request.getParameter("sourceTable");
		String targetData = request.getParameter("targetData");
		String targetTable = request.getParameter("targetTable");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("desc", desc);
		params.put("sourceData", sourceData);
		params.put("sourceTable", sourceTable);
		params.put("targetData", targetData);
		params.put("targetTable", targetTable);
		params.put("userGroupId", userGroupId);
		params.put("createUser", userId);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, String> params1 = new HashMap<String, String>();
			params1.put("name", name);
			List<Map<String, Object>> list = dataPushDao.queryByParams(params1);
			if(list.size() > 0) {
				resultMap.put("success", false);
				resultMap.put("msg", "任务名称已存在！");
			} else {
				dataPushDao.add(params) ;
				resultMap.put("success", true);
				resultMap.put("msg", "添加成功");
			}
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("msg", "添加失败:" + e.getMessage());
		}
		
		return resultMap;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> delete(String id) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", id);
			List<Map<String, Object>> list = dataPushDao.queryByParams(params);
			if(list.size() > 1) {
				resultMap.put("success", false);
				resultMap.put("msg", "数据重复,请检查！");
			} else {
				Map map = list.get(0);
				String status = (String)map.get("STATUS");
				if(!"0".equals(status)) {
					resultMap.put("success", false);
					resultMap.put("msg", "不是待提交数据,无法删除!");
				} else {
					dataPushDao.delete(params) ;
					resultMap.put("success", true);
					resultMap.put("msg", "删除成功!");
				}
			}
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("msg", "删除失败:" + e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> update(HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String id = request.getParameter("id");
			String name = request.getParameter("name");
			String description = request.getParameter("description");
			String sourceDataName = request.getParameter("sourceDataCombobox");
			String sourceTableName = request.getParameter("sourceTableCombobox");
			String targetDataName = request.getParameter("targetDataCombobox");
			String targetTableName = request.getParameter("targetTableCombobox");
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", id);
			List<Map<String, Object>> list = dataPushDao.queryByParams(params);
			if(list.size() > 1) {
				resultMap.put("success", false);
				resultMap.put("msg", "数据重复,请检查！");
			} else {
				Map map = list.get(0);
				String status = (String)map.get("STATUS");
				if(!"0".equals(status)) {
					resultMap.put("success", false);
					resultMap.put("msg", "不是待提交数据,不能进行修改!");
				} else {
					params.remove("id");
					params.put("name", name);
					list = dataPushDao.queryByParams(params);
					boolean flag = false;
					
					for (Map<String, Object> map2 : list) {
						String _id= map2.get("ID").toString();
						if(!_id.equals(id)) {
							flag = true;
							break;
						}
					}
					
					if(flag) {
						resultMap.put("success", false);
						resultMap.put("msg", "任务名称已存在,请重新填写！" );
					} else {
						params.put("id", id);
						params.put("name", name);
						params.put("description", description);
						params.put("source_data_name", sourceDataName);
						params.put("source_table_name", sourceTableName);
						params.put("target_data_name", targetDataName);
						params.put("target_table_name", targetTableName);
						
						dataPushDao.update(params);
						
						resultMap.put("success", true);
						resultMap.put("msg", "修改成功!");
					}
				}
			}
		} catch (Exception e) {
			resultMap.put("success", false);
			resultMap.put("msg", "修改失败:" + e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> operateStatus(String id, String status, String userId) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("status", status);
		
		boolean success = true;
		String msg = "操作成功!";
		try {
			List<Map<String, Object>> list = dataPushDao.queryByParams(params);
			if(list.size() > 1) {
				success = false;
				msg = "数据重复,请检查！";
			} else {
				Map map = list.get(0);
				String _status = (String) map.get("STATUS");
				if("1".equals(status)) {//提交
					if(!_status.equals("0") && !_status.equals("3")) {
						success = false;
						msg = "当前数据无法提交,请检查！";
					} else {
						String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");	
						params.put("application_date", format);
					}
				} else if("2".equals(status) || "3".equals(status)) {// 审批通过  审批驳回
					if(!_status.equals("1")) {
						success = false;
						msg = "当前数据无法审核,请检查！";
					} else {
						String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");	
						params.put("approval_date", format);
						params.put("approval_user", userId);
					}
					
				}  else if("0".equals(status)) {// 撤回
					if(!_status.equals("1")) {
						success = false;
						msg = "当前数据无法撤回,请检查！";
					}
				}
				
				if(success) {
					dataPushDao.operateStatus(params);
				}
			}
		} catch (Exception e) {
			success = false;
			msg = "操作失败:" + e.getMessage();
		}
		
		resultMap.put("success", success);
		resultMap.put("msg", msg);
		return resultMap;
	}

	/**
	 * 
	 */
	@Override
	public List<Map> getUsers(String userGroupId) throws Exception {
		return dataPushDao.getUsers(userGroupId);
	}

}
