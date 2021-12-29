package com.example.demo.pass.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.pass.exception.TokenErrorException;
import com.example.demo.pass.security.ArpUserDetailsService;
import com.example.demo.pass.util.JwtTokenUtil;

// 繼承 OncePerRequestFilter 的 Filter

// 只要進入網頁都會經過此 Filter
// 若無 Token 會通過此 Filter，由後面的 Security 機制把關。
// 若有 Token 則會驗證此 Token 有沒有效。有效就告知後面的 Security 放行。若無效則會拋出錯誤。

// 注意: 此 Filter 並不會驗證帳號、密碼正不正確，帳密驗證這件事在 Controller 已經做過了。我們這邊只會做: 
//		
//		1. 從 Token 解析出帳號名稱( 若Token過期或錯誤，這邊就會報錯了 )
//		2. 用帳號名稱請 Service 查到該帳號腳色
//		3. 使用帳號名稱、腳色建立一個認證 usernamePasswordAuthenticationToken，並存到 SecurityContextHolder 中
//		4. Filter 之後的 Security 機制就會驗證通過。

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private ArpUserDetailsService arpUserDetailsService;
	
    // 覆寫 doFilterInternal 方法
    @SuppressWarnings("deprecation") // 去除警告提醒
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    	
    	System.out.println("這裡是驗證 Header 裡 Token 的 Filter");
  
//    	if(request.getServletPath().equals("/test3")){
    		
    		// 取得 Header 中的 Authorization 參數
	    	String authHeader = request.getHeader("Authorization");
	    	
	    	System.out.println("SecurityContextHolder 中: "+ SecurityContextHolder.getContext().getAuthentication());
	    	
	        if (authHeader != null) {
	        	
	        	
        		// 這邊實際從 Token 裡解析出帳號名稱
        		// 若 Token 過期，或是 Token 內容不對，在這邊就會解析錯誤，顯示錯誤     	
	        	String userId = jwtTokenUtil.getUsername(authHeader.replace("Bearer ", ""));
				
	            
	            // 找到該帳號在 DB 的 帳號、密碼、角色，包成 UserDetails 給我們
	            UserDetails loadUserByUsername = arpUserDetailsService.loadUserByUsername(userId);
	            
	            
	            // 驗證 JWT ，如果正確，通知 Security 給過
	            if (userId != null ) { //SecurityContextHolder.getContext().getAuthentication() == null，不使用，因為有時候會殘留上一個使用者的認證
	            	
	            	// 以帳號名稱、腳色建立一個 UsernamePasswordAuthenticationToken 類別
	                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
	                        new UsernamePasswordAuthenticationToken(userId, null, loadUserByUsername.getAuthorities()); //第三參數 null 改成 loadUserByUsername.getAuthorities()，腳色資訊
	                
	                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                
	                // usernamePasswordAuthenticationToken 繼承一個抽象類別，該類別實作 Authentication 認證。所以他也算是一個認證
	                // 將認證存到 SecurityContextHolder 中
	                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	                
	                System.out.println("解析 Token 後，SecurityContextHolder 現在是"+SecurityContextHolder.getContext().getAuthentication());
	           
	            }
	            
	        }else {
	        	
	        	
	        	System.out.println("路徑:"+request.getServletPath());
	        	
	        	if(request.getServletPath().equals("/loginSuccessPage")){
	        		
	        		System.out.println("從 Google 第三方登入回來，不清空 SecurityContextHolder");

	        	}else {
	        		SecurityContextHolder.getContext().setAuthentication(null);
	        		System.out.println("Header沒東西，把 SecurityContextHolder 清空");
	        	}
	        	
	        	
	        	// Header 沒有 Authorization 參數回傳 401 錯誤
//	        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//	        	return;
	        	//response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        }
	        
//        }
    	// 跑完這個過濾器，繼續往下跑(可能接過濾器或是請求對象)
        chain.doFilter(request, response);
        
    }
    /*
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
	*/
}
