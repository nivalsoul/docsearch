package com.nivalsoul.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "user_dept")
public class UserDept implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column
	private Integer userid;
	@Column
	private Integer deptid;

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

	public void setDeptid(Integer deptid){
		this.deptid = deptid;
	}
	public Integer getDeptid(){
		return this.deptid;
	}
}
