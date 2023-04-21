package com.techwave.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Component
public class JwtUtil {


    public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 1;  // 有效期1天
    public static final String JWT_KEY = "SDFKjhdsfals375HFdsjkdsfds12gkst131af695fac";

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }

        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        Map<String, Object> map = new HashMap<>();
        //map.put("role", role);
        map.put("subject", subject);

        return Jwts.builder()
                .setId(uuid)
                .setClaims(map)
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(secretKey, signatureAlgorithm)
                .setExpiration(expDate);
    }

    public static SecretKey generalKey() {
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
    }

    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static String getUserIdFromToken(String T_Token) {
        if (!StringUtils.hasText(T_Token) || !T_Token.startsWith("Bearer ")) {
            return null;
        }

        T_Token = T_Token.substring(7);

        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(T_Token);
            Map<String, Object> map = new HashMap<>(claims);
            userId = map.get("subject").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

    public static String getUserRoleFromToken(String T_Token) {
        if (!StringUtils.hasText(T_Token) || !T_Token.startsWith("Bearer ")) {
            return null;
        }

        T_Token = T_Token.substring(7);

        String role;
        try {
            Claims claims = JwtUtil.parseJWT(T_Token);
            Map<String, Object> map = new HashMap<>(claims);
            role = map.get("role").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return role;
    }
}
