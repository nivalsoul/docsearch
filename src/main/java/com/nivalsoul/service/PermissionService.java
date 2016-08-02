package com.nivalsoul.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nivalsoul.model.Dept;
import com.nivalsoul.model.Role;
import com.nivalsoul.model.User;
import com.nivalsoul.model.UserDept;
import com.nivalsoul.model.UserRole;

@Service
public class PermissionService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DeptService deptService;
	
	@Autowired
	private UserDeptService userDeptService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	public User getUserById(String userId) {
		return userService.findById(Integer.parseInt(userId));
	}
	
	public List<User> getUsers() {
		List<User> list = new ArrayList<User>();
		Iterable<User> users = userService.list();
		for (User user : users) {
			list.add(user);
		}
		return list;
	}

	
	/**
	 * 获取当前用户所属的部门列表,如果是管理员则获取所有部门列表
	 * @param userId
	 * @return
	 */
	public List<Dept> geDeptsByUserId(String userId) {
		if(isDataAdmin(userId)){//如果是管理员则获取该子系统所有租户列表
			return Lists.newArrayList(deptService.list());
		}else{
			List<Dept> depts = new ArrayList<Dept>();
			for (UserDept ud : userDeptService.findByUserid(Integer.parseInt(userId))) {
				depts.add(deptService.findById(ud.getDeptid()));
			}
			return depts;
		}
	}

	
	/**
	 * 判断用户是否是DataAdmin
	 * @param userId
	 * @return
	 */
	public boolean isDataAdmin(String userId) {
		for (UserRole ur : userRoleService.findByUserid(Integer.parseInt(userId))) {
			Role role = roleService.findById(ur.getRoleid());
			if(role.getRolename().equals("DataAdmin")){
				return true;
			}
		}
		
		return false;
	}
}
