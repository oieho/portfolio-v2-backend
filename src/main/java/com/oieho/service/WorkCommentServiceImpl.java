package com.oieho.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oieho.dto.CommentDTO;
import com.oieho.entity.Member;
import com.oieho.entity.RoleType;
import com.oieho.entity.WorkBoard;
import com.oieho.entity.WorkComment;
import com.oieho.repository.WorkCommentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WorkCommentServiceImpl implements WorkCommentService{
	private final WorkCommentRepository commentRepository;
	
	@Override
	public List<CommentDTO> commentlist(Long boardNo) throws Exception{
		List<CommentDTO> result = commentRepository.getCommentList(boardNo);
		return result;
	}

	@Override
	public void register(Long face, String text, Long wno, Long userNo) throws Exception {
		WorkComment comment = new WorkComment();
		comment.setText(text);
		comment.setFace(face);
		
		WorkBoard workBoard = new WorkBoard();
		workBoard.setWno(wno);
		comment.setWorkBoard(workBoard);
		
	    Member member = new Member();
		member.setUserNo(userNo);
		comment.setMember(member);
		
		comment.setDepth(0L);
		
		Long lastRdepth = commentRepository.findUidByWnoAndRdepth(wno);
		if(lastRdepth == null) {
			comment.setRdepth(1L);
		} else {
			comment.setRdepth(lastRdepth+1);
		}
		comment.setRnum(0L);
		
		Long lastUid = commentRepository.findUidByWno(wno);
		if(lastUid == null) {
			comment.setUid(1L);
		} else {
			comment.setUid(lastUid + 1L);
			}
		commentRepository.updateUids(lastUid);
		commentRepository.save(comment);
	}
	
	@Override
	public void modify(Long face, String text, Long wno, Long uid, Long userNo, RoleType roleType) throws Exception {
		if (roleType == null) {
		    throw new IllegalArgumentException("Role type cannot be null");
		} else if (roleType.getCode().equals("ROLE_USER")) {
			commentRepository.modifyComment(face, text, wno, uid, userNo);
		} else if (roleType.getCode().equals("ROLE_ADMIN")) {
			commentRepository.modifyCommentOnAdmin(face, text, wno, uid);
		} else {
		    throw new EntityNotFoundException("WorkComment not found");
		}
	}
	
	@Override
	public void reply(Long face, String text, Long wno, Long uid, Long depth, Long rdepth, Long userNo) throws Exception {
		WorkComment comment = new WorkComment();
		comment.setText(text);
		comment.setFace(face);
		
		WorkBoard workBoard = new WorkBoard();
		workBoard.setWno(wno);
		comment.setWorkBoard(workBoard);
		
	    Member member = new Member();
		member.setUserNo(userNo);
		comment.setMember(member);
		
		comment.setDepth(++depth);
		comment.setRdepth(rdepth);
		
		Long lastRnum = commentRepository.findRnumByWnoAndRdepth(wno, rdepth);
		comment.setRnum(lastRnum+1);
		
		Long lastUid = commentRepository.findUidByWnoAndDepth(wno, rdepth);
		comment.setUid(lastUid+1);

		
		commentRepository.updateUidsWhenReply(uid,wno,rdepth);
		commentRepository.save(comment);
	}
	
	@Override
	public Long getMaxUidPerOneRdepth(Long wno, Long rdepth) throws Exception {
		Long result = commentRepository.getMaxUidPerOneRdepth(wno, rdepth);
		return result;
	}
	
	@Override
	public ResponseEntity<Void> remove(Long cno, Long wno, Long uid, Long rnum, Long rdepth) throws Exception {
		Long countRnum = commentRepository.countRnum(wno,rdepth); // 댓글에 답글이 있는 경우 삭제 불가
		Long maxRnum = commentRepository.maxRnum(wno,rdepth); // 댓글에 답글이 있는 경우 삭제 불가
		System.out.println(maxRnum+":"+maxRnum+"  "+"rnum:"+rnum);
		if((maxRnum > 1 && maxRnum != rnum) || ((maxRnum > 0 && maxRnum != rnum) && countRnum >= 2)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	    commentRepository.deleteById(cno);
	    commentRepository.updateRnumGreaterThanBeforeRnum(wno,rdepth,rnum);
	    commentRepository.updateUidsGreaterThanDeletedUid(uid);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
