package com.oieho.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.oieho.dto.CommentDTO;
import com.oieho.entity.RoleType;

public interface WorkCommentService {
	public List<CommentDTO> commentlist(Long boardNo) throws Exception;
	public void register(Long face, String text, Long wno, Long userNo) throws Exception;
	public void modify(Long face, String text, Long wno, Long uid, Long userNo, RoleType roleType) throws Exception;
	public void reply(Long face, String text, Long wno, Long uid, Long depth, Long rdepth, Long userNo) throws Exception;
	public Long getMaxUidPerOneRdepth(Long wno, Long rdepth) throws Exception;
	public ResponseEntity<Void> remove(Long cno, Long wno, Long uid, Long rnum, Long rdepth) throws Exception;
}
