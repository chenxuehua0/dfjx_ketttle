package org.seaboxdata.platform.controller.system;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seaboxdata.systemmng.entity.ResourceEntity;
import org.seaboxdata.systemmng.entity.UserGroupAttributeEntity;
import org.seaboxdata.systemmng.service.system.UserService;
import org.seaboxdata.systemmng.utils.ResourceTreeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/resource")
public class ResourceController {

    @Autowired
    UserService userService;
	
	@RequestMapping(value = "/queryResource")
	@ResponseBody
	protected List<ResourceEntity> queryResource(HttpServletResponse response, HttpServletRequest request) throws Exception {
		try {
			UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
			
			String userName = attr.getUserName();
			
			List<ResourceEntity> list = userService.queryResource(userName);
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
