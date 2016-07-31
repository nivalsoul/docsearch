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

import com.nivalsoul.model.Dept;
import com.nivalsoul.service.DeptService;

@Controller
@RequestMapping("/depts")
public class DeptController {
	private static final Logger log = Logger.getLogger(DeptController.class);
	
	@Autowired
	private DeptService deptService;
	
	@RequestMapping(value="/", method = RequestMethod.GET)  
    @ResponseBody
    public Object list(){
        return deptService.list();  
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)  
    @ResponseBody
    public Object get(@PathVariable("id") int id){
        return deptService.findById(id);
    }  
	
	@RequestMapping(value="/", method = RequestMethod.POST)  
	@ResponseBody
    public String add(@RequestBody Dept data){
		data.setDeptid(null);
        deptService.add(data);
        return "ok";
    }  
	
	@RequestMapping(value="/{id}", method = RequestMethod.DELETE)  
	@ResponseBody
	@Transactional
    public String delete(@PathVariable("id") int id){  
        deptService.delete(id);
        return "ok";
    }

}
