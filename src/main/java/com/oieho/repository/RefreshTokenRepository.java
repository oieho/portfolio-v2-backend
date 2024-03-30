package com.oieho.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.RefreshToken;

@Transactional
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserId(String userId);
    RefreshToken findByUserIdAndRefreshToken(String userId, String oldRefreshToken);
    
    @Modifying
    @Query("UPDATE RefreshToken a SET a.refreshToken = :Refreshtoken, a.expirationTime = :expirationTime WHERE a.userId = :userId")
    void updateRefreshToken(@Param("userId") String userId, @Param("Refreshtoken") String Refreshtoken, @Param("expirationTime") Date expirationTime);

	@Modifying
	@Transactional
	@Query("DELETE FROM RefreshToken WHERE expirationTime < NOW()")
	void deleteByToken();
	
}