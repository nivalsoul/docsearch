package com.nivalsoul.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "role")
public class Role implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer roleid;
	@Column
	private String rolename;

	public void setRoleid(Integer roleid){
		this.roleid = roleid;
	}
	public Integer getRoleid(){
		return this.roleid;
	}

	public void setRolename(String rolename){
		this.rolename = rolename;
	}
	public String getRolename(){
		return this.rolename;
	}
}
