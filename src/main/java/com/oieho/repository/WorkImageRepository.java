package com.oieho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.oieho.entity.WorkImage;

@Repository
public interface WorkImageRepository extends JpaRepository<WorkImage, Long> {
	List<WorkImage> findAllByWorkBoardWno(Long wno);

	@Query("SELECT DISTINCT i.path FROM WorkImage i")
	List<String> findDistinctFolderPaths();

	@Query("SELECT DISTINCT i.path FROM WorkImage i WHERE i.workBoard.wno = :wno AND SUBSTRING(i.path, LENGTH(i.path) - 9) != 'thumbnails'")
	List<String> findDistinctFolderPathsByWno(@Param("wno") Long wno);

	boolean existsByUuid(String uuid);

	@Transactional
    @Modifying
    @Query("DELETE FROM WorkImage wi WHERE wi.imgName = :fileName")
    void deleteByImgName(@Param("fileName") String fileName);


	@Query("SELECT CONCAT(img.path, '/', img.uuid, '_', img.imgName), CONCAT(img.path, '/s_', img.uuid, '_', img.imgName) FROM WorkImage img WHERE img.workBoard.wno = :wno")
	List<String[]> findPathAndUuidAndImgName(@Param("wno") Long wno);

	@Transactional
	@Query("UPDATE WorkImage wi SET wi.workBoard.wno = (wi.workBoard.wno - 1) WHERE wi.workBoard.wno > :wno")
    @Modifying
	void shiftWorkImageWno(@Param("wno") Long wno);
}