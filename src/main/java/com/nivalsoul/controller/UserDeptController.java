package com.nivalsoul.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.nivalsoul.model.Dept;
import com.nivalsoul.model.User;
import com.nivalsoul.model.UserDept;
import com.nivalsoul.service.DeptService;
import com.nivalsoul.service.PermissionService;
import com.nivalsoul.service.UserDeptService;
import com.nivalsoul.service.UserService;

@Controller
@RequestMapping("/v1/userDepts")
public class UserDeptController {
	private static final Logger log = Logger.getLogger(UserDeptController.class);
	
	@Autowired
	private UserDeptService userDeptService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value="/", method = RequestMethod.GET)  
    @ResponseBody
    public Object list(@RequestParam(value="userid", required=false) Integer userid,
    		@RequestParam(value="deptid", required=false) Integer deptid){
		if(userid!=null)
			return userDeptService.findByUserid(userid);
		if(deptid!=null)
			return userDeptService.findByDeptid(deptid);
        return userDeptService.list();  
    }  
	
	/**
	 * 获取当前用户所属的部门列表,如果是管理员则获取所有部门列表
	 * @param userid
	 * @return
	 */
	@RequestMapping(value="/depts", method = RequestMethod.GET)  
	@ResponseBody
	public Object getDeptsByUserid(HttpServletRequest request){
		String userId = (String) request.getAttribute("userId");
		List<Dept> depts = permissionService.geDeptsByUserId(userId);
		Map<String, Object> info = new HashMap<String, Object>();
    	info.put("code", 200);
		info.put("data", depts);
		return info;  
	}  
	
	@RequestMapping(value="/users", method = RequestMethod.GET)  
	@ResponseBody
	public Object getUsersByDeptid(@RequestParam(value="deptid") Integer deptid){
		List<User> users = Lists.newArrayList();
		List<UserDept> list = userDeptService.findByDeptid(deptid);
		for (UserDept userDept : list) {
			users.add(userService.findById(userDept.getUserid()));
		}  
		Map<String, Object> info = new HashMap<String, Object>();
    	info.put("code", 200);
		info.put("data", list);
		return info;  
	}  
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)  
    @ResponseBody
    public Object get(@PathVariable("id") int id){
        return userDeptService.findById(id);
    }  
	
	@RequestMapping(value="/", method = RequestMethod.POST)  
	@ResponseBody
    public String add(@RequestParam("userid") Integer userid,
    		@RequestParam("deptid") Integer deptid){
        UserDept data = new UserDept();
        data.setUserid(userid);
        data.setDeptid(deptid);
		userDeptService.add(data);
        return "ok";
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)  
	@ResponseBody
    public String delete(@PathVariable("id") int id){  
        userDeptService.delete(id);
        return "ok";
    }

}
