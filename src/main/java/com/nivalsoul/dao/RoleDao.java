package com.nivalsoul.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.nivalsoul.model.Role;

@Transactional
public interface RoleDao extends PagingAndSortingRepository<Role, Integer> {
	

}
