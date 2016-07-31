package com.nivalsoul.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "user_role")
public class UserRole implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column
	private Integer userid;
	@Column
	private Integer roleid;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setUserid(Integer userid){
		this.userid = userid;
	}
	public Integer getUserid(){
		return this.userid;
	}

	public void setRoleid(Integer roleid){
		this.roleid = roleid;
	}
	public Integer getRoleid(){
		return this.roleid;
	}
}
