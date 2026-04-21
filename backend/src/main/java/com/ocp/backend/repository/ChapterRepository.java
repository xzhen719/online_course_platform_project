package com.ocp.backend.repository;

import com.ocp.backend.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // select * from Chapters where courseId = ? order by chapterIndex asc.
    List<Chapter> findByCourseIdOrderByChapterIndexAsc(Long courseId);

    int countByCourseId(Long courseId);

    // select Max(Chapter_index) from Chapters where courseId = ?.
    // 抓取課程目前最大章節 (若新增章節++)
    @Query("SELECT MAX(c.chapterIndex) FROM Chapter c WHERE c.course.id = :courseId")
    Optional<Integer> findMaxChapterIndexByCourseId(@Param("courseId") Long courseId);
}
