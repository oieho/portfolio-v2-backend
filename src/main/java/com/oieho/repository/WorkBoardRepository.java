package com.oieho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.Category;
import com.oieho.entity.WorkBoard;

@Repository
public interface WorkBoardRepository extends JpaRepository<WorkBoard, Long>, QuerydslPredicateExecutor<WorkBoard> {

	@Transactional
	@Modifying
	@Query("UPDATE WorkBoard e SET e.hits = e.hits + 1 WHERE e.wno = :wno")
	public void increaseHits(@Param("wno") Long wno);

	@Transactional
	@Modifying
	@Query("UPDATE WorkBoard w SET w.portfolioContent = :portfolioContent, w.title = :title, w.description = :description, w.category = :category, w.hits = :hits, w.updDate = now() WHERE w.wno = :wno")
	void updateWorkBoard(@Param("wno") Long wno, @Param("portfolioContent") String portfolioContent,
			@Param("title") String title, @Param("description") String description,
			@Param("category") Category category, @Param("hits") Integer hits);

	@Query("SELECT b.wno FROM WorkBoard b WHERE b.wno = (SELECT MAX(w.wno) FROM WorkBoard w)")
	Long countWno();

	@Transactional
	@Modifying
	@Query("UPDATE WorkBoard b SET b.portfolioContent = :#{#board.portfolioContent}, b.title = :#{#board.title}, b.description = :#{#board.description}, b.category = :#{#board.category}, b.tools = :#{#board.tools}, b.hashTag = :#{#board.hashTag}, b.hits = :#{#board.hits}, b.regDate = :#{#board.regDate} WHERE b.wno = :wno")
	void setByUpdating(@Param("board") WorkBoard board, @Param("wno") Long wno);

	@Transactional
	@Modifying
	@Query("DELETE FROM WorkBoard b WHERE b.title = ''")
	void deleteByEmptyTitle();

	@Query("SELECT COUNT(b) > 0 FROM WorkImage b WHERE b.imgName = :fileName") // 중복 된 이름이 있으면 while문을 계속 순회하며 랜덤 String 생성
	public boolean existsByImgName(@Param("fileName") String fileName);

	@Query("SELECT wb.portfolioContent FROM WorkBoard wb WHERE wb.wno = :wno")
	List<String> findPortfolioContentByWno(@Param("wno") Long wno);

	public boolean existsByWno(Long wno);

}