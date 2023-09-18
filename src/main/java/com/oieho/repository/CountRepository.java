package com.oieho.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oieho.entity.Count;

public interface CountRepository extends JpaRepository<Count, Long> {
	// JPA 쿼리메서드를 사용하여 사용자 정보 조회
	@Query("SELECT todayVar FROM Count")
	public Count findByOne();
	

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Count SET todayVar = todayVar+1, totalVar = totalVar+1 WHERE criteriaVar=1")
	public int updateCount();


	public Optional<Count> findByCriteriaVar(int thisis);
	
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Count SET todayVar = 0 WHERE criteriaVar = 1")
	public Integer updateTodayZero();


	@Query("SELECT c.todayVar FROM Count c WHERE c.criteriaVar = :criteriaVar")
	public Count getReference(@Param("criteriaVar") int criteriaVar);
	
}