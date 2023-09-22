package com.oieho.entity;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {
	private static final long serialVersionUID = 1L;
	private Member member;
	private String userNo;
	public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {  super(username, password, authorities);  }     
		
	public CustomUser(Member member) {  super(member.getUserId(), member.getUserPw(), (Collection<? extends GrantedAuthority>) member.getAuthorities());     this.member = member;  }
	public CustomUser(Member member, Collection<? extends GrantedAuthority> authorities) {  super(member.getUserId(), member.getUserPw(), authorities);     this.member = member;  }
	public long getUserNo() {  return member.getUserNo();  }
	public String getUserId() {  return member.getUserId();    }

	public void setAuthorities(ArrayList<GrantedAuthority> loadUserAuthorities) {
		// TODO Auto-generated method stub
		
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
		
	}
	
	public Member getMember() {
		return member;
	}
	} 