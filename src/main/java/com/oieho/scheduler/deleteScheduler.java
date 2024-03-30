package com.oieho.scheduler;

import java.io.File;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oieho.repository.CountRepository;
import com.oieho.repository.DummyIPRepository;
import com.oieho.repository.RecoverPasswordRepository;
import com.oieho.repository.RefreshTokenRepository;
import com.oieho.repository.WorkImageRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@Transactional
public class deleteScheduler {

	@Value("${upload.path}") // application.properties의 변수
	private String uploadPath;

	private final DummyIPRepository dummyRepository;
	private final CountRepository countRepository;
	private final RecoverPasswordRepository recoverPasswordRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final WorkImageRepository imageRepository;

	@Scheduled(cron = "0 0 14 * * *")
	@Transactional
	public void deleteOnceaDay() {
		dummyRepository.deleteAllInBatch();
		countRepository.updateTodayZero();
	}

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deleteFindPasswordToken() {
		recoverPasswordRepository.deleteAllInBatch();
	}

	@Scheduled(cron = "0 1 14 * * *")
	@Transactional
	public void deleteInvalidRefreshToken() {
		refreshTokenRepository.deleteByToken();
	}

	@Scheduled(cron = "22 07 21 * * *")
	@Transactional
	public void deleteOrphanImgs() {
		List<String> existingFolders = imageRepository.findDistinctFolderPaths(); // 이미지가 저장된 폴더 경로들을 DB에서 가져옴
		for (String folderPath : existingFolders) {
			String imagePath = folderPath;
			System.out.println("imagePath::" + imagePath);
			File imageFolder = new File(imagePath);
			System.out.println("files::" + imageFolder);
			File[] files = imageFolder.listFiles(file -> file.isFile());
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					String uuid = fileName.substring(0, fileName.indexOf('_'));
					boolean existsInDB = imageRepository.existsByUuid(uuid);
					if (!existsInDB && !isThumbnailFile(fileName)) {
						file.delete();
					}
				}
			}
		}
	}

	private boolean isThumbnailFile(String fileName) {
		return fileName.startsWith("s_");
	}
}