package com.nivalsoul.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dept")
public class Dept implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer deptid;
	@Column
	private String deptname;

	public void setDeptid(Integer deptid){
		this.deptid = deptid;
	}
	public Integer getDeptid(){
		return this.deptid;
	}

	public void setDeptname(String deptname){
		this.deptname = deptname;
	}
	public String getDeptname(){
		return this.deptname;
	}
}
