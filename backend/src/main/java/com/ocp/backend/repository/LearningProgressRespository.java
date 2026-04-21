package com.ocp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ocp.backend.entity.LearningProgress;
import com.ocp.backend.entity.LearningStatus;

@Repository
public interface LearningProgressRespository extends JpaRepository<LearningProgress, Long> {

    // 檢查使用者是否完成章節
    Optional<LearningProgress> findByUserIdAndChapterId(Long userId, Long chapterId);

    // 用來計算使用者的學習進度
    long countByUserIdAndCourseIdAndStatus(Long userId, Long courseId, LearningStatus status);

    // 取得使用者在某課程已完成的所有章節進度
    List<LearningProgress> findByUserIdAndCourseIdAndStatus(Long userId, Long courseId, LearningStatus status);

}
