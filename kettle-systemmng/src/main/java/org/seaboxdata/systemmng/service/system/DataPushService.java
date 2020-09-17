package org.seaboxdata.systemmng.service.system;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seaboxdata.systemmng.entity.UserGroupAttributeEntity;

/**
 * 
 * @author 管理员
 *
 */
public interface DataPushService {

	/**
	 * 
	 * @param start
	 * @param limit
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String getList(int start, int limit, HttpServletRequest request) throws Exception;

	/**
	 * 
	 * @param start
	 * @param limit
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String getApprovalList(int start, int limit, HttpServletRequest request) throws Exception;

	/**
	 * 
	 * @param userInfo
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> add(UserGroupAttributeEntity userInfo, HttpServletRequest request) throws Exception;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> delete(String id) throws Exception;

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> update(HttpServletRequest request) throws Exception;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> operateStatus(String id, String status, String userId) throws Exception;

	/**
	 * 
	 * @param userGroupId
	 * @return
	 * @throws Exception
	 */
	public List<Map> getUsers(String userGroupId) throws Exception;

}
