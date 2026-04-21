package com.ocp.backend.filter;

import com.ocp.backend.service.CustomUserDetailsService;
import com.ocp.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 拿 Header (Authorization: Bearer <token>)
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 檢查 Header 格式對不對
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 不對就直接放行 (讓後面的 Security 擋它)
            return;
        }

        // 2. 切割字串，拿到 Token
        jwt = authHeader.substring(7); // 去掉 "Bearer " (7個字元)

        try {
            userEmail = jwtUtil.extractUsername(jwt); // 嘗試解密拿 Email

            // 3. 如果有 Email 且目前還沒登入 (Context 是空的)
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 去 DB 撈這個人的資料
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 驗證 Token 是否有效
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                    // 4. 建立「通行證」 (UsernamePasswordAuthenticationToken)
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());// ROLE

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. 確認OK則把人塞進 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            System.out.println("JWT Token validation failed: " + e.getMessage());
        }

        // 繼續往下走 (交給下一個 Filter)
        filterChain.doFilter(request, response);
    }
}
