package com.example.demo.pass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//自訂義例外

//@ResponseStatus : 當拋出這個例外時，網頁會顯示 NOT_FOUND (404)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TokenErrorException extends RuntimeException {
	
	// 繼承 RuntimeException 例外的ID，不加也沒關係
	private static final long serialVersionUID = 1L;

	public TokenErrorException() {}
	
	public TokenErrorException(String message) {
		// 呼叫父類別 RuntimeException，把訊息丟給他
		super(message);
	}
	
}
