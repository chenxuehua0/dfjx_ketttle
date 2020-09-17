package org.seaboxdata.systemmng.dao;

import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;
import org.seaboxdata.systemmng.entity.ResourceEntity;
import org.seaboxdata.systemmng.entity.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 管理员
 *
 */
@Repository
public interface DataPushDao {

	/**
	 * 
	 * @param start
	 * @param limit
	 * @param userGroupName
	 * @param username
	 * @param userType
	 * @param status
	 * @param name
	 * @return
	 */
	public List<Map<String, Object>> getList(@Param("start") int start, @Param("limit") int limit,
			@Param("userGroupName") String userGroupName, @Param("username") String username,
			@Param("userType") Integer userType, @Param("status") String status, @Param("name") String name);

	/**
	 * 
	 * @param userGroupName
	 * @param username
	 * @param userType
	 * @param status
	 * @param name
	 * @return
	 */
	public Integer getCount(@Param("userGroupName") String userGroupName, @Param("username") String username,
			@Param("userType") Integer userType, @Param("status") String status, @Param("name") String name);

	/**
	 * 
	 * @param start
	 * @param limit
	 * @param userGroupName
	 * @param username
	 * @param userType
	 * @param status
	 * @param name
	 * @return
	 */
	public List<Map<String, Object>> getApprovalList(@Param("start") int start, @Param("limit") int limit,
			@Param("userGroupName") String userGroupName, @Param("status") String status, @Param("name") String name,
			@Param("userId") String userId);

	/**
	 * 
	 * @param userGroupName
	 * @param username
	 * @param userType
	 * @param status
	 * @param name
	 * @return
	 */
	public Integer getApprovalCount(@Param("userGroupName") String userGroupName, @Param("status") String status,
			@Param("name") String name, @Param("userId") String userId);

	/**
	 * 
	 * @param params
	 */
	public void add(Map<String, String> params);

	/**
	 * 
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryByParams(Map<String, String> params);

	/**
	 * 
	 * @param params
	 */
	public void delete(Map<String, String> params);

	/**
	 * 
	 * @param params
	 */
	public void update(Map<String, String> params);

	/**
	 * 
	 * @param params
	 */
	public void operateStatus(Map<String, String> params);

	/**
	 * 
	 * @param userGroupId
	 * @return
	 */
	public List<Map> getUsers(@Param("userGorupId")String userGorupId);

}
