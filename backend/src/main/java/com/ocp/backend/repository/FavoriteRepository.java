package com.ocp.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocp.backend.entity.Favorite;

import jakarta.transaction.Transactional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    // 檢查是否已收藏
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // 返回被刪除的列數
    @Transactional
    int deleteByUserIdAndCourseId(Long userId, Long courseId);
}
