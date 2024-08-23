package com.oieho;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oieho.entity.Member;
import com.oieho.entity.RoleType;
import com.oieho.oauth.entity.ProviderType;
import com.oieho.repository.MemberRepository;
@SpringBootTest
public class addMember {
	@Autowired
	private MemberRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Test
	public void insertDummies() {
		IntStream.rangeClosed(1, 100).forEach(i->{
			RoleType roleType = null;
			if(i==1) {
				roleType = RoleType.ADMIN;
			} else {
				roleType = RoleType.USER;
			}
			Member member = Member.builder()
					.userId("a"+i)
					.userName("사용자"+i)
					.userEmail("user"+i+"@oieho.com")
					.roleType(roleType)
					.providerType(ProviderType.LOCAL)
					.userPw(passwordEncoder.encode("1"))
					.build();
		
			repository.save(member);
		
		});
	}
	
}
