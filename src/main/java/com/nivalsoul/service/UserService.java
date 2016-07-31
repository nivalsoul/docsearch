package com.nivalsoul.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nivalsoul.dao.UserDao;
import com.nivalsoul.model.User;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserDeptService userDeptService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	public Iterable<User> list() {
		return userDao.findAll();
	}
	
	public User findById(int userid) {
		return userDao.findOne(userid);
	}
	
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}
	
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}
	
	public Object add(User user){
		return userDao.save(user);
	}
	
	public Object update(User user) {
		return userDao.save(user);
	}
	
	public void delete(int userid) {
		userDao.delete(userid);
		userDeptService.deleteByUserid(userid);
		userRoleService.deleteByUserid(userid);
	}

}
