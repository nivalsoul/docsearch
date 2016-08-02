package com.nivalsoul.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.nivalsoul.model.User;
import com.nivalsoul.service.UserService;
import com.nivalsoul.utils.MD5;

@Controller
@RequestMapping("/users")
public class UserController {
	private static final Logger log = Logger.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/", method = RequestMethod.GET)  
    @ResponseBody
    public Object list(@RequestParam(value="email", required=false) String email, 
    		@RequestParam(value="username", required=false) String username){
		if(!Strings.isNullOrEmpty(email))
			return userService.findByEmail(email);
		if(!Strings.isNullOrEmpty(username))
			return userService.findByUsername(username);
        return userService.list();  
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)  
    @ResponseBody
    public Object get(@PathVariable("id") int userid){
        return userService.findById(userid);
    }  
	
	@RequestMapping(value="/", method = RequestMethod.POST)  
	@ResponseBody
    public String add(@RequestBody User user){
		user.setUserid(null);
		user.setPassword(MD5.getHashString(user.getPassword()));
        userService.add(user);
        return "ok";
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)  
	@ResponseBody
	@Transactional
    public String delete(@PathVariable("id") int userid){  
        userService.delete(userid);
        return "ok";
    }
	
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @param useraccount
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/user/login", method = RequestMethod.POST)  
	@ResponseBody
	@Transactional
    public Map<String, Object> login(
    		HttpServletRequest request,HttpServletResponse response,
    		@RequestParam(value = "email") String email, 
			@RequestParam(value = "password") String password){   
		log.info("user: "+email+" login...");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", 0);
		result.put("info", "登录成功");
		User userinfo = userService.findByEmail(email);
		if(userinfo ==null){
			result.put("code", 1);
			result.put("info", "用户不存在");
			return result;
		}
		if(!userinfo.getPassword().equals(MD5.getHashString(password))){
			result.put("code", 2);
			result.put("info", "密码错误");
			return result;
		}
		//设置session和cookie
		setSessionCookie(request, response, userinfo);
	    //更新登录信息
		
		
        return result;
    }
	
	/**
	 * 注销
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/user/logout", method = RequestMethod.POST)  
	@ResponseBody
    public String logout(HttpServletRequest request){   
		//设置session
	    HttpSession session = request.getSession();
		log.info("user: "+session.getAttribute("userAccount")+" logout...");
		session.removeAttribute("userName");
		session.removeAttribute("userAccount");
		session.removeAttribute("userId");
	    
        return "ok";
    }

	private void setSessionCookie(HttpServletRequest request,
			HttpServletResponse response, User userinfo) {
		//设置session
		HttpSession session = request.getSession();
		session.setAttribute("userId", userinfo.getUserid());
		session.setAttribute("userName",userinfo.getUsername());
		session.setAttribute("userAccount",userinfo.getEmail());
		session.setMaxInactiveInterval(3*3600);
		//设置cookie
		Cookie cookie = new Cookie("email",userinfo.getEmail());
		// 不设置的话，则cookies不写入硬盘,而是写在内存,只在当前页面有用,以秒为单位
		cookie.setMaxAge(3*3600);
		//设置路径，这个路径即该工程下都可以访问该cookie 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
		cookie.setPath("/");
		response.addCookie(cookie);
		try {
			cookie = new Cookie("userName", URLEncoder.encode(userinfo.getUsername(), "UTF-8"));
			cookie.setMaxAge(3*3600);
			cookie.setPath("/");
			response.addCookie(cookie);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}  

}
