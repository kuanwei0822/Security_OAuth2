package com.example.demo.pass.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.pass.exception.BlogAPIException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

// 用來生成 Token 的工具

@Component
public class JwtTokenUtil {

	public static final String TOKEN_HEADER = "Authorization";
	// Token請求頭

	public static final String TOKEN_PREFIX = "Bearer";
	// Token字首

	private static final String SECRET = "anelfwilldie";
	// 自訂密鑰塞入token內，實際產出時不會寫在配置中

	private static final String ISS = "rrrrrr";
	// 發證者

	private static final long EXPIRE_TIME = 3600L;
	// token存活的時間，這邊沒有用到

	private static final long EXPIRE_REMEMBER = 3600000L;
	// 記住時間，單位毫秒，超過時間會過期，這邊設定1小時

	private static final String ROLE_CLAIMS = "memCadreCard";
	// 添加角色的KEY

	// 送出token
	// JWT由三段組成，分別是頭（header)、負載(payload)、簽名(signature)
	// header中會放:HS512,typ:JWT,來表明使用的加密碼，如果在下面沒給就會默認HS256
	public static String createToken(Authentication authentication) {
		
		String username = authentication.getName();
		Date currenDate = new Date();
		Date expireDate= new Date(currenDate.getTime() + EXPIRE_REMEMBER);

		String token = Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512,SECRET)
				.compact();
			return token;
	}




//驗證Token    
	public String getUsername (String token) {

		Claims claims = Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)  //原本用 parseClaimsJwt
				.getBody();
			return claims.getSubject();

}
	public boolean validateToken(String token) {
		
		try{
			Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
		    return true;
		}catch (SignatureException ex){
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
		} catch (MalformedJwtException ex) {
		    throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
		} catch (ExpiredJwtException ex) {
		    throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
		} catch (UnsupportedJwtException ex) {
		    throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
		    throw new BlogAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
		}
	}
}