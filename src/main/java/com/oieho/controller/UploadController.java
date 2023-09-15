package com.oieho.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oieho.entity.WorkImage;
import com.oieho.repository.WorkBoardRepository;
import com.oieho.repository.WorkImageRepository;
import com.oieho.service.WorkBoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class UploadController {
	private final WorkBoardService boardService;
	private final WorkBoardRepository boardRepository;
	private final WorkImageRepository imageRepository;

	@Value("${upload.path}") // application.properties의 변수
	private String uploadPath;
	
	@PostMapping("/imageUpload")
	public ResponseEntity<Map<String, Object>> uploadImg(@RequestParam Map<String, MultipartFile> fileMap) {
	    List<Map<String, Object>> resultList = new ArrayList<>();
	    String errorMessage = null;
        Long maxWno = boardService.getCountWno();
        if (maxWno == null) {
        	maxWno = 1L;
        }
        
	    for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
	        String fieldName = entry.getKey();
	        MultipartFile uploadFile = entry.getValue();

	        // 이미지 파일만 업로드 가능
	        if (!uploadFile.getContentType().startsWith("image")) {
	            log.warn("This file is not an image type: " + fieldName);
	            errorMessage = "Invalid file type";
	            break;
	        }

	        // 실제 파일 이름 (IE나 Edge는 전체 경로가 들어올 수 있으므로 파일명만 추출)
	        String originalName = uploadFile.getOriginalFilename();
	        // 날짜 폴더 생성
	        Boolean thumbnailyn = false;
	        String folderPath = boardService.makeFolder(thumbnailyn);

	        // UUID
	        String uuid = UUID.randomUUID().toString();

			// 저장할 파일 이름 중간에 "_"를 이용해서 구분
			String saveName = uploadPath + "/" + folderPath + "/" + uuid + "_" + originalName;
			Path savePath = Paths.get(saveName);

			try {
				uploadFile.transferTo(savePath);

				// 응답에 필요한 정보 추가
				String rootStr = "static/" + "boardImgs";
				Map<String, Object> fileData = new HashMap<>();
				fileData.put("url", rootStr + "/" + folderPath + "/" + uuid + "_" + originalName);
				fileData.put("name", originalName);
				fileData.put("size", uploadFile.getSize());
				System.out.println(fileData.get("url"));
				resultList.add(fileData);
			} catch (IOException e) {
				e.printStackTrace();
				// 업로드 실패 시 예외 처리
				errorMessage = "Internal server error";
				break;
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("errorMessage", errorMessage);
		response.put("result", resultList);

		HttpStatus status = (errorMessage != null) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
		return new ResponseEntity<>(response, status);
	}

	@PostMapping("/gettingUrlOnPortfolioImg")
	public ResponseEntity<Map<String, Object>> gettingUrlOnPortfolioImg(
			@RequestParam(name = "uploadFile") MultipartFile[] uploadFiles) throws Exception {
		Map<String, Object> fileData = new HashMap<>();
		for (MultipartFile uploadFile : uploadFiles) {
			// 이미지 파일만 업로드 가능
			if ((uploadFile.getContentType().startsWith("image") == false)) {
				log.warn("this file is not image type");
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			// 실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
			String originalName = uploadFile.getOriginalFilename();
			String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

			log.info("fileName: " + fileName);

			// 날짜 폴더 생성
	        Boolean thumbnailyn = false;
	        String folderPath = boardService.makeFolder(thumbnailyn);
	        String uuid = UUID.randomUUID().toString();
	        		
	        
			String saveName = uploadPath + "/" + folderPath + "/" + uuid + "_" + originalName;
			Path savePath = Paths.get(saveName);

			try {
				uploadFile.transferTo(savePath);}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
			
			String rootStr = "static/" + "boardImgs";
			fileData.put("url", rootStr + "/" + folderPath + "/" + uuid + "_" + originalName);
			fileData.put("name", originalName);
			fileData.put("size", uploadFile.getSize());
			fileData.put("path", rootStr + "/" + folderPath);
			fileData.put("uuid", uuid);

		}
		return new ResponseEntity<Map<String, Object>>(fileData, HttpStatus.OK);
	}

	// 함수로 세 자리 랜덤 영소 대문자와 숫자 생성
	private String generateRandomString() {
	    String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    StringBuilder sb = new StringBuilder();
	    Random random = new Random();

	    for (int i = 0; i < 3; i++) {
	        int index = random.nextInt(characters.length());
	        sb.append(characters.charAt(index));
	    }

	    return sb.toString();
	}

	@PostMapping("/generateThumbnailDir")
	public ResponseEntity<?> generateThumbnailDir(@RequestParam(name = "thumbnailFile") MultipartFile thumbnailFile)
			throws Exception {

		// 이미지 파일만 업로드 가능
		if (!thumbnailFile.getContentType().startsWith("image")) {
			log.warn("This file is not an image type");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		// 실제 파일 이름 (IE나 Edge는 전체 경로가 들어올 수 있으므로 파일명만 추출)
		String originalName = thumbnailFile.getOriginalFilename();
		String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

		log.info("fileName: " + fileName);

		// 날짜 폴더 생성
		Boolean thumbnailyn = true;
		String folderPath = boardService.makeFolder(thumbnailyn);

		// UUID
		String uuid = UUID.randomUUID().toString();

		// 썸네일 이미지 저장 경로
		String thumbnailSaveName = uploadPath + "/" + folderPath + "/" + "thumbnails"
				+ "/" + "s_" + uuid + "_" + fileName;

		return new ResponseEntity<String>(thumbnailSaveName, HttpStatus.OK);
	}
	
	@GetMapping("/display/{wno}")
	public ResponseEntity<byte[]> getFile(@PathVariable("wno") Long wno) {
	    try {
	        List<WorkImage> workImage = imageRepository.findAllByWorkBoardWno(wno);
	        if (workImage == null || workImage.isEmpty()) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        WorkImage image = workImage.get(0);
	        String path = image.getPath(); // 이미지 파일이 저장된 경로
	        String uuid = image.getUuid(); // 이미지 파일의 UUID
	        String imgName = image.getImgName(); // 이미지 파일의 이름
	        File file = new File(path + "/" + "s_" + uuid + "_" + imgName); // 파일 객체 생성
	        HttpHeaders header = new HttpHeaders();

	        // MIME 타입 처리
	        header.add("Content-Type", Files.probeContentType(file.toPath()));
	        // 파일 데이터 처리
	        byte[] fileData = FileCopyUtils.copyToByteArray(file);

	        return new ResponseEntity<>(fileData, header, HttpStatus.OK);
	    } catch (Exception e) {
	        log.error(e.getMessage());
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}


}
