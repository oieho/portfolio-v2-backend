package com.oieho.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.oieho.entity.Category;
import com.oieho.entity.WorkBoard;
import com.oieho.entity.WorkImage;

public interface WorkBoardService {

	public List<Map<String, Object>> searchByCategoryAndKeyword(String searchType, String keyword, String title, String count, String regDate) throws Exception;
	public List<Map<String, Object>> searchByWnosAndCategoryAndKeyword(String searchType, String keyword, String title, String count, String regDate, List<Long> selected) throws Exception;
	public List<Map<String,Object>> searchByKeywordOnHashTag(String searchType, String keyword, String title, String count, String regDate) throws Exception;
	public List<Map<String,Object>> searchByKeywordOnTool(String searchType, String keyword, String title, String count, String regDate) throws Exception;
	public WorkBoard read(Long boardNo) throws Exception;
	public ResponseEntity<String> modify(MultipartFile thumbnailFile, Long wno, String portfolioContent, String title, WorkImage thumbnailImage, List<WorkImage> boardImages, String description, Category category, List<String> tools, Set<String> hashTag, Integer hits) throws Exception;
	public ResponseEntity<String> register(MultipartFile thumbnailFile, String portfolioContent, String title, WorkImage thumbnailImage, List<WorkImage> boardImages, String description, Category category, List<String> tools, Set<String> hashTag) throws Exception;
	public ResponseEntity<?> remove(Long wno) throws Exception;
	public String makeFolder(Boolean thumbnailyn);
	public void deleteOrphanImgs(Long wno);
	public Long getCountWno();
	public boolean checkBoardExists(Long wno);
}