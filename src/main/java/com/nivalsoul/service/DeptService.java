package com.nivalsoul.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nivalsoul.dao.DeptDao;
import com.nivalsoul.model.Dept;

@Service
public class DeptService {
	
	@Autowired
	private DeptDao deptDao;
	
	@Autowired
	private UserDeptService userDeptService;
	
	public Iterable<Dept> list() {
		return deptDao.findAll();
	}
	
	public Dept findById(int deptid) {
		return deptDao.findOne(deptid);
	}
	
	public Object add(Dept dept){
		return deptDao.save(dept);
	}
	
	public Object update(Dept dept) {
		return deptDao.save(dept);
	}
	
	public void delete(int deptid) {
		deptDao.delete(deptid);
		userDeptService.deleteByDeptid(deptid);
	}

}
