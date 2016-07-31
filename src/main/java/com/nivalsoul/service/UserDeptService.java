package com.nivalsoul.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nivalsoul.dao.UserDeptDao;
import com.nivalsoul.model.UserDept;

@Service
public class UserDeptService {
	
	@Autowired
	private UserDeptDao userDeptDao;
	
	public Iterable<UserDept> list() {
		return userDeptDao.findAll();
	}
	
	public UserDept findById(int id) {
		return userDeptDao.findOne(id);
	}
	
	public List<UserDept> findByUserid(int userid) {
		return userDeptDao.findByUserid(userid);
	}
	
	public List<UserDept> findByDeptid(int tenantid) {
		return userDeptDao.findByDeptid(tenantid);
	}
	
	public Object add(UserDept data){
		return userDeptDao.save(data);
	}
	
	public Object update(UserDept data) {
		return userDeptDao.save(data);
	}
	
	public void delete(int id) {
		userDeptDao.delete(id);
	}
	
	public void deleteByUserid(int userid) {
		userDeptDao.delete(userDeptDao.findByUserid(userid));
	}
	
	public void deleteByDeptid(int deptid) {
		userDeptDao.delete(userDeptDao.findByDeptid(deptid));
	}


}
