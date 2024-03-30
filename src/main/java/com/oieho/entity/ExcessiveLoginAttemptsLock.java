package com.oieho.entity;

import java.io.Serializable;

import jakarta.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter	
@RedisHash(value = "LockLogin")
public class ExcessiveLoginAttemptsLock implements Serializable{

	@Id
    private String id; // redis에서는 id 필드를 강제 -> id를 다른 문자로 바꾸면 Entity com.oieho.entity.ExcessiveLoginAttemptsLock requires to have an explicit id field 오류 발생.
	private String username;
	private Integer loginAttempts;
    private Boolean lockLogin;
    
    public ExcessiveLoginAttemptsLock(String username, int loginAttempts, Boolean lockLogin) {
    	this.username = username;
    	this.loginAttempts = loginAttempts;
    	this.lockLogin = lockLogin;
    }

	public ExcessiveLoginAttemptsLock(String username, Integer loginAttempts) {
		this.username = username;
		this.loginAttempts = loginAttempts;
	}

	@JsonCreator
    public ExcessiveLoginAttemptsLock(@JsonProperty("username") String username) {
        this.username = username;
    }
}