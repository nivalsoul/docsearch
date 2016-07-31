package com.nivalsoul.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nivalsoul.model.Role;
import com.nivalsoul.service.RoleService;

@Controller
@RequestMapping("/roles")
public class RoleController {
	private static final Logger log = Logger.getLogger(RoleController.class);
	
	@Autowired
	private RoleService roleService;
	
	@RequestMapping(value="/", method = RequestMethod.GET)  
    @ResponseBody
    public Object list(){
        return roleService.list();  
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)  
    @ResponseBody
    public Object get(@PathVariable("id") int id){
        return roleService.findById(id);
    }  
	
	@RequestMapping(value="/", method = RequestMethod.POST)  
	@ResponseBody
    public String add(@RequestBody Role data){
		data.setRoleid(null);
        roleService.add(data);
        return "ok";
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)  
	@ResponseBody
	@Transactional
    public String delete(@PathVariable("id") int id){  
        roleService.delete(id);
        return "ok";
    }

}
