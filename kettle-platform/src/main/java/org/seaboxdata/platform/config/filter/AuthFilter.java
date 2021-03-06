package org.seaboxdata.platform.config.filter;



import org.apache.commons.lang.StringUtils;
import org.seaboxdata.systemmng.auth.vo.OnlineUser;
import org.seaboxdata.systemmng.entity.UserEntity;
import org.seaboxdata.systemmng.service.auth.AuthService;
import org.seaboxdata.systemmng.utils.auth.CookieUtils;
import org.seaboxdata.systemmng.utils.auth.PropertiesUtil;
import org.seaboxdata.systemmng.utils.auth.SpringContextHolder;
import org.seaboxdata.systemmng.utils.auth.UserThreadLocal;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

/**
 * 登录验证过滤器
 */
public class AuthFilter implements Filter {

	private String excludedPages;       
	private String[] excludedArray;
	private static String gotoAuth = new PropertiesUtil("config/environment.properties").readProperty("gotoAuth");
	private static String returnBack = new PropertiesUtil("config/environment.properties").readProperty("returnBack");


	@Override
	public void init(FilterConfig fConfig) throws ServletException {     
		excludedPages = fConfig.getInitParameter("excludedPages");     
		if (StringUtils.isNotBlank(excludedPages)) {
			excludedArray = excludedPages.split(",");     
		}     
		return;     
	}     

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

		System.out.println("---------------url-----------------------" + ((HttpServletRequest) servletRequest).getRequestURL());

		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		String uri = request.getRequestURI();
		String url = request.getRequestURL().toString();
		for(String page : excludedArray){
			if(uri.indexOf(page) > 0){
				chain.doFilter(servletRequest, servletResponse);
				return ;
			}
		}

		if (((HttpServletRequest) servletRequest).getRequestURL().indexOf("fromAuth") > 0) {
			chain.doFilter(servletRequest, servletResponse);
			return ;
		}

		String token = CookieUtils.GetToken(request);
		if(StringUtils.isBlank(token)){
			response.sendRedirect(this.goUrl(url));
			return ;
		}
		AuthService authService = SpringContextHolder.getBean("authService");

		OnlineUser user = authService.nowOnlineUser(token, request, response);
		if(null == user){
			response.sendRedirect(this.goUrl(url));
			return ;
		}
		user.setToken(token);

		Set<String> permissions = authService.selectPermissionsByUserIdAndSystemToSet(user.getUserId());
		user.setPermissions(permissions);

		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(user.getUserId()+"");
		userEntity.setLogin(user.getUsername());
		//request.getSession().setAttribute("login", userEntity);
		UserThreadLocal.set(user);
		
		chain.doFilter(servletRequest, servletResponse);
	}

	private String goUrl(String url) throws UnsupportedEncodingException {
		String burl = returnBack + "?backUrl=" + URLEncoder.encode(url, "UTF-8");
		return gotoAuth + URLEncoder.encode(burl, "UTF-8");
	}

	@Override
	public void destroy() {
		UserThreadLocal.remove();
	}

}