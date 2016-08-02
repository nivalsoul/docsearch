package com.nivalsoul.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nivalsoul.dao.RoleDao;
import com.nivalsoul.model.Role;

@Service
public class RoleService {
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private UserRoleService userRoleService;
	
	public Iterable<Role> list() {
		return roleDao.findAll();
	}
	
	public Role findById(int roleid) {
		return roleDao.findOne(roleid);
	}
	
	public Object add(Role role){
		return roleDao.save(role);
	}
	
	public Object update(Role role) {
		return roleDao.save(role);
	}
	
	public void delete(int roleid) {
		roleDao.delete(roleid);
		userRoleService.deleteByRoleid(roleid);
	}

}
