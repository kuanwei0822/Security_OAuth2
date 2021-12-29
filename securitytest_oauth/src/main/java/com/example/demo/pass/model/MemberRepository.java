package com.example.demo.pass.model;

import org.springframework.data.jpa.repository.JpaRepository;



public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	// 自定義查詢 用帳號查一筆資料
	Member findByName(String name);
	
}
