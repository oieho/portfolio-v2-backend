package com.oieho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.RecoverPassword;

public interface RecoverPasswordRepository extends JpaRepository<RecoverPassword, String> {

	RecoverPassword findByResetToken(String token);

	@Transactional
	@Modifying
	@Query("DELETE FROM RecoverPassword rp WHERE rp.resetToken = :resetToken")
	void deleteById(@Param("resetToken") String resetToken);

}
