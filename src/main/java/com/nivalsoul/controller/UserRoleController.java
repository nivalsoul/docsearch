package com.nivalsoul.controller;

import java.util.List;

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
import com.nivalsoul.model.Role;
import com.nivalsoul.model.User;
import com.nivalsoul.model.UserDept;
import com.nivalsoul.model.UserRole;
import com.nivalsoul.service.RoleService;
import com.nivalsoul.service.UserRoleService;
import com.nivalsoul.service.UserService;

@Controller
@RequestMapping("/v1/userRoles")
public class UserRoleController {
	private static final Logger log = Logger.getLogger(UserRoleController.class);
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@RequestMapping(value="/", method = RequestMethod.GET)  
    @ResponseBody
    public Object list(@RequestParam(value="userid", required=false) Integer userid,
    		@RequestParam(value="roleid", required=false) Integer roleid){
		if(userid!=null)
			return userRoleService.findByUserid(userid);
		if(roleid!=null)
			return userRoleService.findByRoleid(roleid);
        return userRoleService.list();  
    }  
	
	@RequestMapping(value="/roles", method = RequestMethod.GET)  
	@ResponseBody
	public Object getRolesByUserid(@RequestParam(value="userid") Integer userid){
		List<Role> roles = Lists.newArrayList();
		List<UserRole> list = userRoleService.findByUserid(userid);
		for (UserRole item : list) {
			roles.add(roleService.findById(item.getRoleid()));
		}
		return roles;  
	}  
	
	@RequestMapping(value="/users", method = RequestMethod.GET)  
	@ResponseBody
	public Object getUsersByRoleid(@RequestParam(value="roleid") Integer roleid){
		List<User> users = Lists.newArrayList();
		List<UserRole> list = userRoleService.findByRoleid(roleid);
		for (UserRole item : list) {
			users.add(userService.findById(item.getUserid()));
		}  
		return users;
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)  
    @ResponseBody
    public Object get(@PathVariable("id") int id){
        return userRoleService.findById(id);
    }  
	
	@RequestMapping(value="/", method = RequestMethod.POST)  
	@ResponseBody
    public String add(@RequestParam("userid") Integer userid,@RequestParam("roleid") Integer roleid){
        UserRole data = new UserRole();
        data.setUserid(userid);
        data.setRoleid(roleid);
		userRoleService.add(data);
        return "ok";
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)  
	@ResponseBody
    public String delete(@PathVariable("id") int id){  
        userRoleService.delete(id);
        return "ok";
    }

}
