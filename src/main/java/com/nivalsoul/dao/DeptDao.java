package com.nivalsoul.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.nivalsoul.model.Dept;

@Transactional
public interface DeptDao extends PagingAndSortingRepository<Dept, Integer> {
	

}
