package com.nivalsoul.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.nivalsoul.model.User;

@Transactional
public interface UserDao extends PagingAndSortingRepository<User, Integer> {

	public User findByEmail(String email);
	
	public User findByUsername(String username);

}
