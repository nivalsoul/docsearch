package com.nivalsoul.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nivalsoul.model.Role;
import com.nivalsoul.model.UserRole;
import com.nivalsoul.service.RoleService;
import com.nivalsoul.service.UserRoleService;

@Controller
@RequestMapping("/userRoles")
public class UserRoleController {
	private static final Logger log = Logger.getLogger(UserRoleController.class);
	
	@Autowired
	private UserRoleService userRoleService;
	
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
