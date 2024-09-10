package com.oieho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	// OAuth2
	@Query("SELECT m FROM Member m WHERE m.userId = :userName")
	public Member searchForUserInfo(@Param("userName") String userName);
	@Query("SELECT m.userNo FROM Member m WHERE m.userId = :userId") //컨트롤러의 myInfo 혼용
	public Long getUserNo(@Param("userId") String userId);

	// CustomUserDetailsService
	@Query("SELECT m.roleType FROM Member m WHERE m.userNo = :user_no")
	public List<String> findByAuth(@Param("user_no") long user_no);
	
	// JPA 쿼리메서드를 사용하여 사용자 정보 조회
	@EntityGraph(attributePaths="workComment", type = EntityGraph.EntityGraphType.FETCH)
	public List<Member> findByUserId(String userName);

	// Member Info
	@Query("SELECT m FROM Member m WHERE m.userNo = :userNo")
	public Member findByUserNo(@Param("userNo") Long userNo);
	
	// Member Modify
	@Query("SELECT m.userNo FROM Member m WHERE m.userId = :userId") //컨트롤러의 myInfo 혼용
	public Member getReferenceById(@Param("userId") String userId);
	
	public Boolean existsByUserIdAndUserPw(String userId,String userPw);
	
	@Query("SELECT m.userPw FROM Member m WHERE m.userId = :userId")
	public String findByEncodedPw(@Param("userId") String userId);
	
	
	public Member findUserPwByUserId(String userId);

	@Transactional
	@Modifying
	@Query("UPDATE Member m SET m.userId = :userId, m.userName = :userName, m.userEmail = :userEmail, m.updDate = now() WHERE m.userId = :userId")
	public void setMemberInfo(@Param("userId") String userId, @Param("userName") String userName, @Param("userEmail") String userEmail);
	
	
	// Member Modify Password
	@Transactional
	@Modifying
	@Query("UPDATE Member m SET m.userPw = :userPw WHERE m.userId = :userId")
	public void setMemberPw(@Param("userId") String userId, @Param("userPw") String userPw); // Member Find Password 혼용
	
	
	// Join
	public Boolean existsByUserId(String userId); // Member Find Password 혼용
	public Boolean existsByUserEmail(String userEmail); // Member Find ID 혼용
	
	
	// Member Find Id
	public Boolean existsByUserName(String userName);
	public Boolean existsByUserNameAndUserEmail(String userName, String userEmail);
	public Member findUserIdByUserName(String userName);
	
	
	// Member Find Password
	public Member findUserEmailByUserId(String userId);

	public Boolean existsByUserIdAndUserEmail(@Param("userId") String userId, @Param("userEmail") String userEmail);

	@Query("SELECT rp.resetToken FROM RecoverPassword rp WHERE rp.resetToken = :token")
	public String existsBySearchPasswordToken(@Param("token") String token);

}