package com.nivalsoul.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "user")
public class User implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userid;
	@Column
	private String username;
	@Column
	private String email;
	@Column
	private String password;

	public void setUserid(Integer userid){
		this.userid = userid;
	}
	public Integer getUserid(){
		return this.userid;
	}

	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return this.username;
	}

	public void setEmail(String email){
		this.email = email;
	}
	public String getEmail(){
		return this.email;
	}

	public void setPassword(String password){
		this.password = password;
	}
	public String getPassword(){
		return this.password;
	}
}
