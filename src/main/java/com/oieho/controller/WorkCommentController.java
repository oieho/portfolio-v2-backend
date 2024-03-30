package com.oieho.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oieho.dto.CommentDTO;
import com.oieho.entity.RoleType;
import com.oieho.entity.WorkComment;
import com.oieho.service.WorkCommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/boards/comment")
@RequiredArgsConstructor
public class WorkCommentController {
	private final WorkCommentService commentService;
	
	@GetMapping("/{boardNo}")
	public ResponseEntity<List<CommentDTO>> list(@PathVariable("boardNo") Long boardNo) throws Exception{
		List<CommentDTO> comment = commentService.commentlist(boardNo);
		return new ResponseEntity<List<CommentDTO>>(comment, HttpStatus.OK);
	}
	
	private static final long BLOCK_DURATION_MILLIS = 10000; // 차단 유지 시간 (30분 : 1800000)
	private static final int MAX_VIOLATION_COUNT = 5;
	private static final int MAX_COMMENT_COUNT_PER_CYCLE = 3;
	private static final long CYCLE_DURATION_MILLIS = 5000;
	private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

	private static final Map<String, List<Long>> COMMENT_TIME_MAP = new ConcurrentHashMap<>();
	private static final Map<String, Integer> VIOLATION_COUNT_MAP = new ConcurrentHashMap<>();
	private static final Set<String> BLOCKED_IP_SET = new HashSet<>();
	
	public ResponseEntity<?> blockIP(HttpServletRequest request) throws Exception {
		String clientIP = getClientIP(request);
	    System.out.println(BLOCKED_IP_SET);
	    // 차단된 IP 주소인 경우, HTTP 응답 코드 FORBIDDEN 반환
	    if (BLOCKED_IP_SET.contains(clientIP)) {
	        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	    }

	    // 해당 IP 주소에서의 댓글 등록 시간 리스트 가져오기
	    List<Long> commentTimeList = COMMENT_TIME_MAP.getOrDefault(clientIP, new ArrayList<>());

	    // 일정 시간 안에 등록된 댓글 수 체크
	    long currentTimeMillis = System.currentTimeMillis();
	    int commentCount = 0;

	    for (int i = commentTimeList.size() - 1; i >= 0; i--) {
	        long commentTimeMillis = commentTimeList.get(i);
	        if (currentTimeMillis - commentTimeMillis <= CYCLE_DURATION_MILLIS) {
	            commentCount++;
	            System.out.println("commentcount::"+commentCount);
	        } else {
	            // 일정 시간이 지난 댓글은 리스트에서 제거
	            commentTimeList.remove(i);
	        }
	    }
	    
	    // 일정 시간 안에 등록된 댓글 수가 일정 횟수 이상인 경우 어긴 횟수 증가
	    if (commentCount >= MAX_COMMENT_COUNT_PER_CYCLE) {
	        int violationCount = VIOLATION_COUNT_MAP.getOrDefault(clientIP, 0);
	        VIOLATION_COUNT_MAP.put(clientIP, violationCount + 1);
	        System.out.println("violationCount"+violationCount);

	        // 어긴 횟수가 일정 횟수 이상인 경우 IP 주소 차단
	        if (violationCount+1 == MAX_VIOLATION_COUNT) {
	            BLOCKED_IP_SET.add(clientIP);

	            // 일정 시간이 지난 후 차단 해제
	            EXECUTOR_SERVICE.schedule(() -> {
	                BLOCKED_IP_SET.remove(clientIP);
	                COMMENT_TIME_MAP.remove(clientIP);
	                VIOLATION_COUNT_MAP.remove(clientIP);
	                System.out.println("BLOCKED_IP_SET"); 
	            }, BLOCK_DURATION_MILLIS, TimeUnit.MILLISECONDS);
	            
	            
	            
	            return new ResponseEntity<>("Blocked", HttpStatus.FORBIDDEN);
	        }
	    } else {
	        // 댓글 등록 시간 기록
	        commentTimeList.add(currentTimeMillis);
	        COMMENT_TIME_MAP.put(clientIP, commentTimeList);

	        // 어긴 횟수 초기화
	        VIOLATION_COUNT_MAP.remove(clientIP);
	    }
        return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/register")
	public ResponseEntity<?> register(@RequestParam(name="face") Long face, @RequestParam(name="text") String text, @RequestParam(name="wno") Long wno, @RequestParam(name="userNo") Long userNo, HttpServletRequest request) throws Exception{
	    
		ResponseEntity<?> block = blockIP(request);
		if(block.getStatusCode() == HttpStatus.FORBIDDEN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	    commentService.register(face,text,wno,userNo);
	    return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getClientIP(HttpServletRequest request) {
	    String clientIP = request.getHeader("X-Forwarded-For");
	    if (clientIP == null) {
	        clientIP = request.getRemoteAddr();
	    }
	    return clientIP;
	}
	
	@PutMapping("/modify")
	public ResponseEntity<Boolean> modify(@RequestBody WorkComment comment, HttpServletRequest request) throws Exception{
		ResponseEntity<?> block = blockIP(request);
		if(block.getStatusCode() == HttpStatus.FORBIDDEN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Long face = comment.getFace();
	    String text = comment.getText();
	    Long wno = comment.getWorkBoard().getWno();
	    Long uid = comment.getUid();
	    Long userNo = comment.getMember().getUserNo();
	    RoleType roleType = comment.getMember().getRoleType();
	    commentService.modify(face, text, wno, uid, userNo, roleType);
		
		return new ResponseEntity<Boolean>(HttpStatus.OK);
	}
	
	@GetMapping("/reply")
	public ResponseEntity<?> reply(@RequestParam(name="face") Long face, @RequestParam(name="text") String text, @RequestParam(name="wno") Long wno, @RequestParam(name="uid") Long uid, @RequestParam(name="depth") Long depth, @RequestParam(name="rdepth") Long rdepth, @RequestParam(name="userNo") Long userNo, HttpServletRequest request) throws Exception{
	    
		ResponseEntity<?> block = blockIP(request);
		if(block.getStatusCode() == HttpStatus.FORBIDDEN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		commentService.reply(face, text, wno, uid, depth, rdepth, userNo);
	    return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/remove")
	public ResponseEntity<Void> remove(@RequestParam("cno") Long cno, @RequestParam("wno") Long wno, @RequestParam("uid") Long uid, @RequestParam("rnum") Long rnum, @RequestParam("rdepth") Long rdepth) throws Exception {
		ResponseEntity<Void> result = commentService.remove(cno,wno,uid,rnum,rdepth);
		if(result.getStatusCode() == HttpStatus.OK) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping("/getMaxUidPerOneRdepth")
	public ResponseEntity<Long> getMaxUid(@RequestBody WorkComment comment) throws Exception{
		Long wno = comment.getWorkBoard().getWno();
	    Long rdepth = comment.getRdepth();
	    Long maxUid = commentService.getMaxUidPerOneRdepth(wno, rdepth);
		return new ResponseEntity<Long>(maxUid,HttpStatus.OK);
	}
}