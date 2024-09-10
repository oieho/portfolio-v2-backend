package com.oieho.service;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.oieho.entity.Count;
import com.oieho.entity.DummyIP;
import com.oieho.entity.Member;

public interface MemberService {
	public Member readMyInfo(Long userNo) throws Exception;

	// Count
	public Optional<DummyIP> readCnt(String clientIP) throws Exception;
	public Optional<Count> readCriteriaVar(int thisis) throws Exception;
	public int updateCount() throws Exception;
	public List<Count> list() throws Exception;
	
	// Member Modify
	public Member readMemberByUserId(Long userNo) throws Exception;
	public Boolean pwchk(String userId, String userPw) throws Exception;
	
	public Boolean confirm(String userId, String userPw) throws Exception;
	public void modify(String userId, String userName, String userEmail) throws Exception;
	public void remove(Long userNo) throws Exception;
	
	
	// Member Modify Password
	public void changePw(String userId, String userPw) throws Exception; // Member Find Password 혼용
	
	
	// Join
	public void register(Member member) throws Exception;
	public Boolean chkDuplicatedId(String userId) throws Exception;
	public Boolean chkDuplicatedEmail(String userEmail) throws Exception;  // Member Find ID 혼용
	
	
	//MemberFindId
	public Boolean chkDuplicatedName(String userName) throws Exception; // Member Join 혼용
	public Boolean chkNameAndEmail(String userName, String userEmail) throws Exception;
	public Member findUserIdByUserName(String userName) throws Exception;
	
	// MemberFindPassword
	public Boolean existsByUserIdAndUserEmail(String userId, String userEmail) throws Exception;
	public HashMap<String,Object> chkDuplicatedIdOnFindPassword(String userId) throws Exception;
	public String readSearchPasswordTokenByToken(String token) throws Exception;
	public void removeFindPasswordToken(String resetToken) throws Exception;
} 
