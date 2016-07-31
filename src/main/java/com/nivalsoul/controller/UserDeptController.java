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
import com.nivalsoul.model.UserDept;
import com.nivalsoul.model.UserRole;
import com.nivalsoul.service.RoleService;
import com.nivalsoul.service.UserDeptService;
import com.nivalsoul.service.UserRoleService;

@Controller
@RequestMapping("/userDepts")
public class UserDeptController {
	private static final Logger log = Logger.getLogger(UserDeptController.class);
	
	@Autowired
	private UserDeptService userDeptService;
	
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
