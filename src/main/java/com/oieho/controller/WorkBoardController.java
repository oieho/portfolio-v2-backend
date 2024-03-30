package com.oieho.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.oieho.entity.Category;
import com.oieho.entity.Member;
import com.oieho.entity.WorkBoard;
import com.oieho.entity.WorkImage;
import com.oieho.repository.WorkBoardRepository;
import com.oieho.repository.WorkImageRepository;
import com.oieho.service.WorkBoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class WorkBoardController {
	private final WorkBoardService boardService;
	private final WorkBoardRepository boardRepository;
	private final WorkCommentController commentController;

	@Value("${upload.path}") // application.properties의 변수
	private String uploadPath;

	@PostMapping("/increase/{wno}")
	public ResponseEntity<?> increaseHits(@PathVariable("wno") long wno) throws Exception {
		boardRepository.increaseHits(wno);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> fetchList(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "count", required = false) String count,
			@RequestParam(name = "regDate", required = false) String regDate,
			@RequestParam(name = "searchType", required = false) String searchType,
			@RequestParam(name = "keyword", required = false) String keyword) throws Exception {
		List<Map<String, Object>> boards = boardService.searchByCategoryAndKeyword(searchType, keyword, title, count,
				regDate);
		return new ResponseEntity<List<Map<String, Object>>>(boards, HttpStatus.OK);
	}

	@GetMapping("/{boardNo}")
	public ResponseEntity<WorkBoard> read(@PathVariable("boardNo") Long boardNo) throws Exception {
		WorkBoard board = boardService.read(boardNo);
		System.out.println(board);
		return new ResponseEntity<WorkBoard>(board, HttpStatus.OK);
	}

	@GetMapping("/selected")
	public ResponseEntity<List<Map<String, Object>>> fetchBoardList(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "count", required = false) String count,
			@RequestParam(name = "regDate", required = false) String regDate,
			@RequestParam(name = "searchType", required = false) String searchType,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "selectedList", required = false) List<Long> selectedList) throws Exception {
		List<Map<String, Object>> boards = boardService.searchByWnosAndCategoryAndKeyword(searchType, keyword, title,
				count, regDate, selectedList);
		return new ResponseEntity<List<Map<String, Object>>>(boards, HttpStatus.OK);
	}

	@GetMapping("/fetchHashTag")
	public ResponseEntity<List<Map<String, Object>>> fetchHashTag(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "count", required = false) String count,
			@RequestParam(name = "regDate", required = false) String regDate,
			@RequestParam(name = "searchType", required = false) String searchType,
			@RequestParam(name = "keyword", required = false) String keyword) throws Exception {
		List<Map<String, Object>> boards = boardService.searchByKeywordOnHashTag(searchType, keyword, title, count,
				regDate);
		return new ResponseEntity<List<Map<String, Object>>>(boards, HttpStatus.OK);
	}

	@GetMapping("/fetchTool")
	public ResponseEntity<List<Map<String, Object>>> fetchTool(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "count", required = false) String count,
			@RequestParam(name = "regDate", required = false) String regDate,
			@RequestParam(name = "searchType", required = false) String searchType,
			@RequestParam(name = "keyword", required = false) String keyword) throws Exception {
		List<Map<String, Object>> boards = boardService.searchByKeywordOnTool(searchType, keyword, title, count,
				regDate);
//		String apiUrl = "/boards?selected=" + String.join(",", selected);
//		ResponseEntity<?> response = restTemplate.getForEntity(apiUrl, Object.class);
		return new ResponseEntity<List<Map<String, Object>>>(boards, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(
			@RequestPart(name =  "thumbnailFile", required = true) MultipartFile thumbnailFile,
			@RequestParam(name = "portfolioContent") String portfolioContent, @RequestParam(name = "title") String title,
			@RequestPart(name = "thumbnailImage") WorkImage thumbnailImage,
			@RequestPart(name =  "boardImages", required = false) List<WorkImage> boardImages,
			@RequestParam(name =  "tools", required = false) List<String> tools,
			@RequestParam(name = "description") String description, @RequestParam(name = "category") @NotNull Category category,
			@RequestParam(name = "hashTag") Set<String> hashTag, HttpServletRequest request) throws Exception {
		ResponseEntity<?> block = commentController.blockIP(request);
		if (block.getStatusCode() == HttpStatus.FORBIDDEN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		try {
			boardService.register(thumbnailFile, portfolioContent, title, thumbnailImage, boardImages, description,
					category, tools, hashTag);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			// Handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/modify")
	public ResponseEntity<?> modify(@RequestParam(name = "wno", required = true) String wno,
	        @RequestPart(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
	        @RequestParam(name = "portfolioContent") String portfolioContent,
	        @RequestParam(name = "title", required = true) String title,
	        @RequestPart(name = "thumbnailImage", required = false) WorkImage thumbnailImage,
	        @RequestPart(name = "boardImages", required = false) List<WorkImage> boardImages,
	        @RequestParam(name = "tools", required = false) List<String> tools,
	        @RequestParam(name = "description", required = true) String description,
	        @RequestParam(name = "category", required = true) @NotNull Category category,
	        @RequestParam(name = "hashTag", required = true) Set<String> hashTag,
	        @RequestParam(name = "hits", required = true) String hits, HttpServletRequest request) throws Exception {

		Long parsedWno = Long.parseLong(wno);
		Integer parsedHits = Integer.parseInt(hits);
		ResponseEntity<?> block = commentController.blockIP(request);
		if (block.getStatusCode() == HttpStatus.FORBIDDEN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		try {
			boardService.modify(thumbnailFile, parsedWno, portfolioContent, title, thumbnailImage, boardImages,
					description, category, tools, hashTag, parsedHits);
		} catch (Exception e) {
			// Handle exception
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		boardService.deleteOrphanImgs(parsedWno);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/remove")
	public ResponseEntity<Void> remove(@RequestParam("wno") Long wno) throws Exception {
		ResponseEntity<?> result = boardService.remove(wno);
		if (result.getStatusCode() == HttpStatus.OK) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}
	}

	@PostMapping("/getMaxWno")
	public ResponseEntity<Long> getMaxWno() throws Exception {
		Long maxWno = boardService.getCountWno();
		return new ResponseEntity<Long>(maxWno, HttpStatus.OK);
	}

	@GetMapping("/checkExistsWno")
	public ResponseEntity<Boolean> checkExistsWno(@RequestParam("wno") Long wno) throws Exception {
		Boolean result = boardService.checkBoardExists(wno);
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

	@GetMapping("/prevnextImgs")
	public ResponseEntity<List<Map<String, Object>>> getAllBoards(
	    @RequestParam(required = false) String searchType,
	    @RequestParam(required = false) String keyword,
	    @RequestParam(required = false) String title,
	    @RequestParam(required = false) String count,
	    @RequestParam(required = false) String regDate,
	    @RequestParam(required = false) List<Long> selected,
	    @RequestParam(required = false) String toolOrHashTag,
	    @RequestParam(required = false) Boolean isModified) {
	    try {
	        List<Map<String, Object>> boards;
	        if (isModified != null && isModified) {
	            boards = boardService.searchByCategoryAndKeyword(searchType, keyword, title, count, regDate);
	        } else if ("tool".equals(toolOrHashTag)) {
	            boards = boardService.searchByKeywordOnTool(searchType, keyword, title, count, regDate);
	        } else if ("hashTag".equals(toolOrHashTag)) {
	            boards = boardService.searchByKeywordOnHashTag(searchType, keyword, title, count, regDate);
	        } else if (selected != null && !selected.isEmpty()) {
	            boards = boardService.searchByWnosAndCategoryAndKeyword(searchType, keyword, title, count, regDate, selected);
	        } else {
	            boards = boardService.searchByCategoryAndKeyword(searchType, keyword, title, count, regDate);
	        }
	        return new ResponseEntity<>(boards, HttpStatus.OK);
	    } catch (Exception e) {
	        // 에러 처리
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


}
