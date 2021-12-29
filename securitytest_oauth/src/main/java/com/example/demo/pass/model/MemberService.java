package com.example.demo.pass.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
	
	@Autowired
	private MemberRepository memberRepository;
	
	// 找第一筆資料
	public Optional<Member> getOneMember() {
		Optional<Member> findById = memberRepository.findById(1);
		return findById;
	}
	
	// 用名稱找資料
	public Member getOneMemberByName(String name) {
		Member findByName = memberRepository.findByName(name);
		return findByName;
	}
	
	// 建立一筆帳號
	public Member insertOneMember(Member member) {
		Member member2 = memberRepository.save(member);
		return member2;
	}
	
	
	
	
}
