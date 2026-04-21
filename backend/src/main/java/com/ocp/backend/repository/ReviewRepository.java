package com.ocp.backend.repository;

import com.ocp.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    //Select * from Reviews where courseId = ? Order by createdAt Desc
    List<Review> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    //確認用戶是否已評論過
    //SELECT EXISTS (
    //SELECT 1
    //FROM `reviews`
    //WHERE `user_id` = ?
    //AND `course_id` = ?
    //) AS `exists`;
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
