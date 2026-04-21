package com.ocp.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ocp.backend.dto.response.FavoriteDto;
import com.ocp.backend.entity.Course;
import com.ocp.backend.entity.Favorite;
import com.ocp.backend.entity.User;
import com.ocp.backend.repository.FavoriteRepository;
import com.ocp.backend.repository.UserRepository;
import com.ocp.backend.repository.CourseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /**
     * 取得使用者的收藏列表
     */
    public List<FavoriteDto> getFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * 加入收藏
     */
    @Transactional
    public FavoriteDto addFavorite(Long userId, Long courseId) {
        // 檢查是否已收藏
        if (favoriteRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("已收藏過此課程");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setCourse(course);

        Favorite saved = favoriteRepository.save(favorite);
        return mapToDto(saved);
    }

    /**
     * 移除收藏
     */
    @Transactional
    public boolean removeFavorite(Long userId, Long courseId) {
        int deletedCount = favoriteRepository.deleteByUserIdAndCourseId(userId, courseId);
        return deletedCount > 0;
    }

    /**
     * 轉換 Entity -> DTO
     */
    private FavoriteDto mapToDto(Favorite favorite) {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(favorite.getId());

        Course course = favorite.getCourse();
        if (course != null) {
            dto.setCourseId(course.getId());
            dto.setCourseName(course.getName());
            dto.setCourseImage(course.getImageUrl());
            dto.setCoursePrice(course.getPrice());
            dto.setInstructorName(course.getInstructorName());
        }

        return dto;
    }
}
