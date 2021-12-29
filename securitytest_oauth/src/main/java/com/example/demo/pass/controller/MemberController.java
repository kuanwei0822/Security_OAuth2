package com.example.demo.pass.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.pass.model.Member;
import com.example.demo.pass.model.MemberService;
import com.example.demo.pass.util.JWTAuthResponse;
import com.example.demo.pass.util.JwtTokenUtil;

import io.spring.gradle.dependencymanagement.org.apache.maven.model.Model;

@RestController
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	OAuth2AuthorizedClientService oauth2ClientService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	// 使用到 Token 工具
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	
	// 測試網頁1
	@RequestMapping(path="/test1",method = RequestMethod.GET)
	public String testWeb1( OAuth2AuthenticationToken oauthToken, @AuthenticationPrincipal OidcUser principal) {
		// oauthToken 跟 principal 都是從第三方傳來的資料
		
		// oauthToken 其實是從 SecurityContextHolder 中拿出來的
		// Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
		// OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
		
		// principal 其實是從 oauthToken 中取出來的
		// principal = oauthToken.getPrincipal()
		
		System.out.println("-----------------------");
		System.out.println("oauthToken="+oauthToken);
		
		// 取得 idToken
		OidcIdToken idToken = principal.getIdToken();
		System.out.println("idToken="+idToken);
		
		// idToken 可以取得使用者名稱、Email...
		System.out.println(idToken.getEmail());
		System.out.println(idToken.getFullName());
		
		
		
		OAuth2AuthorizedClient oauth2Client = oauth2ClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
		//String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
//		System.out.println("jwtAccessToken="+jwtAccessToken);
		
		
		OAuth2AccessToken accessToken = oauth2Client.getAccessToken();
		
		
		System.out.println("AccessToken="+accessToken);
		System.out.println("-----------------------");
		
		return "hello1";
	}
	
	// 測試網頁2
	@RequestMapping(path="/test2",method = RequestMethod.GET)
	public String testWeb2() {
		return "hello2";
	}
	
	// 測試網頁3
	@RequestMapping(path="/test3",method = RequestMethod.GET)
	public String testWeb3() {
		return "hello3";
	}
	
	// 測試網頁4
	@RequestMapping(path="/test4",method = RequestMethod.GET)
	public String testWeb4() {
		return "hello4";
	}
	
	
	// 登入頁面(這頁有鎖)
//	@RequestMapping(path="/loginPage",method = RequestMethod.GET)
//	public ModelAndView loginPage() {
//		return new ModelAndView("loginPage.html");
//	}
	
	
	// Config 設定登入成功頁面(這頁有鎖)
	@RequestMapping(path="/loginSuccessPage",method = RequestMethod.GET)
	public ResponseEntity<JWTAuthResponse> loginSuccessPage(OAuth2AuthenticationToken oauthToken, @AuthenticationPrincipal OidcUser principal) {
		
		
		
		System.out.println("-----------------------");
		//System.out.println("OAuth2AuthenticationToken="+oauthToken);
		
		// 取得 idToken
		OidcIdToken idToken = principal.getIdToken();
		//System.out.println("idToken="+idToken);
		
		// idToken 可以取得使用者Email、ID...
		System.out.println("使用者Email="+idToken.getEmail());
		System.out.println("Google ID="+idToken.getSubject());
		
		
		// 取得 Google 用的 AccessToken
//		OAuth2AuthorizedClient oauth2Client = oauth2ClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
//		String jwtAccessToken = oauth2Client.getAccessToken().getTokenValue();
//		System.out.println("jwtAccessToken="+jwtAccessToken);
		System.out.println("-----------------------");
		
		// 資料庫搜尋此帳號
		Member memberFromDB = memberService.getOneMemberByName(idToken.getEmail());
		
		// 如果此帳號不存在建立帳號
		if( memberFromDB==null ) {
			
			Member newMember = new Member();
			
			newMember.setName(idToken.getEmail());
			newMember.setRole("A");
			newMember.setOauthtype("google");
			// password 暫時使用不加密的 Google ID
			newMember.setPaasword(idToken.getSubject());
			
			// 建立新帳號
			Member existMember = memberService.insertOneMember(newMember);
			if( existMember!=null ) {
				System.out.println("帳號建立成功");
			}
			
			// 查詢建立的新帳號
			memberFromDB = memberService.getOneMemberByName(idToken.getEmail());
			
		}
		
		// 第三方認證在進入這個 Controller 之前就已經做好完善的身分驗證了
		// 所以到這邊我們只是取出 驗證完成之後的資料 來產生認證
		
		// 產生認證(密碼帶入的是 Google 的使用者 ID)
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(memberFromDB.getName(), idToken.getSubject() ));
		
		// 產生 Token
        String token = jwtTokenUtil.createToken(authentication);
        
        
		// 回傳 Token 到頁面
        return ResponseEntity.ok(new JWTAuthResponse(token));
		
		
	}
	
	// 第三方預設登入成功頁面(這頁有鎖)
	@RequestMapping(path="/",method = RequestMethod.GET)
	public String oauthloginSuccessPage(OAuth2AuthenticationToken token) {
		System.out.println(token.getPrincipal());
		return "welcom";
	}
	
}