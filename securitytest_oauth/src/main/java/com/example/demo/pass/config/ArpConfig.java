package com.example.demo.pass.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.pass.filter.JWTAuthenticationFilter;
import com.example.demo.pass.security.ArpUserDetailsService;


@Configuration
@EnableWebSecurity
public class ArpConfig extends WebSecurityConfigurerAdapter{

	
	// for .addFilterBefore(...) 方法中使用。之前使用建構函數來帶入。  
	@Autowired
	private JWTAuthenticationFilter jwtAuthenticationFilter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		
			// 用來規定哪些請求可以放行
			.authorizeRequests()
			// 登入首頁放行
			.antMatchers(HttpMethod.GET,"/loginPage.html").permitAll()
			
			// 取得(非第三方) Token 頁面放行
			.antMatchers(HttpMethod.POST,"/getToken").permitAll()
			
			.anyRequest().authenticated() // 代表剩下網頁一律需要驗證
			
			// 第三方登入配置
			.and()
			.oauth2Login()
			.loginPage("/loginPage.html")
			.defaultSuccessUrl("/loginSuccessPage")
			
			
			// 安全性設定，固定要放
			.and()
			.csrf().disable()
			
			// 指定我們 Filter 在 FilerChain 中的位置。
			// 不指定好像會在最前面或最後面 Run，甚至可能不 Run! 這些都是我們不要的!
			// 我們指定在 UsernamePasswordAuthenticationFilter 之前，強制加入 jwtAuthenticationFilter 
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
	   
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {

	}
	
	
	 // 註冊認證管理器，當你帳密輸入正確之後，他會發一個已授權的 Authentication 認證給你。
	 // 驗證必須要加，否則會找不到 Bean
	 @Override
	 @Bean
	 public AuthenticationManager authenticationManagerBean() throws Exception {
	     return super.authenticationManagerBean();
	 }
	
	
//	// 密碼驗證。適用於資料庫中密碼已經是加密過的。
//	@Autowired
//	private UserDetailsService userDetailsService;
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		
//		// 用來驗證帳號、密碼(只能驗證在資料庫就是加密狀態的密碼)
//		auth.userDetailsService(userDetailsService)
//		.passwordEncoder(new BCryptPasswordEncoder());
//		
//	}
	
	// 密碼驗證。適用於資料庫密碼不加密。
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); //不推薦使用，但測試不管它
    }
    
    
    
    
}
