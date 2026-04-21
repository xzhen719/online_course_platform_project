package com.ocp.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.backend.dto.response.FavoriteDto;
import com.ocp.backend.entity.User;
import com.ocp.backend.service.FavoriteService;
import com.ocp.backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final UserService userService;

    /**
     * 取得我的收藏列表
     * GET /api/favorites
     */
    @GetMapping
    public ResponseEntity<List<FavoriteDto>> getMyFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<FavoriteDto> favorites = favoriteService.getFavoritesByUserId(user.getId());
        return ResponseEntity.ok(favorites);
    }

    /**
     * 加入收藏
     * POST /api/favorites/add/{courseId}
     */
    @PostMapping("/add/{courseId}")
    public ResponseEntity<FavoriteDto> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        FavoriteDto favorite = favoriteService.addFavorite(user.getId(), courseId);
        return ResponseEntity.ok(favorite);
    }

    /**
     * 移除收藏
     * DELETE /api/favorites/remove/{courseId}
     */
    @DeleteMapping("/remove/{courseId}")
    public ResponseEntity<Boolean> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        boolean removed = favoriteService.removeFavorite(user.getId(), courseId);
        return ResponseEntity.ok(removed);
    }
}
