package com.example.demo.pass.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.pass.model.Member;
import com.example.demo.pass.model.MemberRepository;

// 功能: 只要給他使用者名稱，Service 就會抓取資料庫中要驗證的該筆資料的帳號、密碼、腳色，再包成 User 類別回傳。

// 在登入按鈕按下之後到 JwtAuthenticationController 中，會從 authenticationManager 的方法中呼叫此 Service。  
// 這邊執行完，回到 JwtAuthenticationController 中，並使用拿到的資料到config檔進行比對。
// 必須實作介面 UserDetailsService。
@Service
public class ArpUserDetailsService implements UserDetailsService{

	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		
		System.out.println("這是比對帳密的Service");
		
		Member member = memberRepository.findByName(name);
		
		if(member == null) {
			System.err.println("fu帳密錯誤");
		}
		
		
		// 建立權限:
		// 資料庫中 role 欄位是 A 給腳色"ADMIN"，欄位是 B 給腳色"USER"
		// 權限會存到動態陣列 authorities 中
		Collection<GrantedAuthority> authorities = null;
		String role = member.getRole();
		
		if( role.equals("A") ) {
			
			// role 欄位是 A 給腳色"ADMIN"
			authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");
			
		}else if( role.equals("B") ) {
			
			// role 欄位是 B 給腳色"USER"
			authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
			
			//不要用 AuthorityUtils.commaSeparatedStringToAuthorityList("USER") 這種寫法，會爆

		}else {
			
			// role 欄位在預期外，給空的動態陣列(無腳色)
			System.out.println("腳色未知");
			authorities = Collections.emptyList();
			
		}
		
		
		return new User(member.getName(),member.getPaasword(),authorities);
		// 第三個參數為權限設定。如果沒有設定權限，使用Collections.emptyList() : 空的動態陣列
		
	}
	
	
	
}
