package com.oieho.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.oieho.entity.Category;
import com.oieho.entity.QWorkBoard;
import com.oieho.entity.QWorkComment;
import com.oieho.entity.WorkBoard;
import com.oieho.entity.WorkImage;
import com.oieho.repository.WorkBoardRepository;
import com.oieho.repository.WorkImageRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;

@RequiredArgsConstructor
@Service
public class WorkBoardServiceImpl implements WorkBoardService {
	private final WorkBoardRepository boardRepository;
	private final WorkImageRepository imageRepository;
	private final EntityManager entityManager;

	@Value("${upload.path}") // application.properties의 변수
	private String uploadPath;

	// Default
	@Override
	public List<Map<String, Object>> searchByCategoryAndKeyword(String searchType, String keyword, String title,
			String count, String regDate) throws Exception {
		BooleanBuilder builder = new BooleanBuilder();
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QWorkBoard qWorkBoard = QWorkBoard.workBoard;
		QWorkComment qWorkComment = QWorkComment.workComment;
		String filteredStr = Optional.ofNullable(keyword).filter(s -> !s.isEmpty()).orElse("");
		String[] convertedKeyword = new String[] { keyword };
		if (searchType != null) {
			BooleanBuilder categoryBuilder = new BooleanBuilder();
			BooleanBuilder keywordBuilder = new BooleanBuilder();

			switch (searchType) {
			case "브로셔":
			case "로고":
			case "포스터":
			case "캐릭터":
			case "홈페이지":
			case "상세페이지":
			case "잡지":
			case "기타":
				categoryBuilder
						.and(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.title.contains(filteredStr)))
						.or(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.portfolioContent.contains(filteredStr)))
						.or(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.hashTag.any().in(convertedKeyword)))
						.or(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.tools.any().in(convertedKeyword)));
				break;
			default:
				break;
			}

			if (!filteredStr.isEmpty()) {
				keywordBuilder.and(
						qWorkBoard.title.contains(filteredStr).or(qWorkBoard.portfolioContent.contains(filteredStr))
								.or(qWorkBoard.hashTag.any().in(convertedKeyword))
								.or(qWorkBoard.tools.any().in(convertedKeyword)));
			}

			builder.and(categoryBuilder.and(keywordBuilder));
		}

		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if ("desc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.desc());
		} else if ("asc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.asc());
		}

		if ("desc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.desc());
		} else if ("asc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.asc());
		}

		if ("desc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.desc());
		} else if ("asc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.asc());
		}
		List<Tuple> result = queryFactory
				.select(qWorkBoard,
						JPAExpressions.select(qWorkComment.cno.count()).from(qWorkComment)
								.where(qWorkBoard.wno.eq(qWorkComment.workBoard.wno)))
				.from(qWorkBoard).where(builder)
				.orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[orderSpecifiers.size()])).fetch();

		List<Map<String, Object>> jsonResult = new ArrayList<>();
		for (Tuple tuple : result) {
			Map<String, Object> resultMap = new HashMap<>();
			WorkBoard workBoard = tuple.get(0, WorkBoard.class);
			Long commentCount = tuple.get(1, Long.class);
			Set<String> hashTags = workBoard.getHashTag();
			List<String> tools = workBoard.getTools();
			// 필요한 경우에만 HashTag 엔티티를 추가로 조회
			if (!hashTags.isEmpty() && !tools.isEmpty()) {
				Hibernate.initialize(hashTags);
				Hibernate.initialize(tools);
			}

			resultMap.put("workBoard", workBoard);
			resultMap.put("commentCount", commentCount);
			jsonResult.add(resultMap);
		}

		return jsonResult;
	}

	@Override // wnos는 select 모드에서 선택 된 객체들을 의미
	public List<Map<String, Object>> searchByWnosAndCategoryAndKeyword(String searchType, String keyword, String title,
			String count, String regDate, List<Long> selected) throws Exception {
		BooleanBuilder builder = new BooleanBuilder();
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QWorkBoard qWorkBoard = QWorkBoard.workBoard;
		QWorkComment qWorkComment = QWorkComment.workComment;
		String filteredStr = Optional.ofNullable(keyword).filter(s -> !s.isEmpty()).orElse("");
		String[] convertedKeyword = new String[] { keyword };

		if (selected != null && !selected.isEmpty()) {
			builder.and(qWorkBoard.wno.in(selected));
		}

		if (searchType != null) {
			BooleanBuilder categoryBuilder = new BooleanBuilder();
			BooleanBuilder keywordBuilder = new BooleanBuilder();

			switch (searchType) {
			case "브로셔":
			case "로고":
			case "포스터":
			case "캐릭터":
			case "홈페이지":
			case "상세페이지":
			case "잡지":
			case "기타":
				categoryBuilder
						.and(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.title.contains(filteredStr)))
						.or(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.portfolioContent.contains(filteredStr)))
						.or(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.hashTag.any().in(convertedKeyword)))
						.or(qWorkBoard.category.eq(Category.valueOf(searchType))
								.and(qWorkBoard.tools.any().in(convertedKeyword)));
				break;
			default:
				break;
			}

			if (!filteredStr.isEmpty()) {
				keywordBuilder.and(
						qWorkBoard.title.contains(filteredStr).or(qWorkBoard.portfolioContent.contains(filteredStr))
								.or(qWorkBoard.hashTag.any().in(convertedKeyword))
								.or(qWorkBoard.tools.any().in(convertedKeyword)));
			}

			builder.and(categoryBuilder.and(keywordBuilder));
		}

		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if ("desc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.desc());
		} else if ("asc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.asc());
		}

		if ("desc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.desc());
		} else if ("asc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.asc());
		}

		if ("desc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.desc());
		} else if ("asc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.asc());
		}
		List<Tuple> result = queryFactory
				.select(qWorkBoard,
						JPAExpressions.select(qWorkComment.cno.count()).from(qWorkComment)
								.where(qWorkBoard.wno.eq(qWorkComment.workBoard.wno)))
				.from(qWorkBoard).where(builder)
				.orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[orderSpecifiers.size()])).fetch();

		List<Map<String, Object>> jsonResult = new ArrayList<>();
		for (Tuple tuple : result) {
			Map<String, Object> resultMap = new HashMap<>();
			WorkBoard workBoard = tuple.get(0, WorkBoard.class);
			Long commentCount = tuple.get(1, Long.class);
			Set<String> hashTags = workBoard.getHashTag();
			List<String> tools = workBoard.getTools();
			// 필요한 경우에만 HashTag 엔티티를 추가로 조회
			if (!hashTags.isEmpty() && !tools.isEmpty()) {
				Hibernate.initialize(hashTags);
				Hibernate.initialize(tools);
			}

			resultMap.put("workBoard", workBoard);
			resultMap.put("commentCount", commentCount);
			jsonResult.add(resultMap);
		}

		return jsonResult;
	}

	@Override
	public List<Map<String, Object>> searchByKeywordOnHashTag(String searchType, String keyword, String title,
			String count, String regDate) throws Exception {
		BooleanBuilder builder = new BooleanBuilder();
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QWorkBoard qWorkBoard = QWorkBoard.workBoard;
		QWorkComment qWorkComment = QWorkComment.workComment;
		String[] convertedKeyword = new String[] { keyword };

		if (searchType != null) {
			switch (searchType) {
			case "브로셔":
			case "로고":
			case "포스터":
			case "캐릭터":
			case "홈페이지":
			case "상세페이지":
			case "잡지":
			case "기타":
				BooleanBuilder categoryBuilder = new BooleanBuilder();
				categoryBuilder.and(qWorkBoard.category.eq(Category.valueOf(searchType)));

				BooleanBuilder hashTagBuilder = new BooleanBuilder();
				hashTagBuilder.and(qWorkBoard.hashTag.any().in(convertedKeyword));

				BooleanBuilder otherBuilder = hashTagBuilder;
				builder.and(categoryBuilder).and(otherBuilder);
				break;
			default:
				builder.and((qWorkBoard.hashTag.any().in(convertedKeyword)));
				break;
			}
		}

		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if ("desc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.desc());
		} else if ("asc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.asc());
		}

		if ("desc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.desc());
		} else if ("asc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.asc());
		}

		if ("desc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.desc());
		} else if ("asc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.asc());
		}
		List<Tuple> result = queryFactory
				.select(qWorkBoard,
						JPAExpressions.select(qWorkComment.cno.count()).from(qWorkComment)
								.where(qWorkBoard.wno.eq(qWorkComment.workBoard.wno)))
				.from(qWorkBoard).where(builder)
				.orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[orderSpecifiers.size()])).fetch();

		List<Map<String, Object>> jsonResult = new ArrayList<>();
		for (Tuple tuple : result) {
			Map<String, Object> resultMap = new HashMap<>();
			WorkBoard workBoard = tuple.get(0, WorkBoard.class);
			Long commentCount = tuple.get(1, Long.class);
			Set<String> hashTags = workBoard.getHashTag();
			List<String> tools = workBoard.getTools();
			// 필요한 경우에만 HashTag 엔티티를 추가로 조회
			if (!hashTags.isEmpty() && !tools.isEmpty()) {
				Hibernate.initialize(hashTags);
				Hibernate.initialize(tools);
			}

			resultMap.put("workBoard", workBoard);
			resultMap.put("commentCount", commentCount);
			jsonResult.add(resultMap);
		}

		return jsonResult;
	}

	@Override
	public List<Map<String, Object>> searchByKeywordOnTool(String searchType, String keyword, String title,
			String count, String regDate) throws Exception {
		BooleanBuilder builder = new BooleanBuilder();
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QWorkBoard qWorkBoard = QWorkBoard.workBoard;
		QWorkComment qWorkComment = QWorkComment.workComment;
		String[] convertedKeyword = new String[] { keyword };

		if (searchType != null) {
			switch (searchType) {
			case "브로셔":
			case "로고":
			case "포스터":
			case "캐릭터":
			case "홈페이지":
			case "상세페이지":
			case "잡지":
			case "기타":
				BooleanBuilder categoryBuilder = new BooleanBuilder();
				categoryBuilder.and(qWorkBoard.category.eq(Category.valueOf(searchType)));

				BooleanBuilder toolsBuilder = new BooleanBuilder();
				toolsBuilder.and(qWorkBoard.tools.any().in(convertedKeyword));

				BooleanBuilder otherBuilder = toolsBuilder;
				builder.and(categoryBuilder).and(otherBuilder);
				break;
			default:
				builder.and(qWorkBoard.tools.any().in(convertedKeyword));
				break;
			}
		}

		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		if ("desc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.desc());
		} else if ("asc".equals(title)) {
			orderSpecifiers.add(qWorkBoard.title.asc());
		}

		if ("desc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.desc());
		} else if ("asc".equals(count)) {
			orderSpecifiers.add(qWorkBoard.hits.asc());
		}

		if ("desc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.desc());
		} else if ("asc".equals(regDate)) {
			orderSpecifiers.add(qWorkBoard.regDate.asc());
		}
		List<Tuple> result = queryFactory
				.select(qWorkBoard,
						JPAExpressions.select(qWorkComment.cno.count()).from(qWorkComment)
								.where(qWorkBoard.wno.eq(qWorkComment.workBoard.wno)))
				.from(qWorkBoard).where(builder)
				.orderBy(orderSpecifiers.toArray(new OrderSpecifier<?>[orderSpecifiers.size()])).fetch();

		List<Map<String, Object>> jsonResult = new ArrayList<>();
		for (Tuple tuple : result) {
			Map<String, Object> resultMap = new HashMap<>();
			WorkBoard workBoard = tuple.get(0, WorkBoard.class);
			Long commentCount = tuple.get(1, Long.class);
			Set<String> hashTags = workBoard.getHashTag();
			List<String> tools = workBoard.getTools();
			// 필요한 경우에만 HashTag 엔티티를 추가로 조회
			if (!hashTags.isEmpty() && !tools.isEmpty()) {
				Hibernate.initialize(hashTags);
				Hibernate.initialize(tools);
			}

			resultMap.put("workBoard", workBoard);
			resultMap.put("commentCount", commentCount);
			jsonResult.add(resultMap);
		}

		return jsonResult;
	}

	@Override
	public WorkBoard read(Long wno) throws Exception {
		WorkBoard result = boardRepository.getReferenceById(wno);
		return result;
	}

	@Override
	public ResponseEntity<String> register(MultipartFile thumbnailFile, String portfolioContent, String title,
			WorkImage thumbnailImage, List<WorkImage> boardImages, String description, Category category,
			List<String> tools, Set<String> hashTag) {

		try {
			saveThumbnailImg(thumbnailFile, thumbnailImage);
			saveImagesToDB(portfolioContent, title, description, category, tools, hashTag, thumbnailImage, boardImages);

		} catch (IOException e) {
			e.printStackTrace();
			String errorMessage = "Internal server error";
			return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void deletePreviousThumbnail(Long wno) {
		WorkBoard previousBoard = boardRepository.findById(wno).orElse(null);
		if (previousBoard != null && previousBoard.getWorkImages() != null
				&& !previousBoard.getWorkImages().isEmpty()) {
			WorkImage previousImage = previousBoard.getWorkImages().get(0); // 첫 번째 WorkImage 객체를 가져옴
			String previousPath = previousImage.getPath();
			String previousImgName = previousImage.getImgName();
			String previousUuid = previousImage.getUuid();
			File previousFile = new File(previousPath, "s_" + previousUuid + "_" + previousImgName);
			if (previousFile.exists()) {
				previousFile.delete();
			}
		}
	}

	private void saveThumbnailImg(MultipartFile uploadFile, WorkImage imgInfo) throws IOException {
		if (imgInfo != null) {
			String imgName = imgInfo.getImgName();
			String uuid = imgInfo.getUuid();
			String path = imgInfo.getPath();
			String thumbnailSaveName = path + "/" + "s_" + uuid + "_" + imgName;

			BufferedImage originalImage = ImageIO.read(uploadFile.getInputStream());
			BufferedImage thumbnailImage = Thumbnailator.createThumbnail(originalImage, 100, 100);
			File thumbnailFile = new File(thumbnailSaveName);
			ImageIO.write(thumbnailImage, "png", thumbnailFile);
		}
	}

	public String makeFolder(Boolean thumbnailyn) {
		String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		String folderPath = str.replace("/", "/");
		// make folder------------
		String fullPath = null;
		if (thumbnailyn == true) {
			fullPath = uploadPath + "/" + folderPath + "/" + "thumbnails";
		} else {
			fullPath = uploadPath + "/" + folderPath;
		}
		File uploadPathFolder = new File(fullPath);
		
		if (uploadPathFolder.exists() == false) {
			uploadPathFolder.mkdirs();
			// 폴더 권한 변경 (drwxrwxrwx)
			if (uploadPathFolder.setExecutable(true, false) && uploadPathFolder.setReadable(true, false)
					&& uploadPathFolder.setWritable(true, false)) {
				System.out.println("폴더의 권한이 변경되었습니다.");
			} else {
				System.err.println("폴더 권한 변경에 실패했습니다.");
			}
		}

		return folderPath;
	}

	public void deleteOrphanImgs(Long wno) {
		// DB에 등록되지 않은 실제 이미지 파일을 제거
		List<String> existingFolders = imageRepository.findDistinctFolderPathsByWno(wno); // 지우기 선택한 글에 최소 한 개 이상 엔티티가
																							// 존재해야 로직 수행
		for (String folderPath : existingFolders) {

			String imagePath = folderPath;
			File imageFolder = new File(imagePath);
			File[] files = imageFolder.listFiles(file -> file.isFile());
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					String uuid = fileName.substring(0, fileName.indexOf('_'));
					boolean existsInDB = imageRepository.existsByUuid(uuid);
					if (!existsInDB) {
						file.delete();
						System.out.println("file : " + file + "is deleted.");
					}
				}
			}
		}
	}

	@Transactional
	private void saveImagesToDB(String portfolioContent, String title, String description, Category category,
			List<String> tools, Set<String> hashTag, WorkImage thumbnailImage, List<WorkImage> boardImages) {
		LocalDateTime now = LocalDateTime.now();
		WorkBoard board = WorkBoard.builder().portfolioContent(portfolioContent).title(title).description(description)
				.category(category).tools(tools).hashTag(hashTag).hits(0).build();
		board.setRegDate(now);

		WorkImage thumbnailWorkImage = new WorkImage();
		if (thumbnailImage != null) {
			String thumbnailName = thumbnailImage.getImgName();
			String thumbnailUuid = thumbnailImage.getUuid();
			String thumbnailPath = thumbnailImage.getPath();

			thumbnailWorkImage.setImgName(thumbnailName);
			thumbnailWorkImage.setUuid(thumbnailUuid);
			thumbnailWorkImage.setPath(thumbnailPath);

		} else if (thumbnailImage == null) {
			thumbnailWorkImage = null;
		}
		board.setWorkImages(Collections.singletonList(thumbnailWorkImage));

		boardRepository.save(board);

		if (boardImages != null && !boardImages.isEmpty()) {
		    for (WorkImage boardImage : boardImages) {
		        String boardImgName = boardImage.getImgName();
		        String boardImgUuid = boardImage.getUuid();
		        String boardImgPath = boardImage.getPath();

		        WorkImage boardWorkImage = new WorkImage();
		        boardWorkImage.setImgName(boardImgName);
		        boardWorkImage.setUuid(boardImgUuid);
		        boardWorkImage.setPath(boardImgPath);

		        boardWorkImage.setWorkBoard(board);
		        board.getWorkImages().add(boardWorkImage);
		    }

		    boardRepository.save(board);
		}

	}

	@Override
	public ResponseEntity<String> modify(MultipartFile thumbnailFile, Long wno, String portfolioContent, String title,
			WorkImage thumbnailImage, List<WorkImage> boardImages, String description, Category category,
			List<String> tools, Set<String> hashTag, Integer hits) {
		boardRepository.updateWorkBoard(wno, portfolioContent, title, description, category, hits);
		if(thumbnailImage != null) {
			imageRepository.deleteByPathEndingWithThumbnailsAndWno(wno);
		}
		try {
			if (thumbnailFile != null) {
				deletePreviousThumbnail(wno);
				saveThumbnailImg(thumbnailFile, thumbnailImage);
			}
			setWorkImage(wno, portfolioContent, title, description, category, tools, hashTag, hits, thumbnailImage,
					boardImages);
			saveToolsAndHashTag(wno, tools, hashTag);
		} catch (IOException e) {
			e.printStackTrace();
			String errorMessage = "Internal server error";
			return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Transactional
	@Override
	public ResponseEntity<Void> remove(Long wno) throws Exception {
		List<String[]> fileDirName = imageRepository.findPathAndUuidAndImgName(wno);
		for (int i = 0; i < fileDirName.size(); i++) {
			String[] filePaths = fileDirName.get(i);
			String filePath1 = filePaths[0];
			String filePath2 = filePaths[1];
			File imageFile1 = new File(filePath1); // 첫 번째 이후의 루프에서 내용만 삭제
			File imageFile2 = new File(filePath2); // 첫 번째 루프에서만 삭제

			if (i == 0) {
				if (imageFile2.delete()) {
					System.out.println(imageFile2 + " ::: Deleted.");
				} else {
					System.out.println("Failed to delete " + imageFile2);
				}
			} else if (i > 0) {
				if (imageFile1.delete()) {
					System.out.println(imageFile1 + " ::: Deleted.");
				} else {
					System.out.println("Failed to delete : " + imageFile1);
				}
			} else {
				System.out.println("Image file not found");
			}
		}

		boardRepository.deleteById(wno);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void setWorkImage(Long wno, String portfolioContent, String title, String description, Category category,
			List<String> tools, Set<String> hashTag, Integer hits, WorkImage thumbnailImage,
			List<WorkImage> boardImages) {
		WorkBoard board = boardRepository.findById(wno).orElseThrow();
		try {
		if (thumbnailImage != null) {

			List<WorkImage> existingWorkImages = new ArrayList<>();

			if (!existingWorkImages.isEmpty()) {
			    ((WorkImage) existingWorkImages).setImgName(thumbnailImage.getImgName());
			    ((WorkImage) existingWorkImages).setUuid(thumbnailImage.getUuid());
			    ((WorkImage) existingWorkImages).setPath(thumbnailImage.getPath());
			    ((WorkImage) existingWorkImages).setWorkBoard(board);
			} else {
			    WorkImage newWorkImage = new WorkImage();
			    newWorkImage.setImgName(thumbnailImage.getImgName());
			    newWorkImage.setUuid(thumbnailImage.getUuid());
			    newWorkImage.setPath(thumbnailImage.getPath());
			    newWorkImage.setWorkBoard(board);
			    existingWorkImages.add(newWorkImage);
			}

			imageRepository.saveAll(existingWorkImages);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (boardImages != null && !boardImages.isEmpty()) {
			for (WorkImage boardImage : boardImages) {
				String boardImgName = boardImage.getImgName();
				String boardImgUuid = boardImage.getUuid();
				String boardImgPath = boardImage.getPath();

				WorkImage boardWorkImage = new WorkImage();
				boardWorkImage.setImgName(boardImgName);
				boardWorkImage.setUuid(boardImgUuid);
				boardWorkImage.setPath(boardImgPath);

				boardWorkImage.setWorkBoard(board);
				board.getWorkImages().add(boardWorkImage);
			}

			boardRepository.save(board);
		}
	}

	private void saveToolsAndHashTag(Long wno, List<String> tools, Set<String> hashTag) {
		hashTag.removeIf(tag -> tag == null || tag.isEmpty() || tag.equals(""));
		WorkBoard board = boardRepository.findById(wno).orElse(null);
		if (board != null) {
			board.setTools(tools);
			board.setHashTag(hashTag);
			boardRepository.save(board);
		}
	}

	@Override
	public Long getCountWno() {
		Long countWno = boardRepository.countWno();
		return countWno;
	}

	@Override
	public boolean checkBoardExists(Long wno) {
		return boardRepository.existsByWno(wno);
	}

}