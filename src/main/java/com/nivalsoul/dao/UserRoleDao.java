package com.nivalsoul.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.nivalsoul.model.UserRole;

@Transactional
public interface UserRoleDao extends PagingAndSortingRepository<UserRole, Integer> {

	public List<UserRole> findByUserid(int userid);
	
	public List<UserRole> findByRoleid(int UserRole);

}
