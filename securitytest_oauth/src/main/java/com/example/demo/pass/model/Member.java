package com.example.demo.pass.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "member")
//@ApiModel(description = "會員資料")
public class Member{

	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name="id")
	private int id;// 會員id囉
	
	@Column(name="name")
	private String name;
	
	@Column(name="password")
	private String paasword;

	@Column(name="role")
	private String role;
	
	
	@Column(name="oauthtype")
	private String oauthtype;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPaasword() {
		return paasword;
	}

	public void setPaasword(String paasword) {
		this.paasword = paasword;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getOauthtype() {
		return oauthtype;
	}

	public void setOauthtype(String oauthtype) {
		this.oauthtype = oauthtype;
	}
	
	
}