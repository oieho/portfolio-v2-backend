package com.oieho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.dto.CommentDTO;
import com.oieho.entity.WorkComment;

@Repository
public interface WorkCommentRepository extends JpaRepository<WorkComment, Long>{

	@Query("SELECT new com.oieho.dto.CommentDTO(wb.title, wb.regDate as boardRegDate, wb.description, m.userNo, m.userName, c.cno, c.regDate, c.text, c.face, c.uid, c.depth, c.rnum, c.rdepth, wb.wno) " +
		       "FROM WorkComment c " +
		       "LEFT JOIN c.member m " +
		       "LEFT JOIN c.workBoard wb " +
		       "WHERE wb.wno = :boardNo " +
			   "ORDER BY c.uid ASC")
	List<CommentDTO> getCommentList(@Param("boardNo") Long boardNo);

	@Query("SELECT max(c.uid) FROM WorkComment c WHERE c.workBoard.wno = :wno")
    Long findUidByWno(@Param("wno") Long wno);
	
	@Query("SELECT max(c.rdepth) FROM WorkComment c WHERE c.workBoard.wno = :wno")
    Long findUidByWnoAndRdepth(@Param("wno") Long wno);
	
	@Query("SELECT max(c.rnum) FROM WorkComment c WHERE c.rdepth = :rdepth AND c.workBoard.wno = :wno")
    Long findRnumByWnoAndRdepth(@Param("wno") Long wno, @Param("rdepth") Long rdepth);
	
	@Query("SELECT max(c.uid) FROM WorkComment c WHERE c.rdepth = :rdepth AND c.workBoard.wno = :wno AND c.rnum = (SELECT max(cc.rnum) FROM WorkComment cc WHERE cc.rdepth = :rdepth AND cc.workBoard.wno = :wno)")
    Long findUidByWnoAndDepth(@Param("wno") Long wno, @Param("rdepth") Long rdepth);
	
	@Transactional //삭제 후 uid 업데이트
    @Modifying
    @Query("UPDATE WorkComment c SET c.uid = c.uid - 1 WHERE c.uid > :uid")
    void updateUidsGreaterThanDeletedUid(@Param("uid") Long uid);

	WorkComment findByWorkBoard_WnoAndUidAndMember_UserNo(Long wno, Long uid, Long userNo); //Modify

	@Transactional
	@Modifying
	@Query("UPDATE WorkComment c SET c.updDate = now(), c.face = :face, c.text = :text WHERE c IN (SELECT c FROM WorkComment c JOIN c.workBoard b WHERE b.wno = :wno AND c.uid = :uid) AND c IN (SELECT c FROM WorkComment c JOIN c.member m WHERE m.userNo = :userNo)")
	void modifyComment(@Param("face") Long face, @Param("text") String text, @Param("wno") Long wno, @Param("uid") Long uid, @Param("userNo") Long userNo);
	
	@Transactional
	@Modifying
	@Query("UPDATE WorkComment c SET c.updDate = now(), c.face = :face, c.text = :text WHERE c IN (SELECT c FROM WorkComment c JOIN c.workBoard b WHERE b.wno = :wno AND c.uid = :uid)")
	void modifyCommentOnAdmin(@Param("face") Long face, @Param("text") String text, @Param("wno") Long wno, @Param("uid") Long uid);

	@Transactional //uid 업데이트 후 uid 정렬
    @Modifying
    @Query("UPDATE WorkComment c SET c.uid = c.uid + 1 WHERE c.uid > :uid")
	void updateUids(@Param("uid") Long uid);
	
	@Transactional //uid 업데이트 후 uid 정렬
    @Modifying
    @Query("UPDATE WorkComment c SET c.uid = c.uid + 1 WHERE c.uid > :uid + (SELECT max(cc.rnum) FROM WorkComment cc WHERE cc.rdepth = :rdepth AND cc.workBoard.wno = :wno)")
	void updateUidsWhenReply(@Param("uid") Long uid, @Param("wno") Long wno, @Param("rdepth") Long rdepth);

	@Query("SELECT max(c.uid) FROM WorkComment c WHERE c.rdepth = :rdepth AND c.workBoard.wno = :wno")
	Long getMaxUidPerOneRdepth(@Param("wno") Long wno, @Param("rdepth") Long rdepth);

	@Query("SELECT count(c.rnum) FROM WorkComment c WHERE c.workBoard.wno = :wno AND c.rdepth = :rdepth")
	Long countRnum(@Param("wno") Long wno, @Param("rdepth") Long rdepth);
	
	@Query("SELECT max(c.rnum) FROM WorkComment c WHERE c.workBoard.wno = :wno AND c.rdepth = :rdepth")
	Long maxRnum(@Param("wno") Long wno, @Param("rdepth") Long rdepth);
	
	@Transactional
	@Modifying
	@Query("UPDATE WorkComment c SET c.rnum = c.rnum - 1 WHERE c.workBoard.wno = :wno AND c.rdepth = :rdepth AND c.rnum > :rnum")
	void updateRnumGreaterThanBeforeRnum(@Param("wno") Long wno, @Param("rdepth") Long rdepth, @Param("rnum") Long rnum);

}