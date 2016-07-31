package com.nivalsoul.controller;

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

}
