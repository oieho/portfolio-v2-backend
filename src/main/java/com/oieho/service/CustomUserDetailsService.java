package com.oieho.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.oieho.entity.CustomUser;
import com.oieho.entity.Member;
import com.oieho.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private MemberRepository repository;

	public ArrayList<GrantedAuthority> loadUserAuthorities(long user_no) {
        List<String> authorities = repository.findByAuth(user_no);
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String auth: authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(auth));
        }

        return grantedAuthorities;
    }
	
	// 사용자정보 조회
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Member member = repository.findByUserId(userName).get(0);
		System.out.println("member:::::::"+member);
		member.setAuthorities(loadUserAuthorities(member.getUserNo()));
		return member == null ? null : new CustomUser(member);
	}
}