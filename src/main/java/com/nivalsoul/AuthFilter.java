package com.nivalsoul;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

/**
 * Servlet Filter implementation class SysFilter
 */
public class AuthFilter implements Filter {

    /**
     * Default constructor. 
     */
    public AuthFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		HttpSession session = req.getSession();
		Object userId = session.getAttribute("userId");
		if(userId!=null){
			request.setCharacterEncoding("UTF8");
			request.setAttribute("userId", userId.toString());
			request.setAttribute("userName", session.getAttribute("userName"));
			request.setAttribute("userAccount", session.getAttribute("userAccount"));
			chain.doFilter(request, response);
		}else{
			System.out.println("当前用户没有登录！");
			responseAuthFailure(res, 401, "need login!");
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	private void responseAuthFailure(HttpServletResponse response, int status, String message) throws IOException{
		  response.setStatus(status);
		  response.setContentType("application/json;charset=utf-8");
		  PrintWriter writer = response.getWriter();
		  Map<String, Object> respMap = Maps.newHashMap();
		  respMap.put("code", status);
		  respMap.put("message", message);
		  writer.write(JSON.toJSONString(respMap));
		  writer.flush();
  }

}
