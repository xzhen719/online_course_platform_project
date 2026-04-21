package com.ocp.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Header.Payload.Signature
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // 供外部呼叫產生Token的方法
    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        // 用Map來裝payload的claim(自定義欄位)
        // 現階段沒有自定義欄位，所以是空的。

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder() // header
                .setClaims(claims) // custom key-value pairs in payload
                .setSubject(subject) // 設定主體是誰 (username), 通常是用immutable的屬性
                .setIssuedAt(new Date(System.currentTimeMillis())) // issue Token的時間
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Token過期的時間
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Signature, 含密鑰和演篹法
                .compact(); // 組合成字串base64 encoded
    }

    // Token驗證
    public boolean validateToken(String token, String userEmail) {
        // 從 Token 解析出 email
        String emailInToken = extractUsername(token);

        // 檢查 1: Token 裡的 email 是否跟傳進來的人一樣？
        // 檢查 2: Token 是否過期？
        return (emailInToken.equals(userEmail) && !isTokenExpired(token));
    }

    // --- 工具方法 ---

    // 從 Token 中取出 Email (Subject)
    public String extractUsername(String token) {
        // 解密整個Claims object(JSON Map)
        Claims claims = extractAllClaims(token);
        // 取出subject屬性 {"sub": "user@example.com"}
        return claims.getSubject();
    }

    // 檢查 Token 是否過期
    private boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        Date expirationDate = claims.getExpiration();
        // 如果 過期時間 在 現在時間 之前 (before)，那就是過期了
        return expirationDate.before(new Date());
    }

    // 最核心的方法：解密 Token 取得所有內容
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()) // 用同一把鑰匙來驗證簽名
                .build()
                .parseClaimsJws(token) // 1.拆成h.p.s三部分 2. 驗證簽名 3. 確認時效性
                .getBody(); // 取得Claims object.
    }

    // 把字串密鑰轉成加密演算法需要的 Key物件
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
