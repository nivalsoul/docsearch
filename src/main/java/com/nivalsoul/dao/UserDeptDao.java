package com.nivalsoul.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.nivalsoul.model.UserDept;

@Transactional
public interface UserDeptDao extends PagingAndSortingRepository<UserDept, Integer> {

	public List<UserDept> findByUserid(int userid);
	
	public List<UserDept> findByDeptid(int tenantid);

}
