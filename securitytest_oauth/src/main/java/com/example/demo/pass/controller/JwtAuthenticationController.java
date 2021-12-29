package com.example.demo.pass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.example.demo.pass.security.ArpUserDetailsService;
import com.example.demo.pass.util.JWTAuthResponse;
import com.example.demo.pass.util.JwtTokenUtil;

import java.util.Collection;
import java.util.Objects;

// 此 Controller 用來驗證帳密，如果通過驗證就會給出一個 token
// 未完成: 帳密錯誤要拋出錯誤

@RestController
public class JwtAuthenticationController {
	
	// authenticationManager 用來產生 authentication 認證物件，裏頭包含用戶資料(指 UserDetails)、權限
	@Autowired
	private AuthenticationManager authenticationManager;
    
	// 使用到 Token 工具
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@PostMapping("/getToken")
	public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestParam String username ,@RequestParam String password){
		
		System.out.println("這裡是驗證帳密，給出token的Controller");
		
		// 將用戶輸入的帳號密碼轉換成 UsernamePasswordAuthenticationToken
		// 由 authenticationManager.authenticate(...) 進行帳戶認證
		
		/* 
		 * 底層操作:
		 * 				
		 * 		authenticationManager.authenticate(...) 方法中
		 * 
		 * 		I. 首先會先呼叫我們寫的 ArpUserDetailsService，查詢正確的帳號、密碼、角色 ，
		 * 			Return User 並把他存在一個叫 loadedUser 的變數中。
		 * 		
		 * 		II. authenticationManager 會建立一包 authentication 類別，
		 * 			裡面包含我們輸入的帳號、密碼、(沒有腳色)
		 * 
		 * 		III. authenticationManager 用兩個分別叫 additionalAuthenticationChecks、postAuthenticationCheck 
		 * 			的比對方法，用來比對 Service 查到的正確帳密 跟 我們輸入的帳密。
		 *   		帳號、密碼比對方式會採用 config檔 中 passwordEncoder 設定( 這邊是不加密狀態下的比對 )。
		 * 
		 * 		IV. 如果密碼錯誤會拋出例外。如果正確，會 Return 一個認證後的 Authentication 物件(包含帳號、密碼、角色)
		 * 			( 底層會寫成 retuen craeateSuccessAuthentication(....)，但實際上他Create就是 Authentication 物件沒錯 )
		 * 
		 * 		參考資料: https://www.itread01.com/content/1544756435.html
		 * 
		 */
		// 這邊的認證會呼叫 ArpUserDetailsService 來配合做實際的驗證動作
		
		
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)); //這邊比對帳密、所以不用加入第三個參數:角色
        // 認證成功，會回傳一個 Authentication 物件
 		// 認證失敗則會丟出 Exception
 		// Authentication 物件包含 用戶資料(指 UserDetails)、權限
        

        // SecurityContextHolder 是用於存放 一個已經被授權的 Authentication 認證資訊
        // 存放你這次登入成功後，所擁有的權限資訊
        // 這邊 SecurityContextHolder 存放授權成功的 Authentication 認證
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 產生 token
        String token = jwtTokenUtil.createToken(authentication);
        
        // 把產生的 token 加工變成我們要的形式，再 return，長這樣:
        // {"accessToken":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmaXJzdCIsImlhdCI6MTYzOTA0MDk2MywiZXhwIjoxNjM5MDQxNTY4fQ.YloKzjIBMyyYglRnkdcLJVH1xuOJHTsoiwPQDWSTAn5hLSn67NIEncE7kK8QtaKJfQC-G7y7FcqmOkgAOivKGA",
        //  "tokenType":"Bearer"}
       
        
        // 這裡 JWT 是 Return 到 body
        // 但是之後驗證登入是要在 Header 裡找到 token
        // 而要把 return 的 token 存到 Header 需要靠前端 Ajax 來做。這件事，我們暫時用 postman 或 swagger 代替。 
        return ResponseEntity.ok(new JWTAuthResponse(token));
        
	}
	
}
