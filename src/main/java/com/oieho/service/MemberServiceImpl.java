package com.oieho.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oieho.entity.Count;
import com.oieho.entity.DummyIP;
import com.oieho.entity.Member;
import com.oieho.entity.WorkComment;
import com.oieho.repository.CountRepository;
import com.oieho.repository.DummyIPRepository;
import com.oieho.repository.MemberRepository;
import com.oieho.repository.RecoverPasswordRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
	private final MemberRepository memberRepository;
	private final CountRepository countRepository;
	private final DummyIPRepository dummyRepository;
	private final RecoverPasswordRepository recoverPasswordRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public Member readMyInfo(Long userNo) throws Exception {
		return memberRepository.getReferenceById(userNo);
	}

	
	// Count
	@Override
	public Optional<DummyIP> readCnt(String clientIP) throws Exception {
		return dummyRepository.findById(clientIP);
	}
	@Override
	public Optional<Count> readCriteriaVar(int thisis) throws Exception {
		return countRepository.findByCriteriaVar(thisis);
	}
	@Override
	public int updateCount() throws Exception {
		return countRepository.updateCount();
	}
	@Override
	public List<Count> list() throws Exception {
		return countRepository.findAll(Sort.by(Direction.DESC, "todayVar"));
	}
	
	// Member Modify
	@Override
	public Member readMemberByUserId(Long userNo) throws Exception { //컨트롤러의 myInfo 혼용
		return memberRepository.findByUserNo(userNo);
	}
	@Override
	public Boolean pwchk(String userId, String userPw) throws Exception{
		String encodedPw = memberRepository.findByEncodedPw(userId);
		Boolean chkResult = false;
		if(passwordEncoder.matches(userPw,encodedPw)) {
			chkResult = memberRepository.existsByUserIdAndUserPw(userId,encodedPw);
		}
		return chkResult;
	}
	
	@Override
	public Boolean confirm(String userId, String userPw) throws Exception {
		Member userPwinDB = memberRepository.findUserPwByUserId(userId);
		String extractsUserPwInDB = userPwinDB.getUserPw();
		return passwordEncoder.matches(userPw,extractsUserPwInDB);
	}
	@Override
	public void modify(String userId, String userName, String userEmail) throws Exception {
		memberRepository.setMemberInfo(userId, userName, userEmail);
	}
	@Override
	public void remove(Long userNo) throws Exception {
	    Member member = memberRepository.findById(userNo).orElseThrow(() -> new EntityNotFoundException("Member not found"));

	    // Member와 연관된 WorkComment 엔티티들의 Member 필드를 null 값으로 업데이트
	    for (WorkComment comment : member.getWorkComment()) {
	        comment.setMember(null);
	    }
	    memberRepository.deleteById(userNo);
	}
	
	
	// Member Modify Password
	@Override
	public void changePw(String userId, String userPw) throws Exception { // Member Find Password 혼용
		memberRepository.setMemberPw(userId, userPw);
	}
	
	
	// Join
	@Override
	public void register(Member member) throws Exception {
		Member memberEntity = new Member();
		memberEntity.setUserId(member.getUserId());
		memberEntity.setUserPw(member.getUserPw());
		memberEntity.setUserEmail(member.getUserEmail());
		memberEntity.setUserName(member.getUserName());
		memberEntity.setRoleType(member.getRoleType());
		memberEntity.setProviderType(member.getProviderType());
		memberRepository.save(memberEntity);
		member.setUserNo(memberEntity.getUserNo());
	}
	@Override
	public Boolean chkDuplicatedId(String userId) throws Exception{ // Member Find Password 혼용
		boolean countId = memberRepository.existsByUserId(userId);
		System.out.println("countId:"+countId);
		return countId;
	}
	
	@Override
	public Boolean chkDuplicatedEmail(String userEmail) throws Exception{  // Member Find ID 혼용
		return memberRepository.existsByUserEmail(userEmail);
	}
	

	// MemberFindId
	@Override
	public Boolean chkDuplicatedName(String userName) throws Exception {
		System.out.println("::"+userName);
		return memberRepository.existsByUserName(userName);
	}
	@Override
	public Boolean chkNameAndEmail(String userName, String userEmail) throws Exception {
		return memberRepository.existsByUserNameAndUserEmail(userName,userEmail);
	}
	@Override
	public Member findUserIdByUserName(String userName) throws Exception {
		return memberRepository.findUserIdByUserName(userName);
	}
	
	// MemberFindPassword
	@Override
	public String existsByUserIdAndUserEmail(String userId, String userEmail) throws Exception {
		return memberRepository.existsByUserIdAndUserEmail(userId, userEmail);
	}
	@Override
	public HashMap<String,Object> chkDuplicatedIdOnFindPassword(String userId) throws Exception { // Member Find Password 혼용
		boolean id = memberRepository.existsByUserId(userId);
		Boolean booleanResult = null;
		Member email = null;
		if(id==true) {
			email = memberRepository.findUserEmailByUserId(userId);
			booleanResult = true;
		} else {
			booleanResult = false;
		}
		HashMap<String, Object> chkResult = new HashMap<>();
		chkResult.put("userId",id);
		if (email != null) {
	        chkResult.put("userEmail", email.getUserEmail());
	    }
		chkResult.put("booleanResult", booleanResult);
		System.out.println("CHKRESULT::"+chkResult);
		return chkResult;
	}
	@Override
	public String readSearchPasswordTokenByToken(String token) throws Exception {
		return memberRepository.existsBySearchPasswordToken(token);
	}
	@Override
	public void removeFindPasswordToken(String resetToken) throws Exception {
		recoverPasswordRepository.deleteById(resetToken);
	}
}
