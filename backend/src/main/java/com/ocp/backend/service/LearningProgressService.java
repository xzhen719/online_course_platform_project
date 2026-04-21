package com.ocp.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ocp.backend.dto.response.LearningProgressDto;
import com.ocp.backend.entity.Chapter;
import com.ocp.backend.entity.Coupon;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.LearningProgress;
import com.ocp.backend.entity.LearningStatus;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.ChapterRepository;
import com.ocp.backend.repository.LearningProgressRespository;
import com.ocp.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningProgressService {

    private final LearningProgressRespository learningProgressRespository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final CouponService couponService;

    @Transactional
    // 當使用者按下complete的時候, 後端不用回傳布林值isChapterCompleted (這是使用者自己決定的)
    // 而是要回傳計算課程進度條(eg. 3/5 in total)
    public LearningProgressDto markChapterAsCompleted(Long userId, Long chapterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("chapter not found"));
        Course course = chapter.getCourse();

        // 如果還沒有Progress的話就new一個出來
        LearningProgress progress = learningProgressRespository.findByUserIdAndChapterId(userId, chapterId)
                .orElse(new LearningProgress());

        // Check if already completed to avoid duplicate logic/coupons
        if (progress.getStatus() == LearningStatus.COMPLETED) {
            return calculateCourseCompletion(user, course.getId(), false);
        }

        // 承上, 若為新的progress的話, 先設定user, course, chapter.
        if (progress.getId() == null) {
            progress.setUser(user);
            progress.setCourse(course);
            progress.setChapter(chapter);
        }

        progress.setStatus(LearningStatus.COMPLETED);
        progress.setProgressPercent(100);
        progress.setCompletedAt(LocalDateTime.now());

        // 儲存progress
        learningProgressRespository.save(progress);

        return calculateCourseCompletion(user, course.getId(), true);
    }

    // 課程進度計算; triggerReward僅用來確認是否首次完成課程(避免重複發放優惠券)
    private LearningProgressDto calculateCourseCompletion(User user, Long courseId, boolean triggerReward) {

        long totalChapters = chapterRepository.countByCourseId(courseId);

        // 完成了課程中多少個chapter?
        long completedCount = learningProgressRespository.countByUserIdAndCourseIdAndStatus(user.getId(), courseId,
                LearningStatus.COMPLETED);

        // 防止totalChapters是0
        if (totalChapters == 0) {
            return new LearningProgressDto(false, 0, null);
        }
        int percent = (int) ((completedCount * 100) / totalChapters);
        boolean isComplete = (completedCount == totalChapters);

        // 兩個條件都符合才發放優惠券
        if (isComplete && triggerReward) {
            log.info("User {} has completed Course {}!", user.getId(), courseId);
            // Generate coupon and return its code to the frontend
            Coupon coupon = couponService.generateCourseCompletionCoupon(user, courseId);
            return new LearningProgressDto(true, percent, coupon.getCode());
        }
        return new LearningProgressDto(isComplete, percent, null);
    }

    // 取得使用者在某課程已完成的所有章節ID
    @Transactional
    public List<Long> getCompletedChapterIds(Long userId, Long courseId) {
        return learningProgressRespository
                .findByUserIdAndCourseIdAndStatus(userId, courseId, LearningStatus.COMPLETED)
                .stream()
                .map(progress -> progress.getChapter().getId())
                .toList();
    }
}
