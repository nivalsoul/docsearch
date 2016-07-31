package com.nivalsoul.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nivalsoul.dao.UserRoleDao;
import com.nivalsoul.model.UserRole;

@Service
public class UserRoleService {
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	public Iterable<UserRole> list() {
		return userRoleDao.findAll();
	}
	
	public UserRole findById(int id) {
		return userRoleDao.findOne(id);
	}
	
	public List<UserRole> findByUserid(int userid) {
		return userRoleDao.findByUserid(userid);
	}
	
	public List<UserRole> findByRoleid(int roleid) {
		return userRoleDao.findByRoleid(roleid);
	}
	
	public Object add(UserRole data){
		return userRoleDao.save(data);
	}
	
	public Object update(UserRole data) {
		return userRoleDao.save(data);
	}
	
	public void delete(int id) {
		userRoleDao.delete(id);
	}
	
	public void deleteByUserid(int userid) {
		userRoleDao.delete(userRoleDao.findByUserid(userid));
	}
	
	public void deleteByRoleid(int roleid) {
		userRoleDao.delete(userRoleDao.findByRoleid(roleid));
	}

}
